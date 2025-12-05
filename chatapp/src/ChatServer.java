package src;
import java.io.*;
import java.net.*;

public class ChatServer {
    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for client...");

            Socket socket = serverSocket.accept(); // Wait for client
            InetAddress clientInfo = socket.getInetAddress();
            System.out.println("Client connected: " + clientInfo.getHostName() + " [" + clientInfo.getHostAddress() + "]");

            // Data streams for communication
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Thread to read messages from client
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF(); // Read UTF string from client
                        System.out.println("Client: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Client disconnected.");
                }
            });
            readThread.start();

            // Main thread to send messages to client
            while (true) {
                String msgToSend = console.readLine();
                if (msgToSend != null) {
                    out.writeUTF(msgToSend); // Send UTF string to client
                    out.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
