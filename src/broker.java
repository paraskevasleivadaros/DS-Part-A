import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class broker {

	public static Hashtable<String, ArrayList<String>> br_bus;
	public static Hashtable<String, String> match;
	public static String IP = "localhost";
	//public static String path = Paths.get("brokers.txt").toAbsolutePath().toString();
	public static String port;

	public static void main(String[] args) throws IOException {
		port = args[0];

		String[] busLines = {"021", "022", "024", "025", "026", "027", "032", "036", "040", "046", "049", "051", "054", "057", "060", "1", "10"};

		ArrayList<String> br_hash;

		try {
			br_hash = hashIPandPort();

			System.out.println(br_hash);

			ArrayList<String> br1_bus = new ArrayList<>();
			ArrayList<String> br2_bus = new ArrayList<>();
			ArrayList<String> br3_bus = new ArrayList<>();

			for (String busLine : busLines) {
				if (SHA1(busLine).compareTo(br_hash.get(0)) == -1 || SHA1(busLine).compareTo(br_hash.get(2)) == 1) {
					br1_bus.add(busLine);
				} else if (SHA1(busLine).compareTo(br_hash.get(1)) == -1) {
					br2_bus.add(busLine);
				} else if (SHA1(busLine).compareTo(br_hash.get(2)) == -1) {
					br3_bus.add(busLine);
				}
			}

			br_bus.put(match.get(br_hash.get(0)), br1_bus);
			br_bus.put(match.get(br_hash.get(1)), br2_bus);
			br_bus.put(match.get(br_hash.get(2)), br3_bus);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		new broker().openServer();
	}

	public void openServer() throws IOException {
		ServerSocket providerSocket = new ServerSocket(Integer.parseInt(port));
		Socket connection;

		while (true) {
			connection = providerSocket.accept();

			new myThread(connection).start();
		}
	}

	private class myThread extends Thread {
		Socket socket;

		public myThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			PrintStream out = null;
			Scanner in = null;

			Socket requestSocket = null;
			PrintStream p_out = null;
			Scanner p_in = null;

			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());

				requestSocket = new Socket(IP, 1871);
				p_out = new PrintStream(requestSocket.getOutputStream());
				p_in = new Scanner(requestSocket.getInputStream());

				String sub_msg;
				String pub_msg;

				out.println(br_bus.get(socket));
				//System.out.println(br_bus.get(socket));

				sub_msg = in.nextLine();
				for (int i = 1; i < 3; i++)
					out.println(br_bus.get(match.get(i)));
				p_out.println(sub_msg);
				p_out.flush();

				do {
					pub_msg = in.nextLine();

					out.println(pub_msg);
					out.flush();
				} while (in.nextLine().compareTo("stop") != 0);

				out.println(pub_msg);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				in.close();
				out.close();
				this.socket.close();
				p_in.close();
				p_out.close();
				requestSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> hashIPandPort() throws NoSuchAlgorithmException {
		try {
			FileReader in = new FileReader("brokers.txt");
			BufferedReader br = new BufferedReader(in);
			ArrayList<String> hashed = new ArrayList<>();
			String line;

			final DatagramSocket socket = new DatagramSocket();
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			String myIP = socket.getLocalAddress().getHostAddress();

			for (int i=0; i<3; i++) {
				line = myIP + br.readLine();
				hashed.add(SHA1(line));
				match.put(SHA1(line), line);
			}
			in.close();
			Collections.sort(hashed);
			return hashed;
		} catch (IOException e) {
			System.out.println("File Read Error");
			return null;
		}
	}

	public static String SHA1(String s) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		mDigest.update(s.getBytes(), 0, s.length());
		return new BigInteger(1, mDigest.digest()).toString();
	}
}