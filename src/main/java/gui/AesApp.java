package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import krypto.AesDecryption;
import krypto.AesEncryption;
import krypto.AesCore;

public class AesApp extends Application {

    @Override
    public void start(Stage stage) {
        TextField keyField = new TextField("SuperKlucz123456");
        keyField.setPromptText("Klucz (dokładnie 16 znaków)");
        Button generateKey = new Button("Wygeneruj klucz");

        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Wpisz tekst do zaszyfrowania...");
        inputArea.setPrefRowCount(4);

        TextArea outputArea = new TextArea();
        outputArea.setPromptText("Wynik pojawi się tutaj...");
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(4);

        Label statusLabel = new Label();

        Button encryptBtn = new Button("Zaszyfruj");
        Button decryptBtn = new Button("Odszyfruj");

        ComboBox<Integer> modeBox = new ComboBox<>();
        modeBox.getItems().addAll(128, 192, 256);
        modeBox.setValue(128);

        generateKey.setOnAction(e -> {
            int bits = modeBox.getValue();
            int bytesLength = bits / 8;

            byte[] key = AesEncryption.generateKey(bytesLength);
            String keyHex = AesCore.bytesToHex(key);
            keyField.setText(keyHex);
        });

        encryptBtn.setOnAction(e -> {
            try {
                byte[] keyBytes = AesFileHandler.parseKey(keyField.getText().trim());
                AesEncryption enc = new AesEncryption(keyBytes);

                byte[] data = inputArea.getText().getBytes("UTF-8");
                outputArea.setText(AesCore.bytesToHex(enc.encode(data)));

                statusLabel.setText("Gotowe");
            } catch (Exception ex) {
                statusLabel.setText("Błąd: " + ex.getMessage());
            }
        });

        decryptBtn.setOnAction(e -> {
            try {
                byte[] keyBytes = AesFileHandler.parseKey(keyField.getText().trim());

                String hexInput = inputArea.getText().trim();
                byte[] cipher = new byte[hexInput.length() / 2];
                for (int i = 0; i < cipher.length; i++) {
                    cipher[i] = (byte) Integer.parseInt(hexInput.substring(i * 2, i * 2 + 2), 16);
                }

                AesEncryption enc = new AesEncryption(keyBytes);
                AesDecryption dec = new AesDecryption(enc.getKeyWords());
                byte[] decoded = dec.decode(cipher);

                outputArea.setText(new String(decoded, "UTF-8"));
                statusLabel.setText("Odszyfrowano pomyślnie.");
            } catch (Exception ex) {
                statusLabel.setText("Błąd: " + ex.getMessage());
            }
        });

        Button encryptFileBtn = new Button("Zaszyfruj plik");
        Button decryptFileBtn = new Button("Odszyfruj plik");


        encryptFileBtn.setOnAction(e -> AesFileHandler.encryptFile(stage, keyField.getText().trim(), statusLabel));
        decryptFileBtn.setOnAction(e -> AesFileHandler.decryptFile(stage, keyField.getText().trim(), statusLabel));

        HBox keyControls = new HBox(10, new Label("Tryb AES:"), modeBox);
        HBox textButtons = new HBox(10, encryptBtn, decryptBtn, generateKey);
        HBox fileButtons = new HBox(10, encryptFileBtn, decryptFileBtn);

        VBox root = new VBox(10,
                keyControls,
                new Label("Klucz:"), keyField,
                new Label("Tekst:"), inputArea,
                textButtons,
                new Label("Plik:"), fileButtons,
                new Label("Wynik:"), outputArea,
                statusLabel
        );
        root.setPadding(new Insets(15));
        root.setPrefWidth(550);

        stage.setScene(new Scene(root));
        stage.setTitle("AES-128 Szyfrowanie");
        stage.show();

    }
}