import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class publisher {

	private static ArrayList<String> busIDs;
	private static int port;
	private static String[] busLines = {"1151", "821", "750", "817", "818", "974", "1113", "816", "804", "1219", "1220", "938", "831", "819", "1180", "868", "824", "825", "1069", "1077"};
	private String path = Paths.get("busPositionsNew.txt").toAbsolutePath().toString();
	
	public static void main(String[] args) throws IOException {
		busIDs = new ArrayList<String>();
		port = 1871;
		if (args[0].compareTo("1") == 0) {
			for (int i = 0; i < 10; i++) {
				busIDs.add(busLines[i]);
			}
		} else if (args[0].compareTo("2") == 0) {
			port = 1917;
			for (int i = 10; i < busLines.length; i++) {
				busIDs.add(busLines[i]);
			}
		}
		new publisher().startPublisher();
	}

	public void startPublisher() throws IOException {
		ServerSocket publisherSocket = new ServerSocket(port);
		Socket requestSocket = null;
		
		while (true) {
			requestSocket = publisherSocket.accept();
			
			new myThread(requestSocket).start();
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
			
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String broker_buses = in.nextLine();
			String[] tokens = broker_buses.split("], ");
			
			String broker1_buses = tokens[0];
			String broker2_buses = tokens[1];
			String broker3_buses = tokens[2];
			
			String broker1 = broker1_buses.substring(1, broker1_buses.indexOf("="));
			broker1_buses = broker1_buses.substring(broker1_buses.indexOf("="));
			
			String broker2 = broker2_buses.substring(0, broker2_buses.indexOf("="));
			broker2_buses = broker2_buses.substring(broker2_buses.indexOf("="));
			
			String broker3 = broker3_buses.substring(0, broker3_buses.indexOf("="));
			broker3_buses = broker3_buses.substring(broker3_buses.indexOf("="));
			
			String broker_message = in.nextLine();
			
			if (busIDs.contains(broker_message)) {
				if (broker1_buses.contains(broker_message)) {
					out.println(Integer.parseInt(broker1.substring(broker1.length()-4)));
				} else if (broker2_buses.contains(broker_message)) {
					out.println(Integer.parseInt(broker2.substring(broker2.length()-4)));
				} else if (broker3_buses.contains(broker_message)) {
					out.println(Integer.parseInt(broker3.substring(broker3.length()-4)));
				}
				
				try{ 
					FileReader in2 = new FileReader(path);
					BufferedReader br = new BufferedReader(in2);

					String line;
					    
					while ((line = br.readLine()) != null) {
						String[] t = line.split(",");
						if (t[0].compareTo(broker_message) == 0) {
							out.println(t[3]+" " + t[4]);
							out.flush();
							sleep(500);
						}
					}
					out.println("stop");
					out.flush();
					in2.close();
				} catch (IOException e) {
					System.out.println("File Read Error");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				out.println("Not found!");
			}
			
			/*while (broker_message != "stop") {
				if (busIDs.contains(broker_message)) {
					try{ 
						FileReader in2 = new FileReader(path);
						BufferedReader br = new BufferedReader(in2);

						String line;
						    
						while ((line = br.readLine()) != null) {
							String[] t = line.split(",");
							if (t[0].compareTo(broker_message) == 0) {
								out.println(t[3]+" " + t[4]);
								out.flush();
								sleep(500);
							}
						}
						out.println("stop");
						out.flush();
						in2.close();
					} catch (IOException e) {
						System.out.println("File Read Error");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					out.println("Not found!");
				}
				broker_message = in.nextLine();
			}*/
			
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
	
	/*public ArrayList readBusLines(int i) {
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
	}*/
	
	/*public String readBusPositions(String busID) {
		try{ 
			FileReader in = new FileReader("C:\\Users\\xristos\\Documents\\Eclipse Workspace\\DS_Part1\\src\\busPositionsNew.txt");
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
	}*/
}