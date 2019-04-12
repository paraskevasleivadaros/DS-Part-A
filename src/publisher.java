import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class publisher {

	private static ArrayList<String> busIDs;
	private String path = Paths.get("busPositionsNew.txt").toAbsolutePath().toString();
	private static String[] busLines = {"1151", "821", "750", "817", "818", "974", "1113", "816", "804", "1219", "1220", "938", "831", "819", "1180", "868", "824", "825", "1069", "1077"};
	
	public static void main(String[] args) throws IOException {
		busIDs = new ArrayList<>();
		if (args[0].compareTo("1") == 0) {
            busIDs.addAll(Arrays.asList(busLines).subList(0, 10));
		} else if (args[0].compareTo("2") == 0) {
            busIDs.addAll(Arrays.asList(busLines).subList(10, busLines.length));
		}
		new publisher().startPublisher();
	}
	
	private void startPublisher() throws IOException {
		ServerSocket publisherSocket = new ServerSocket(1871);
		Socket requestSocket;
		
		while (true) {
			requestSocket = publisherSocket.accept();
			new myThread(requestSocket).start();
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
			
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

            String broker_message = null;
            if (in != null) {
                broker_message = in.nextLine();
            }

            if (broker_message != null) {
                while (broker_message.equals("stop")) {
                    if (busIDs.contains(broker_message)) {
                        try{
                            FileReader in2 = new FileReader(path);
                            BufferedReader br = new BufferedReader(in2);

                            String line;

                            while ((line = br.readLine()) != null) {
                                String[] tokens = line.split(",");
                                if (tokens[0].compareTo(Objects.requireNonNull(broker_message)) == 0) {

                                   out.println(tokens[3]+" " + tokens[4]);
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
                }
            }

            try {
                if (in != null) in.close();
                if (out != null) out.close();
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