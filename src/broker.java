import java.io.*;
import java.net.*;
import java.util.Scanner;

public class broker {

	public static void main(String[] args) throws IOException {
		new broker().openServer();
	}
	
	public void openServer() throws IOException {
		ServerSocket providerSocket = null;
		Socket connection = null;
		
		providerSocket = new ServerSocket(1917);
		
		for (int i = 0; i < 2; i++) {
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
			PrintStream out;
			Scanner in;
			try {
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());

				System.out.println(socket.getPort());
				System.out.println(socket.getInetAddress());
				System.out.println(in.nextInt());
				System.out.println(in.nextInt());
				
				out.println(3);
				out.println(4);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}