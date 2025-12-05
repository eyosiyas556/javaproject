package src;
import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server.");

            // Data streams for communication
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Thread to read messages from server
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF(); // Read UTF string from server
                        System.out.println("Server: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Server disconnected.");
                }
            });
            readThread.start();

            // Main thread to send messages to server
            while (true) {
                String msgToSend = console.readLine();
                if (msgToSend != null) {
                    out.writeUTF(msgToSend); // Send UTF string
                    out.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
