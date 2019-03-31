import java.io.*;
import java.net.*;
import java.util.*;

public class publisher {

	public static void main(String[] args) throws UnknownHostException, IOException {
		new publisher().startClient();
	}
	
	public void startClient() throws UnknownHostException, IOException {
		Socket requestSocket = null;
		
		for (int i = 0; i < 2; i++) {
			requestSocket= new Socket(InetAddress.getByName("192.168.1.140"), 1917);
			
			new myThread(requestSocket, i).start();
		}
	}
	
	private class myThread extends Thread {
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
	}
	
	public ArrayList readBusLines(int i) {
		try{ 
		    FileReader in = new FileReader("C:\\Users\\xristos\\Documents\\Eclipse Workspace\\DS_Part1\\src\\busLinesNew.txt");
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
	
	public void readBusPositions() {
		try{ 
			FileReader in = new FileReader("C:\\Users\\xristos\\Documents\\Eclipse Workspace\\DS_Part1\\src\\busPositionsNew.txt");
			BufferedReader br = new BufferedReader(in);                 //reading coordinates

			String line;
			    
			while ((line = br.readLine()) != null) {
			    	
					String[] tokens = line.split(",");
			        System.out.println(tokens[3]+" " + tokens[4]);
			} 
			in.close();
		} catch (IOException e) {
			System.out.println("File Read Error");
		}
	}
}