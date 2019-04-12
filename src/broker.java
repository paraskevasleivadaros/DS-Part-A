import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Scanner;

public class broker {
	
	private static Hashtable <String, ArrayList<String>> br_bus;
	private static Hashtable <String, String> match;
	private static String path = Paths.get("brokers.txt").toAbsolutePath().toString();
	private static String port;
	private static String[] busLines = {"1151", "821", "750", "817", "818", "974", "1113", "816", "804", "1219", "1220", "938", "831", "819", "1180", "868", "824", "825", "1069", "1077"};
    private static String[] busLinesCon = {"021", "022", "024", "025", "026", "027", "032", "036", "040", "046", "049", "051", "054", "057", "060", "1", "10"};

    public static void main(String[] args) throws IOException {

		port = args[0];
		ArrayList br_hash;

		try {
			br_hash = hashIPandPort();
			
			ArrayList br1_bus = new ArrayList();
			ArrayList br2_bus = new ArrayList();
			ArrayList br3_bus = new ArrayList();

			for (String busLine : busLines) {
				if (br_hash != null) {
					if (SHA1(busLine).compareTo((String) br_hash.get(0)) == -1) {
						br1_bus.add(busLine);
					} else if (SHA1(busLine).compareTo((String) br_hash.get(1)) == -1) {
						br2_bus.add(busLine);
					} else if (SHA1(busLine).compareTo((String) br_hash.get(2)) == -1) {
						br3_bus.add(busLine);
					} else {
						br1_bus.add(busLine);
					}
				}
			}
			
			br_bus = new Hashtable <>();

			br_bus.put(match.get(br_hash.get(0)), br1_bus);
			br_bus.put(match.get(br_hash.get(1)), br2_bus);
			br_bus.put(match.get(br_hash.get(2)), br3_bus);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		new broker().openServer();
	}
	
	private void openServer() throws IOException {
		ServerSocket providerSocket = new ServerSocket(Integer.parseInt(port));
		Socket connection;
		
		while(true) {
			connection = providerSocket.accept();
			new myThread(connection).start();
		}
	}
	
	private class myThread extends Thread {
		Socket socket;
		
		myThread(Socket socket) {
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
				
				out.println(br_bus.toString());

				String IP = "192.168.1.7";
				requestSocket = new Socket(IP, 1871);
				p_out = new PrintStream(requestSocket.getOutputStream());
				p_in = new Scanner(requestSocket.getInputStream());
				
				String sub_msg;
				String pub_msg;
				
				sub_msg = in.nextLine();
				for (int i = 0 ; i < busLinesCon.length; i++){
				   if(sub_msg.equals(busLinesCon[i])) {
					   sub_msg = busLines[i];
					   break;
				   }
				}

				p_out.println(sub_msg);
				p_out.flush();

				do {
					pub_msg = p_in.nextLine();
					out.println(pub_msg);
					out.flush();
				} while (p_in.nextLine().compareTo("stop") != 0);

				out.println(pub_msg);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if (in != null) in.close();
				if (out != null) out.close();
				this.socket.close();
				if (p_in != null) p_in.close();
				if (p_out != null) p_out.close();
				if (requestSocket != null) requestSocket.close();
			} catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	/*private class myThread extends Thread {
		Socket socket;
		
		public myThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			PrintStream out;
			Scanner in;
			ArrayList busIDs;
			ArrayList brokerLines = new ArrayList();
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
				
				busIDs = readBusLines();
				
				System.out.println(socket.getInetAddress().toString());
				
				String IPandPort = socket.getInetAddress().toString() + socket.getPort();
				IPandPort = SHA1(IPandPort);
				
				for (int i = 0; i < busIDs.size(); i++) {
					if (SHA1((String)busIDs.get(i)).compareTo(IPandPort) == -1) {
						brokerLines.add((String)busIDs.get(i));
					}
				}
				
				System.out.println(brokerLines);
				
				System.out.println(busIDs);
				System.out.println(in.nextLine());
				System.out.println(in.nextLine());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	} */
	
	private static ArrayList hashIPandPort() throws NoSuchAlgorithmException {
		try{ 
		    FileReader in = new FileReader(path);
		    BufferedReader br = new BufferedReader(in);
		    ArrayList hashed = new ArrayList();
		    match = new Hashtable <>();

		    String line;
		    while ((line = br.readLine()) != null) {
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
	
	private static String SHA1(String s) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		mDigest.update(s.getBytes(), 0, s.length());
		return new BigInteger(1, mDigest.digest()).toString();
	}
}