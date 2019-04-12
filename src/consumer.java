import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class consumer {

    private static String bus;

	public static void main(String[] args) throws IOException {
		bus = args[0];
		new consumer().startClient();
	}

	private void startClient() throws IOException {
		Socket requestSocket;
        String IP = "192.168.1.7";
        requestSocket = new Socket(IP, 3421);
		new myThread(requestSocket).start();
	}
	
	private class myThread extends Thread {
		Socket socket;
		
		myThread(Socket socket) { this.socket = socket;	}
		
		public void run() {
			PrintStream out = null;
			Scanner in = null;

			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
				
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

				socket.close();
				
				if (broker1_buses.contains(bus)) {
					socket = new Socket(broker1.substring(0, broker1.length()-4), Integer.parseInt(broker1.substring(broker1.length()-4)));
				} else if (broker2_buses.contains(bus)) {
					socket = new Socket(broker2.substring(0, broker2.length()-4), Integer.parseInt(broker2.substring(broker2.length()-4)));
				} else if (broker3_buses.contains(bus)) {
					socket = new Socket(broker3.substring(0, broker3.length()-4), Integer.parseInt(broker3.substring(broker3.length()-4)));
				}

				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
				
				out.println(bus);
				
				int i=0;
				
				do {
					System.out.println(in.nextLine());
					i++;
					if(i==10) break;
				} while (in.nextLine().compareTo("stop") != 0);
				
				/*Scanner in2 = null;
				
				String cons_msg;

				System.out.println("Give me the number of the bus you are interested for :");
				//cons_msg = in2.nextLine();
				cons_msg = "824";
				
				out.println(cons_msg);
				
				int i=0;
				
				do {
					System.out.println(in.nextLine());
					i++;
					if(i==10) break;
				} while (in.nextLine().compareTo("stop") != 0);
				
				System.out.println("Yo");
				
				cons_msg = "825";
				
				out.println(cons_msg);
				
				i=0;
				
				do {
					System.out.println(in.nextLine());
					i++;
					if(i==10) break;
				} while (in.nextLine().compareTo("stop") != 0);*/

			} catch (IOException e) {
				e.printStackTrace();
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
}