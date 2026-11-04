package com.exile.launcher;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;

/** Minimal self-updating launcher. The manifest is hosted beside the release ZIP. */
public final class ExileLauncher {
    private static final String DEFAULT_MANIFEST = "https://YOUR-DOMAIN.example/exile/manifest.json";
    private static final Path HOME = Paths.get(System.getProperty("user.home"), ".exile");
    public static void main(String[] args) throws Exception {
        String manifestUrl = System.getProperty("exile.manifest", env("EXILE_MANIFEST_URL", DEFAULT_MANIFEST));
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
    private static String required(String json,String key){String marker="\""+key+"\"";int p=json.indexOf(marker);if(p<0)throw new IllegalArgumentException("Manifest missing "+key);int c=json.indexOf(':',p);int q=json.indexOf('"',c);if(q>=0){int e=json.indexOf('"',q+1);return json.substring(q+1,e);}int e=c+1;while(e<json.length()&&",}\r\n ".indexOf(json.charAt(e))>=0)e++;int end=e;while(end<json.length()&&",}\r\n ".indexOf(json.charAt(end))<0)end++;return json.substring(e,end);}
    private static void download(URL url,Path target)throws IOException{HttpURLConnection c=(HttpURLConnection)url.openConnection();c.setConnectTimeout(10000);c.setReadTimeout(120000);c.setRequestProperty("User-Agent","ExileLauncher/1");try(InputStream in=c.getInputStream();OutputStream out=Files.newOutputStream(target)){byte[] b=new byte[8192];int n;while((n=in.read(b))>=0)out.write(b,0,n);}}
    private static String read(URL url)throws IOException{ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream in=url.openStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))>=0)out.write(b,0,n);}return new String(out.toByteArray(),StandardCharsets.UTF_8);}
    private static String sha256(Path path)throws Exception{MessageDigest d=MessageDigest.getInstance("SHA-256");try(InputStream in=Files.newInputStream(path)){byte[] b=new byte[8192];int n;while((n=in.read(b))>=0)d.update(b,0,n);}StringBuilder s=new StringBuilder();for(byte b:d.digest())s.append(String.format("%02x",b));return s.toString();}
    private static void unzip(Path zip,Path target)throws IOException{try(ZipInputStream in=new ZipInputStream(Files.newInputStream(zip))){ZipEntry e;while((e=in.getNextEntry())!=null){Path out=target.resolve(e.getName()).normalize();if(!out.startsWith(target))throw new IOException("Unsafe archive path");if(e.isDirectory())Files.createDirectories(out);else{Files.createDirectories(out.getParent());try(OutputStream os=Files.newOutputStream(out)){byte[] b=new byte[8192];int n;while((n=in.read(b))>=0)os.write(b,0,n);}}}}}
    private static String javaBin(){String home=System.getProperty("java.home");return Paths.get(home,"bin",System.getProperty("os.name").startsWith("Windows")?"java.exe":"java").toString();}
}
