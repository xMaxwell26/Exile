import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Loader
extends JFrame {
    private static final long serialVersionUID = 1;
    private static final String CLIENT_URL = "http://cdn.koiosps.com/Koios.jar";
    public static final String SERVER_NAME = "Koios";
    private static final String CACHE_PATH = String.valueOf(System.getProperty("user.home")) + File.separator + ".Koios_Launcher" + File.separator;
    private static final String VERSION_FILE = String.valueOf(CACHE_PATH) + "clientVersion.dat";
    private static GUI g;

    @SuppressWarnings("resource")
	public static long getCurrentVersion() throws NumberFormatException, FileNotFoundException, IOException {
        block3 : {
            try {
                File versionDir = new File(VERSION_FILE);
                if (versionDir.exists()) break block3;
                versionDir.createNewFile();
                return -1;
            }
            catch (Exception e) {
                return -1;
            }
        }
        return Long.parseLong(new BufferedReader(new InputStreamReader(new FileInputStream(VERSION_FILE))).readLine());
    }

    public static long getNewestVersion() {
        try {
            return ((HttpURLConnection)new URL(CLIENT_URL).openConnection()).getContentLengthLong();
        }
        catch (Exception e) {
            Loader.handleException(e);
            return -1;
        }
    }

    private static void handleException(Exception e) {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("Please Screenshot this message, and send it to a " + SERVER_NAME + " administrator!\r\n\r\n");
        strBuff.append(String.valueOf(e.getClass().getName()) + " \"" + e.getMessage() + "\"\r\n");
        StackTraceElement[] arrstackTraceElement = e.getStackTrace();
        int n = arrstackTraceElement.length;
        int n2 = 0;
        while (n2 < n) {
            StackTraceElement s = arrstackTraceElement[n2];
            strBuff.append(String.valueOf(s.toString()) + "\r\n");
            ++n2;
        }
        Loader.alert("Exception [" + e.getClass().getSimpleName() + "]", strBuff.toString(), true);
    }

    private static void alert(String title, String msg, boolean error) {
        JOptionPane.showMessageDialog(null, msg, title, error ? 0 : -1);
    }

    public static final void main(String[] args) {
        try {
            File file = new File(CACHE_PATH);
            if (!file.exists()) {
                file.mkdir();
            }
            long currentVersion = Loader.getCurrentVersion();
            long newestVersion = Loader.getNewestVersion();
            if (newestVersion != currentVersion) {
                int nRead;
                g = new GUI();
                g.setLocationRelativeTo(null);
                g.setVisible(true);
                InputStream inStream = new URL(CLIENT_URL).openStream();
                BufferedInputStream bufIn = new BufferedInputStream(inStream);
                File fileWrite = new File(String.valueOf(CACHE_PATH) + "Koios.jar");
                FileOutputStream out = new FileOutputStream(fileWrite);
                BufferedOutputStream bufOut = new BufferedOutputStream(out);
                byte[] buffer = new byte[1024];
                while ((nRead = bufIn.read(buffer, 0, buffer.length)) > 0) {
                    bufOut.write(buffer, 0, nRead);
                }
                bufOut.flush();
                out.close();
                inStream.close();
                FileOutputStream fileOutputStream = new FileOutputStream(VERSION_FILE);
				fileOutputStream.write(String.valueOf(newestVersion).getBytes());
				fileOutputStream.close();
                g.dispose();
            }
            new ProcessBuilder("java", "-jar", String.valueOf(CACHE_PATH) + "Koios.jar").start();
        }
        catch (Exception e) {
            Loader.handleException(e);
        }
    }
}

