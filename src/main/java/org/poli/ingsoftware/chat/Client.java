package org.poli.ingsoftware.chat;

import org.poli.ingsoftware.constants.ChatConstants;
import org.poli.ingsoftware.constants.ClientConstants;
import org.poli.ingsoftware.model.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private DataInputStream in;
    private DataOutputStream out;
    private final Scanner sc;
    private Socket socket;
    boolean isConexionActive;

    public Client() {
        sc = new Scanner(System.in);
    }

    private String sendingMessage(User user) {
        try {
            System.out.println(user.getName() + ": ");
            String message = sc.nextLine();
            out.writeUTF(user.getName() + ": " + message);
            return message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void receivingMessage(DataInputStream in) {
        try {
            String receiveMessage = in.readUTF();
            System.out.println(receiveMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startSession() throws IOException {
        isConexionActive = true;
        socket = new Socket(ChatConstants.SERVER_HOST, ChatConstants.SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        User user = createUser();

        while (isConexionActive) {
            // Receive message from server
            receivingMessage(in);
            String sentMessage = sendingMessage(user);
            isConexionActive = !sentMessage.equals(ChatConstants.BYE_PHASE);
        }
    }

    private User createUser() throws IOException {
        System.out.println(ClientConstants.TYPE_YOUR_USER_NAME);
        User user = new User(sc.nextLine(), socket);
        out.writeUTF(user.getName());
        return user;
    }

    private void endingSession() throws IOException {
        System.out.println(ClientConstants.GOOD_BYE_MESSAGE + ChatConstants.SERVER_HOST);
        socket.close();
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.startSession();
            client.endingSession();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}