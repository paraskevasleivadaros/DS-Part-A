import java.io.*;
import java.math.*;
import java.net.*;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class broker {
	
	public static Hashtable <String, ArrayList<String>> br_bus;
	public static Hashtable <String, String> match;
	public static String IP = "192.168.1.140";
	public static String path = Paths.get("brokers.txt").toAbsolutePath().toString();
	public static String port;
	public static String[] busLines = {"1151", "821", "750", "817", "818", "974", "1113", "816", "804", "1219", "1220", "938", "831", "819", "1180", "868", "824", "825", "1069", "1077"};

	public static void main(String[] args) throws IOException {
		port = args[0];
		
		ArrayList br_hash = new ArrayList();
		try {
			br_hash = hashIPandPort();
			
			ArrayList br1_bus = new ArrayList();
			ArrayList br2_bus = new ArrayList();
			ArrayList br3_bus = new ArrayList();
			
			for (int i = 0; i < busLines.length; i++) {
				if (SHA1(busLines[i]).compareTo((String)br_hash.get(0)) == -1) {
					br1_bus.add(busLines[i]);
				} else if (SHA1(busLines[i]).compareTo((String)br_hash.get(1)) == -1) {
					br2_bus.add(busLines[i]);
				} else if (SHA1(busLines[i]).compareTo((String)br_hash.get(2)) == -1) {
					br3_bus.add(busLines[i]);
				} else {
					br1_bus.add(busLines[i]);
				}
			}
			
			br_bus = new Hashtable <String, ArrayList<String>>();

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
		Socket connection = null;
		
		while(true) {
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
			
			Socket requestSocket1 = null;
			Socket requestSocket2 = null;
			PrintStream p_out = null;
			Scanner p_in = null;
			PrintStream p1_out = null;
			Scanner p1_in = null;
			PrintStream p2_out = null;
			Scanner p2_in = null;
			
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
				
				out.println(br_bus.toString());
				out.flush();
				
				requestSocket1 = new Socket("192.168.1.140", 1871);
				p1_out = new PrintStream(requestSocket1.getOutputStream());
				p1_in = new Scanner(requestSocket1.getInputStream());

				p1_out.println(br_bus.toString());
				p1_out.flush();
				
				requestSocket2 = new Socket("192.168.1.140", 1917);
				p2_out = new PrintStream(requestSocket2.getOutputStream());
				p2_in = new Scanner(requestSocket2.getInputStream());
				
				p2_out.println(br_bus.toString());
				p2_out.flush();
				
				String sub_msg;
				String pub_msg;
				
				sub_msg = in.nextLine();
				
				p1_out.println(sub_msg);
				p1_out.flush();
				p2_out.println(sub_msg);
				p2_out.flush();
				
				if (p1_in.nextLine().compareTo(port) == 0) {
					p_out = p1_out;
					p_in = p1_in;
				} else if (p2_in.nextLine().compareTo(port) == 0) {
					p_out = p2_out;
					p_in = p2_in;
				}
				
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
	            in.close();
	            out.close();
	            this.socket.close();
	            p_in.close();
	            p_out.close();
	            p1_in.close();
	            p1_out.close();
	            p2_in.close();
	            p2_out.close();
	            requestSocket1.close();
	            requestSocket2.close();
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
	
	public static ArrayList hashIPandPort() throws NoSuchAlgorithmException {
		try{ 
		    FileReader in = new FileReader(path);
		    BufferedReader br = new BufferedReader(in);
		    ArrayList hashed = new ArrayList();
		    match = new Hashtable <String, String>();

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
	
	public static String SHA1 (String s) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		mDigest.update(s.getBytes(), 0, s.length());
		return new BigInteger(1, mDigest.digest()).toString();
	}
}