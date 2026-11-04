package com.koios.network.mysql;

import com.koios.GameService;
import com.koios.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import org.sql2o.Connection;

/**
 * Small callback receiver for vote sites. It records votes only; it never
 * grants items, currency, levels, or other in-game rewards.
 *
 * Configure with EXILE_VOTE_BIND, EXILE_VOTE_PORT and EXILE_VOTE_TOKEN.
 * Keep the listener behind a reverse proxy/firewall in production.
 */
public final class VoteCallbackServer {
    private final HttpServer server;
    private final String token;

    private VoteCallbackServer(HttpServer server, String token) {
        this.server = server;
        this.token = token;
    }

    public static VoteCallbackServer start(boolean onlineMode) {
        if (!onlineMode || !enabled()) return null;
        try {
            String bind = value("EXILE_VOTE_BIND", "127.0.0.1");
            int port = Integer.parseInt(value("EXILE_VOTE_PORT", "8088"));
            String token = value("EXILE_VOTE_TOKEN", "");
            if (token.length() < 16) throw new IllegalStateException("EXILE_VOTE_TOKEN must be at least 16 characters");
            HttpServer http = HttpServer.create(new InetSocketAddress(bind, port), 32);
            VoteCallbackServer callback = new VoteCallbackServer(http, token);
            http.createContext("/health", callback::health);
            http.createContext("/vote", callback::vote);
            http.setExecutor(Executors.newFixedThreadPool(2));
            http.start();
            System.out.println("Vote callback listening at http://" + bind + ":" + port + "/vote");
            return callback;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to start vote callback", ex);
        }
    }

    private static boolean enabled() { return "true".equalsIgnoreCase(value("EXILE_VOTE_CALLBACK", "false")); }
    private static String value(String key, String fallback) { String v = System.getenv(key); return v == null || v.trim().isEmpty() ? fallback : v.trim(); }
    private void health(HttpExchange exchange) throws IOException { respond(exchange, 200, "OK"); }

    private void vote(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { respond(exchange, 405, "POST required"); return; }
        String supplied = exchange.getRequestHeaders().getFirst("X-Exile-Vote-Token");
        if (!token.equals(supplied)) { respond(exchange, 401, "Unauthorized"); return; }
        String body = readBody(exchange.getRequestBody());
        String player = field(body, "player");
        String site = field(body, "site");
        String externalId = field(body, "id");
        if (player.isEmpty() || site.isEmpty() || externalId.isEmpty()) { respond(exchange, 400, "player, site and id are required"); return; }
        try (Connection connection = GameService.getConnection()) {
            connection.createQuery("INSERT INTO exile_votes (player_name, site, external_id, payload) VALUES (:player, :site, :id, :payload) ON DUPLICATE KEY UPDATE last_seen_at=CURRENT_TIMESTAMP, payload=:payload")
                    .addParameter("player", player).addParameter("site", site).addParameter("id", externalId).addParameter("payload", body).executeUpdate();
            respond(exchange, 202, "Recorded");
        } catch (Exception ex) {
            ex.printStackTrace();
            respond(exchange, 503, "Database unavailable");
        }
    }

    private static String field(String body, String key) {
        String marker = "\"" + key + "\"";
        int at = body.indexOf(marker); if (at < 0) return "";
        int colon = body.indexOf(':', at + marker.length()); if (colon < 0) return "";
        int start = body.indexOf('"', colon + 1); if (start < 0) return "";
        int end = body.indexOf('"', start + 1); return end < 0 ? "" : body.substring(start + 1, end).replace("\\\"", "\"");
    }
    private static String readBody(InputStream input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048]; int read; int total = 0;
        while ((read = input.read(buffer)) >= 0) { total += read; if (total > 65536) throw new IOException("Vote payload too large"); out.write(buffer, 0, read); }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
    private static void respond(HttpExchange exchange, int code, String text) throws IOException { byte[] data = text.getBytes(StandardCharsets.UTF_8); exchange.sendResponseHeaders(code, data.length); try (OutputStream out = exchange.getResponseBody()) { out.write(data); } }
    public void stop() { server.stop(0); }
}
