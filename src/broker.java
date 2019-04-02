import java.io.*;
import java.math.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class broker {
	
	public static ArrayList br_bus;
	public static String IP = "192.168.1.140";

	public static void main(String[] args) throws IOException {
		String port = args[0];
		
		String[] busLines = {"021", "022", "024", "025", "026", "027", "032", "036", "040", "045", "049", "051", "054", "057", "060", "1", "10"};
		
		br_bus = new ArrayList();
		
		String hash = IP + port;
		try {
			hash = SHA1(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < busLines.length; i++) {	
			try {
				if(SHA1(busLines[i]).compareTo(hash) == -1) {
					br_bus.add(busLines[i]);
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(br_bus);
		//new broker().openServer();
	}
	
	public void openServer() throws IOException {
		ServerSocket providerSocket = new ServerSocket(1917);
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
			
			Socket requestSocket = null;
			PrintStream p_out = null;
			Scanner p_in = null;
			
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
				
				requestSocket = new Socket("192.168.1.140", 1871);
				p_out = new PrintStream(requestSocket.getOutputStream());
				p_in = new Scanner(requestSocket.getInputStream());
				
				String sub_msg;
				String pub_msg;
				
				sub_msg = in.nextLine();
				
				p_out.println(sub_msg);
				p_out.flush();
				
				do {
					pub_msg = in.nextLine();
					
					out.println(pub_msg);
					out.flush();
				} while (in.nextLine().compareTo("stop") != 0);
				
				in.nextLine();

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
	
	/*public ArrayList readBusLines() {
		try{ 
		    FileReader in = new FileReader("C:\\Users\\xristos\\Documents\\Eclipse Workspace\\DS_Part1\\src\\busLinesNew.txt");
		    BufferedReader br = new BufferedReader(in);
		    ArrayList busIDs = new ArrayList();

		    String line;
		    String lineCode;
		    while ((line = br.readLine()) != null) {
		    	//lineCode = line.substring(line.indexOf(',')+1, line.lastIndexOf(','));
		    	lineCode = line.substring(0, line.indexOf(','));
		    	busIDs.add(lineCode);
		    }	
		    in.close();
		    return busIDs;
		} catch (IOException e) {
			System.out.println("File Read Error");
			return null;
		}
	}*/
	
	public static String SHA1 (String s) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		mDigest.update(s.getBytes(), 0, s.length());
		return new BigInteger(1, mDigest.digest()).toString();
	}
}