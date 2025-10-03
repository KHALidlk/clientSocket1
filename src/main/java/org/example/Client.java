package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.ClientLogin;
import org.example.model.Message;

import java.io.*;
import java.net.Socket;

public class Client extends Application {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    private String username;

    // UI chat
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Entrez votre nom d'utilisateur :");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        Button connectButton = new Button("Se connecter");

        VBox loginLayout = new VBox(10, label, usernameField, connectButton);
        Scene loginScene = new Scene(loginLayout, 300, 200);

        // === SCENE 2 : CHAT ===
        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        messageField.setPromptText("Votre message...");
        sendButton = new Button("Envoyer");

        VBox chatLayout = new VBox(10, chatArea, messageField, sendButton);
        Scene chatScene = new Scene(chatLayout, 400, 400);

        // Action du bouton connexion
        connectButton.setOnAction(e -> {
            username = usernameField.getText().trim();
            if (username.isEmpty()) {
                showAlert("Erreur", "Veuillez entrer un nom d'utilisateur.");
                return;
            }
            if (connectToServer()) {
                primaryStage.setScene(chatScene); // switch vers la scène chat
            } else {
                showAlert("Erreur", "Impossible de se connecter au serveur.");
            }
        });

        // Action du bouton envoyer
        sendButton.setOnAction(e -> sendMessage());

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Client Chat");
        primaryStage.show();
    }

    // Connexion au serveur
    private boolean connectToServer() {
        try {
            socket = new Socket("192.168.234.247", 5000);
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objIn = new ObjectInputStream(socket.getInputStream());

            ClientLogin clientLogin = new ClientLogin(username);
            objOut.writeObject(clientLogin);
            objOut.flush();

            Message greeting = (Message) objIn.readObject();
            appendToChat("[Serveur] " + greeting.getMessage());

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

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Envoi d'un message
    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty()) return;

        try {
            Message message = new Message();
            message.setMessage(username + ": " + msg);
            objOut.writeObject(message);
            objOut.flush();
            messageField.clear();

            if (msg.equalsIgnoreCase("exit")) {
                socket.close();
                appendToChat("Vous avez quitté la conversation.");
                sendButton.setDisable(true);
                messageField.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appendToChat("Erreur lors de l'envoi du message.");
        }
    }

    // Afficher message dans l'UI
    private void appendToChat(String text) {
        Platform.runLater(() -> chatArea.appendText(text + "\n"));
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
