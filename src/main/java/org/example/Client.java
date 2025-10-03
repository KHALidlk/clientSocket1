package org.example;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.example.model.ClientLogin;
import org.example.model.Message;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private TextArea chatArea;   // zone d'affichage dans l'UI
    private String username;

    public Client(TextArea chatArea) {
        this.chatArea = chatArea;
    }

    public void startClient() {
        try {
            // pour ce test : on fixe un nom ou on pourrait demander avec une TextField
            this.username = "User-" + (int)(Math.random()*1000);
            ClientLogin clientLogin = new ClientLogin(username);

            socket = new Socket("localhost", 5000);

            objOut = new ObjectOutputStream(socket.getOutputStream());
            objIn = new ObjectInputStream(socket.getInputStream());

            // envoi login
            objOut.writeObject(clientLogin);
            objOut.flush();

            // lire premier message du serveur (accueil)
            Message greeting = (Message) objIn.readObject();
            appendToChat("[Serveur] " + greeting.getMessage());

            // thread qui écoute les messages du serveur
            new Thread(() -> {
                try {
                    while (true) {
                        Message resultOp = (Message) objIn.readObject();
                        appendToChat("[Serveur] " + resultOp.getMessage());
                    }
                } catch (Exception e) {
                    appendToChat("Déconnecté du serveur.");
                }
            }).start();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        try {
            Message message = new Message();
            message.setMessage(username + ": " + msg);
            objOut.writeObject(message);
            objOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendToChat(String text) {
        Platform.runLater(() -> chatArea.appendText("\n" + text));
    }
}
