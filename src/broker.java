import java.io.*;
import java.math.*;
import java.net.*;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class broker {

	public static void main(String[] args) throws IOException {
		new broker().openServer();
	}
	
	public void openServer() throws IOException {
		ServerSocket providerSocket = new ServerSocket(1917);
		Socket connection = null;
		
		/*while(true) {
			connection = providerSocket.accept();
			
			new myThread(connection).start();
		}*/
		
		connection = new Socket("localhost", 1871);
		new myThread(connection).start();
	}
	
	private class myThread extends Thread {
		Socket socket;
		
		public myThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			PrintStream out = null;
			Scanner in = null;
			
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
				
				Socket requestSocket = new Socket("localhost", 1871);
				PrintStream p_out = new PrintStream(requestSocket.getOutputStream());
				Scanner p_in = new Scanner(requestSocket.getInputStream());
				
				String result;
				
				//p_out.println("1151");
				//result = p_in.nextLine();
				//System.out.println(result);
				
				p_out.println("821");
				result = p_in.nextLine();
				System.out.println(result);
				
				p_out.println("stop");
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} */

	String path = Paths.get("src\\busLinesNew.txt").toAbsolutePath().toString();
	// System.out.println(path);

	public ArrayList readBusLines() {
		try{ 
		    FileReader in = new FileReader(path);
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
	}
	
	public String SHA1 (String s) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		mDigest.update(s.getBytes(), 0, s.length());
		return new BigInteger(1, mDigest.digest()).toString();
	}
}