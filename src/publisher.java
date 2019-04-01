import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.*;

public class publisher {

	public static void main(String[] args) throws UnknownHostException, IOException {
		new publisher().startPublisher();
	}
	
	public void startPublisher() throws UnknownHostException, IOException {
		ServerSocket publisherSocket = new ServerSocket(1871);
		Socket requestSocket = null;
		
		/*for (int i = 0; i < 2; i++) {
			ArrayList busIDs = readBusLines(i);
			while (true) {
				requestSocket = publisherSocket.accept();
				
				new myThread(requestSocket, busIDs).start();
				System.out.println("lol");
			}
		}*/
		
		ArrayList busIDs = readBusLines(0);
		while (true) {
			requestSocket = publisherSocket.accept();
			
			new myThread(requestSocket, busIDs).start();
		}
		
		/*for (int i = 0; i < 2; i++) {
			requestSocket= new Socket(InetAddress.getByName("192.168.1.140"), 1917);
			
			new myThread(requestSocket, i).start();
		}*/
	}
	
	private class myThread extends Thread {
		Socket socket;
		ArrayList busIDs;
		
		public myThread(Socket socket, ArrayList busIDs) {
			this.socket = socket;
			this.busIDs = busIDs;
		}
		
		public void run() {
			PrintStream out = null;
			Scanner in = null;
			
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String broker_message = in.nextLine();
			
			while (broker_message != "stop") {
				if (busIDs.contains(broker_message)) {
					out.println(readBusPositions(broker_message));
				} else {
					out.println("Not found!");
				}
				broker_message = in.nextLine();
			}
			
			try {
	            in.close();
	            out.close();
	            this.socket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	/*private class myThread extends Thread {
		Socket socket;
		int num;
		
		public myThread(Socket socket, int num) {
			this.socket = socket;
			this.num = num;
		}
		
		public void run() {
			PrintStream out;
			Scanner in;
			ArrayList busIDs;
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
				
				busIDs = readBusLines(num);
				
				for (int i = 0; i < 2; i++) {
					out.println(busIDs);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/

	String path1 = Paths.get("src\\busLinesNew.txt").toAbsolutePath().toString();
	// System.out.println(path);

	public ArrayList readBusLines(int i) {
		try{ 
		    FileReader in = new FileReader(path1);
		    BufferedReader br = new BufferedReader(in);
		    ArrayList busIDs = new ArrayList();

		    String line;
		    String lineCode;
		    while ((line = br.readLine()) != null) {
		    	lineCode = line.substring(0, line.indexOf(','));
		    	if (i == 0) {
		    		if (Integer.parseInt(lineCode) < 900) busIDs.add(lineCode);
		    	} else {
		    		if (Integer.parseInt(lineCode) >= 900) busIDs.add(lineCode);
		    	}
		    }	
		    in.close();
		    return busIDs;
		} catch (IOException e) {
			System.out.println("File Read Error");
			return null;
		}
	}
	
	String path2 = Paths.get("src\\busPositionsNew.txt").toAbsolutePath().toString();
	
	public String readBusPositions(String busID) {
		try{ 
			FileReader in = new FileReader(path2);
			BufferedReader br = new BufferedReader(in);                 //reading coordinates

			String line;
			    
			while ((line = br.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens[0].compareTo(busID) == 0) {
				        return tokens[3]+" " + tokens[4];
					}
			} 
			in.close();
		} catch (IOException e) {
			System.out.println("File Read Error");
		}
		return null;
	}
}