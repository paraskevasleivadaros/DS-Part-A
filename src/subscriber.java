import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class subscriber {
    public static void main(String[] args) throws UnknownHostException, IOException {
        new subscriber().startClient();
    }

    public void startClient() throws UnknownHostException, IOException {
        Socket requestSocket = null;

        for (int i = 0; i < 2; i++) {
            requestSocket = new Socket(InetAddress.getByName("192.168.1.140"), 1917);

            new subscriber.myThread(requestSocket).start();
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

                for (int i = 0; i < 2; i++) {
                    out.println(i);
                }

                System.out.println(in.nextInt());
                System.out.println(in.nextInt());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
