package com.exile.launcher;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;
import javax.swing.JOptionPane;

/** Minimal self-updating launcher. The manifest is hosted beside the release ZIP. */
public final class ExileLauncher {
    private static final String DEFAULT_MANIFEST = "https://YOUR-DOMAIN.example/exile/manifest.json";
    private static final Path HOME = Paths.get(System.getProperty("user.home"), ".exile");
    public static void main(String[] args) throws Exception {
        try { run(args); } catch (Throwable failure) {
            failure.printStackTrace();
            String message = failure.getClass().getSimpleName()+": "+(failure.getMessage()==null?failure.toString():failure.getMessage());
            try { JOptionPane.showMessageDialog(null, message, "Exile Launcher", JOptionPane.ERROR_MESSAGE); } catch (Throwable ignored) { }
        }
    }
    private static void run(String[] args) throws Exception {
        String embedded = embeddedManifest();
        String manifestUrl = System.getProperty("exile.manifest", env("EXILE_MANIFEST_URL", embedded));
        if (manifestUrl.contains("YOUR-DOMAIN")) throw new IllegalStateException("Set EXILE_MANIFEST_URL or -Dexile.manifest");
        String json = read(new URL(manifestUrl));
        String version = required(json, "version"), host = required(json, "host"), sha = required(json, "sha256");
        int port = Integer.parseInt(required(json, "port"));
        Path release = HOME.resolve("releases").resolve(version), clientJar = release.resolve("client.jar");
        if (!Files.isRegularFile(clientJar) || !sha256(clientJar).equalsIgnoreCase(sha)) {
            Files.createDirectories(release);
            Path temp = Files.createTempFile(HOME, "exile-", ".zip");
            download(new URL(required(json, "url")), temp);
            unzip(temp, release); Files.deleteIfExists(temp);
            if (!Files.isRegularFile(clientJar) || !sha256(clientJar).equalsIgnoreCase(sha)) throw new IOException("Client integrity check failed");
        }
        Files.write(HOME.resolve("current.properties"), Arrays.asList("version="+version,"host="+host,"port="+port), StandardCharsets.UTF_8);
        Process process = new ProcessBuilder(javaBin(), "-Xmx1G", "-Darpg.serverHost="+host, "-Darpg.serverPort="+port, "-cp", clientJar.toString(), "com.koios.client.Client").directory(release.toFile()).inheritIO().start();
        if (Boolean.getBoolean("exile.wait")) process.waitFor();
    }
    private static String env(String key,String fallback){String v=System.getenv(key);return v==null||v.trim().isEmpty()?fallback:v.trim();}
    private static String embeddedManifest() {
        try (InputStream in = ExileLauncher.class.getResourceAsStream("/launcher.properties")) {
            if (in == null) return DEFAULT_MANIFEST;
            Properties p = new Properties(); p.load(in); return p.getProperty("manifest", DEFAULT_MANIFEST).trim();
        } catch (IOException ignored) { return DEFAULT_MANIFEST; }
    }
    private static String required(String json,String key){String marker="\""+key+"\"";int p=json.indexOf(marker);if(p<0)throw new IllegalArgumentException("Manifest missing "+key);int c=json.indexOf(':',p);if(c<0)throw new IllegalArgumentException("Manifest missing value "+key);int start=c+1;while(start<json.length()&&Character.isWhitespace(json.charAt(start)))start++;if(start<json.length()&&json.charAt(start)=='\"'){int end=json.indexOf('"',start+1);if(end<0)throw new IllegalArgumentException("Unterminated manifest value "+key);return json.substring(start+1,end);}int end=start;while(end<json.length()&&json.charAt(end)!=','&&json.charAt(end)!='}')end++;return json.substring(start,end).trim();}
    private static void download(URL url,Path target)throws IOException{HttpURLConnection c=(HttpURLConnection)url.openConnection();c.setConnectTimeout(10000);c.setReadTimeout(120000);c.setRequestProperty("User-Agent","ExileLauncher/1");try(InputStream in=c.getInputStream();OutputStream out=Files.newOutputStream(target)){byte[] b=new byte[8192];int n;while((n=in.read(b))>=0)out.write(b,0,n);}}
    private static String read(URL url)throws IOException{HttpURLConnection c=(HttpURLConnection)url.openConnection();c.setInstanceFollowRedirects(true);c.setConnectTimeout(10000);c.setReadTimeout(30000);c.setRequestProperty("User-Agent","ExileLauncher/1");try(InputStream in=c.getInputStream()){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))>=0)out.write(b,0,n);return new String(out.toByteArray(),StandardCharsets.UTF_8);}}
    private static String sha256(Path path)throws Exception{MessageDigest d=MessageDigest.getInstance("SHA-256");try(InputStream in=Files.newInputStream(path)){byte[] b=new byte[8192];int n;while((n=in.read(b))>=0)d.update(b,0,n);}StringBuilder s=new StringBuilder();for(byte b:d.digest())s.append(String.format("%02x",b));return s.toString();}
    private static void unzip(Path zip,Path target)throws IOException{try(ZipInputStream in=new ZipInputStream(Files.newInputStream(zip))){ZipEntry e;while((e=in.getNextEntry())!=null){Path out=target.resolve(e.getName()).normalize();if(!out.startsWith(target))throw new IOException("Unsafe archive path");if(e.isDirectory())Files.createDirectories(out);else{Files.createDirectories(out.getParent());try(OutputStream os=Files.newOutputStream(out)){byte[] b=new byte[8192];int n;while((n=in.read(b))>=0)os.write(b,0,n);}}}}}
    private static String javaBin(){String home=System.getProperty("java.home");return Paths.get(home,"bin",System.getProperty("os.name").startsWith("Windows")?"java.exe":"java").toString();}
}
