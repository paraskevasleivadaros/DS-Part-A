import java.io.*;
import java.net.*;
import java.util.Scanner;

public class consumer {

	public static void main(String[] args) throws UnknownHostException, IOException {
		new consumer().startClient();
	}

	public void startClient() throws UnknownHostException, IOException {
		Socket requestSocket = null;

		requestSocket = new Socket(InetAddress.getByName("192.168.1.140"), 1917);
		new myThread(requestSocket).start();
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
                String bro_msg;
				String cons_msg;
				
                bro_msg = in.nextLine();
                
				
				Scanner in2 = null;
				
				

				System.out.println("Give me the number of the bus you are interested for :");
				//cons_msg = in2.nextLine();
				cons_msg = "046";
                if(!bro_msg.contains(cons_msg)){
                   in.nextLine();    // list with remaining brokers
                		             //change bro
                	   
                   
                }
                out.println(cons_msg);
                
				do {
					System.out.println(in.nextLine());
				} while (in.nextLine().compareTo("stop") != 0);

			} catch (IOException e) {
				e.printStackTrace();
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
