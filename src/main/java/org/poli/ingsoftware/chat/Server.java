package org.poli.ingsoftware.chat;

import org.poli.ingsoftware.constants.ChatConstants;
import org.poli.ingsoftware.constants.ServerConstants;
import org.poli.ingsoftware.model.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private DataOutputStream out;
    private DataInputStream in;
    private final Scanner sc;
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        sc = new Scanner(System.in);
    }

    private void startServer() {
        System.out.println(ServerConstants.GREETING_MESSAGE);
        boolean stateConnection;
        try {
            Socket cliente = this.serverSocket.accept();
            in = new DataInputStream(cliente.getInputStream());
            out = new DataOutputStream(cliente.getOutputStream());
            User user = createUser(cliente);

            while (!this.serverSocket.isClosed()) {
                sendingMessage();

                String receiveMessage = receivingMessage(user);
                stateConnection = receiveMessage.equals(ChatConstants.BYE_PHASE);
                if (stateConnection) {
                    cliente.close();
                    System.out.println("El usuario abandono");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendingMessage() throws IOException {
        System.out.println(ServerConstants.SERVER_NAME + ": ");
        out.writeUTF(ServerConstants.SERVER_NAME + ": " + sc.nextLine());
    }

    private String receivingMessage(User user) throws IOException {
        String receivedMessage = in.readUTF();
        System.out.println(user.getName() + ": " + receivedMessage);
        return receivedMessage;
    }

    private User createUser(Socket client) throws IOException {
        User user = new User(in.readUTF(), client);
        System.out.println("Usuario {" + user.getName() + "} conectado");
        return user;
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(new ServerSocket(ChatConstants.SERVER_PORT));
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
