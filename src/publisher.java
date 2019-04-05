import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class publisher {

	public static ArrayList busIDs;
	String path = Paths.get("src\\busPositionsNew.txt").toAbsolutePath().toString();
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		busIDs = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
			busIDs.add(args[i]);
		}
		new publisher().startPublisher();
	}
	
	public void startPublisher() throws UnknownHostException, IOException {
		ServerSocket publisherSocket = new ServerSocket(1871);
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

			String broker_message = in.nextLine();

			while (broker_message != "stop") {
				if (busIDs.contains(broker_message)) {
					try{
						FileReader in2 = new FileReader(path);
						BufferedReader br = new BufferedReader(in2);

						String line;

						while ((line = br.readLine()) != null) {
								String[] tokens = line.split(",");
								if (tokens[0].compareTo(broker_message) == 0) {
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

			try {
	            in.close();
	            out.close();
	            this.socket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
}