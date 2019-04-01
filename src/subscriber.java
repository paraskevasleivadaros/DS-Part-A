import java.io.*;
import java.net.*;
import java.util.Scanner;

/*
client με thread για να μπορείς να έχεις πολλούς clients
Θα ρωτάει τον χρήστη πες μου πιο λεωφορείο θες, θα το παίρνει και το στέλνει στον broker
*/

public class subscriber {

    public static void main(String[] args) {
        new subscriber().startClient();
    }

    public void startClient() {

        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String message;

        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter the bus: ");
        int n = reader.nextInt(); // Scans the next token of the input as an int.
        //once finished
        reader.close();

        try {
            requestSocket = new Socket(InetAddress.getByName("127.0.1.1"), 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            try {
                message = (String) in.readObject();
                System.out.println("Server>" + message);
                out.writeObject("Hi!");
                out.flush();

                out.writeObject("Just Testing...");
                out.flush();

                out.writeObject("bye");
                out.flush();

            } catch (ClassNotFoundException classNot) {
                System.err.println("data received in unknown format");
            }
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

