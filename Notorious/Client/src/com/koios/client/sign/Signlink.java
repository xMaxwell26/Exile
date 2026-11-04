package com.koios.client.sign;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Signlink implements Runnable {

	public static void startpriv(InetAddress inetaddress) {
		threadliveid = (int) (Math.random() * 99999999D);
		if (active) {
			try {
				Thread.sleep(500L);
			} catch (Exception _ex) {
			}
			active = false;
		}
		socketreq = 0;
		threadreq = null;
		dnsreq = null;
		savereq = null;
		urlreq = null;
		socketip = inetaddress;
		Thread thread = new Thread(new Signlink());
		thread.setDaemon(true);
		thread.start();
		while (!active) {
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}
		}
	}

	/**
	 * This Platform enum contains the different operative systems we are
	 * expecting to deal with.
	 */
	private static enum Platform {
		LINUX, MAC_OS_X, UNKOWN, WINDOWS;
	}

	/**
	 * Internal function to determine the {@code Platform} type.
	 *
	 * @return the {@code Platform} this machine is running.
	 */
	private static Platform getPlatform() {
		final String name = System.getProperty("os.name").toLowerCase();

		if (name.contains("win")) {
			return Platform.WINDOWS;
		} else if (name.contains("mac")) {
			return Platform.MAC_OS_X;
		} else if (name.contains("linux")) {
			return Platform.LINUX;
		} else if (name.contains("unix")) {
			return Platform.LINUX;
		}

		return Platform.UNKOWN;
	}

	public void run() {
		active = true;
		uid = getuid(findcachedir());
		try {
			cache_dat = new RandomAccessFile(findcachedir() + "main_file_cache.dat", "rw");
			for (int j = 0; j < 5; j++) {
				cache_idx[j] = new RandomAccessFile(findcachedir() + "main_file_cache.idx" + j, "rw");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		for (int i = threadliveid; threadliveid == i;) {
			if (socketreq != 0) {
				try {
					socket = new Socket(socketip, socketreq);
				} catch (Exception _ex) {
					socket = null;
				}
				socketreq = 0;
			} else if (threadreq != null) {
				Thread thread = new Thread(threadreq);
				thread.setDaemon(true);
				thread.start();
				thread.setPriority(threadreqpri);
				threadreq = null;
			} else if (dnsreq != null) {
				try {
					dns = InetAddress.getByName(dnsreq).getHostName();
				} catch (Exception _ex) {
					dns = "unknown";
				}
				dnsreq = null;
			} else if (savereq != null) {
				if (savebuf != null)
					try {
						FileOutputStream fileoutputstream = new FileOutputStream(findcachedir() + savereq);
						fileoutputstream.write(savebuf, 0, savelen);
						fileoutputstream.close();
					} catch (Exception _ex) {
					}
				if (waveplay) {
					waveplay = false;
				}
				if (midiplay) {
					midi = findcachedir() + savereq;
					midiplay = false;
				}
				savereq = null;
			} else if (urlreq != null) {
				try {
					System.out.println("urlstream");
					urlstream = new DataInputStream((new URL(mainapp.getCodeBase(), urlreq)).openStream());
				} catch (Exception _ex) {
					urlstream = null;
				}
				urlreq = null;
			}
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}
		}
	}

	public static String findcachedir() {
		// return "./Cache/";
		Path path = Paths.get(System.getProperty("user.home") + File.separator + "koios-cache" + File.separator);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String directory = path.toFile().getPath();
		return directory + File.separator;
	}

	private static int getuid(String s) {
		try {
			File file = new File(s + "uid.dat");
			if (!file.exists() || file.length() < 4L) {
				DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(s + "uid.dat"));
				dataoutputstream.writeInt((int) (Math.random() * 99999999D));
				dataoutputstream.close();
			}
		} catch (Exception _ex) {
		}
		try {
			DataInputStream datainputstream = new DataInputStream(new FileInputStream(s + "uid.dat"));
			int i = datainputstream.readInt();
			datainputstream.close();
			return i + 1;
		} catch (Exception _ex) {
			return 0;
		}
	}

	public static synchronized Socket opensocket(int i) throws IOException {
		for (socketreq = i; socketreq != 0;)
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}

		if (socket == null)
			throw new IOException("could not open socket");
		else
			return socket;
	}

	public static synchronized DataInputStream openurl(String s) throws IOException {
		for (urlreq = s; urlreq != null;)
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}

		if (urlstream == null)
			throw new IOException("could not open: " + s);
		else
			return urlstream;
	}

	public static synchronized void dnslookup(String s) {
		dns = s;
		dnsreq = s;
	}

	public static synchronized void startthread(Runnable runnable, int i) {
		threadreqpri = i;
		threadreq = runnable;
	}

	public static synchronized boolean wavesave(byte abyte0[], int i) {
		if (i > 0x1e8480)
			return false;
		if (savereq != null) {
			return false;
		} else {
			wavepos = (wavepos + 1) % 5;
			savelen = i;
			savebuf = abyte0;
			waveplay = true;
			savereq = "sound" + wavepos + ".wav";
			return true;
		}
	}

	public static synchronized boolean wavereplay() {
		if (savereq != null) {
			return false;
		} else {
			savebuf = null;
			waveplay = true;
			savereq = "sound" + wavepos + ".wav";
			return true;
		}
	}

	public static synchronized void midisave(byte abyte0[], int i) {
		if (i > 0x1e8480)
			return;
		if (savereq != null) {
		} else {
			midipos = (midipos + 1) % 5;
			savelen = i;
			savebuf = abyte0;
			midiplay = true;
			savereq = "jingle" + midipos + ".mid";
		}
	}

	public static void reporterror(String s) {
		System.out.println("Error: " + s);
	}

	public static String getSerialNumber() {

		String sn = null;
		OutputStream os = null;
		InputStream is = null;
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		
		switch (platform) {
		
		case MAC_OS_X:

			try {
				process = runtime.exec(new String[] { "/usr/sbin/system_profiler", "SPHardwareDataType" });
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			os = process.getOutputStream();
			is = process.getInputStream();

			try {
				os.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			String marker = "Serial Number";
			try {
				while ((line = br.readLine()) != null) {
					if (line.contains(marker)) {
						sn = line.split(":")[1].trim();
						break;
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			if (sn == null) {
				throw new RuntimeException("Cannot find computer SN");
			}

			return sn;
			
		case LINUX:
			if (sn == null) {
				sn = readDmidecode();
			}
			if (sn == null) {
				sn = readLshal();
			}
			if (sn == null) {
				throw new RuntimeException("Cannot find computer SN");
			}
			return sn;
			
		case WINDOWS:
			
			  String result = "";
			    try {
			      File file = File.createTempFile("realhowto",".vbs");
			      file.deleteOnExit();
			      FileWriter fw = new java.io.FileWriter(file);

			      String vbs =
			         "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
			        + "Set colItems = objWMIService.ExecQuery _ \n"
			        + "   (\"Select * from Win32_BaseBoard\") \n"
			        + "For Each objItem in colItems \n"
			        + "    Wscript.Echo objItem.SerialNumber \n"
			        + "    exit for  ' do the first cpu only! \n"
			        + "Next \n";

			      fw.write(vbs);
			      fw.close();
			      Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
			      BufferedReader input =
			        new BufferedReader
			          (new InputStreamReader(p.getInputStream()));
			      String l;
			      while ((l = input.readLine()) != null) {
			         result += l;
			      }
			      input.close();
			    }
			    catch(Exception e){
			        e.printStackTrace();
			    }
			    System.out.println(result.trim());
			return result.trim();
		default:
			return "empty_serial";
		}
	}

	private static BufferedReader read(String command) {

		OutputStream os = null;
		InputStream is = null;

		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec(command.split(" "));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		os = process.getOutputStream();
		is = process.getInputStream();

		try {
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new BufferedReader(new InputStreamReader(is));
	}

	private static String readDmidecode() {

		String line = null;
		String marker = "Serial Number:";
		BufferedReader br = null;
		String serial = null;

		try {
			br = read("dmidecode -t system");
			while ((line = br.readLine()) != null) {
				if (line.indexOf(marker) != -1) {
					serial = line.split(marker)[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return serial;
	}

	private static String readLshal() {

		String line = null;
		String marker = "system.hardware.serial =";
		BufferedReader br = null;
		String serial = null;

		try {
			br = read("lshal");
			while ((line = br.readLine()) != null) {
				if (line.indexOf(marker) != -1) {
					serial = line.split(marker)[1].replaceAll("\\(string\\)|(\\')", "").trim();
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return serial;
	}

	private Signlink() {
		
	}
	
	static {
		platform = getPlatform();
		serialNumber = "";//getSerialNumber();
	}

	public static final int clientversion = 317;
	public static int uid;
	public static int storeid = 32;
	public static RandomAccessFile cache_dat = null;
	public static final RandomAccessFile[] cache_idx = new RandomAccessFile[5];
	public static boolean sunjava;
	public static Applet mainapp = null;
	private static boolean active;
	private static int threadliveid;
	private static InetAddress socketip;
	private static int socketreq;
	private static Socket socket = null;
	private static int threadreqpri = 1;
	private static Runnable threadreq = null;
	private static String dnsreq = null;
	public static String dns = null;
	private static String urlreq = null;
	private static DataInputStream urlstream = null;
	private static int savelen;
	private static String savereq = null;
	private static byte[] savebuf = null;
	private static boolean midiplay;
	private static int midipos;
	public static String midi = null;
	public static int midivol;
	public static int midifade;
	private static boolean waveplay;
	private static int wavepos;
	public static int wavevol;
	public static boolean reporterror = true;
	public static String errorname = "";
	public static final String serialNumber;
	public static final Platform platform;
}