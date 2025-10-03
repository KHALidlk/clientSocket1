package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ChatApp extends Application {

    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;
    private Client client;  // ton client Socket

    @Override
    public void start(Stage stage) {
        chatArea = new TextArea();
        chatArea.setEditable(false);

        inputField = new TextField();
        sendButton = new Button("Envoyer");

        HBox inputBox = new HBox(10, inputField, sendButton);
        VBox root = new VBox(10, chatArea, inputBox);

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Chat JavaFX");
        stage.setScene(scene);
        stage.show();

        // 1) Lancer le client réseau
        client = new Client(chatArea);
        client.startClient();

        // 2) Bouton envoyer
        sendButton.setOnAction(e -> {
            String text = inputField.getText();
            if (!text.isEmpty()) {
                client.sendMessage(text);
                inputField.clear();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
