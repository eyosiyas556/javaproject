package src;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatGUI {

    // Server components
    private JTextArea serverArea;
    private JTextField serverField;
    private DataInputStream serverIn;
    private DataOutputStream serverOut;

    // Client components
    private JTextArea clientArea;
    private JTextField clientField;
    private DataInputStream clientIn;
    private DataOutputStream clientOut;

    public ChatGUI() {
        JFrame frame = new JFrame("Chat GUI - Server & Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        // ------------------ Server Panel ------------------
        JPanel serverPanel = new JPanel(new BorderLayout());
        serverPanel.setBorder(BorderFactory.createTitledBorder("Server"));
        serverArea = new JTextArea(10, 50);
        serverArea.setEditable(false);
        serverField = new JTextField();
        JButton serverSend = new JButton("Send");
        JPanel serverInputPanel = new JPanel(new BorderLayout());
        serverInputPanel.add(serverField, BorderLayout.CENTER);
        serverInputPanel.add(serverSend, BorderLayout.EAST);
        serverPanel.add(new JScrollPane(serverArea), BorderLayout.CENTER);
        serverPanel.add(serverInputPanel, BorderLayout.SOUTH);

        // ------------------ Client Panel ------------------
        JPanel clientPanel = new JPanel(new BorderLayout());
        clientPanel.setBorder(BorderFactory.createTitledBorder("Client"));
        clientArea = new JTextArea(10, 50);
        clientArea.setEditable(false);
        clientField = new JTextField();
        JButton clientSend = new JButton("Send");
        JPanel clientInputPanel = new JPanel(new BorderLayout());
        clientInputPanel.add(clientField, BorderLayout.CENTER);
        clientInputPanel.add(clientSend, BorderLayout.EAST);
        clientPanel.add(new JScrollPane(clientArea), BorderLayout.CENTER);
        clientPanel.add(clientInputPanel, BorderLayout.SOUTH);

        // Add panels to frame
        frame.setLayout(new GridLayout(2, 1));
        frame.add(serverPanel);
        frame.add(clientPanel);

        frame.setVisible(true);

        // Start server and client threads
        new Thread(this::startServer).start();
        new Thread(this::startClient).start();

        // Action listeners
        serverSend.addActionListener(e -> sendServerMessage());
        serverField.addActionListener(e -> sendServerMessage());

        clientSend.addActionListener(e -> sendClientMessage());
        clientField.addActionListener(e -> sendClientMessage());
    }

    // ------------------ SERVER METHODS ------------------
    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            serverArea.append("Server started. Waiting for client...\n");
            Socket socket = serverSocket.accept();
            serverArea.append("Client connected: " + socket.getInetAddress() + "\n");

            serverIn = new DataInputStream(socket.getInputStream());
            serverOut = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String msg = serverIn.readUTF();
                serverArea.append("Client: " + msg + "\n");
            }

        } catch (IOException e) {
            serverArea.append("Server error: " + e.getMessage() + "\n");
        }
    }

    private void sendServerMessage() {
        String msg = serverField.getText();
        if (!msg.isEmpty() && serverOut != null) {
            try {
                serverOut.writeUTF(msg);
                serverOut.flush();
                serverArea.append("Server: " + msg + "\n");
                serverField.setText("");
            } catch (IOException e) {
                serverArea.append("Error sending message.\n");
            }
        }
    }

    // ------------------ CLIENT METHODS ------------------
    private void startClient() {
        try {
            Thread.sleep(500); // wait for server to start

            Socket socket = new Socket("localhost", 5000);
            clientArea.append("Client connected to server.\n");

            clientIn = new DataInputStream(socket.getInputStream());
            clientOut = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String msg = clientIn.readUTF();
                clientArea.append("Server: " + msg + "\n");
            }

        } catch (IOException | InterruptedException e) {
            clientArea.append("Client error: " + e.getMessage() + "\n");
        }
    }

    private void sendClientMessage() {
        String msg = clientField.getText();
        if (!msg.isEmpty() && clientOut != null) {
            try {
                clientOut.writeUTF(msg);
                clientOut.flush();
                clientArea.append("Client: " + msg + "\n");
                clientField.setText("");
            } catch (IOException e) {
                clientArea.append("Error sending message.\n");
            }
        }
    }

    // ------------------ MAIN ------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}    