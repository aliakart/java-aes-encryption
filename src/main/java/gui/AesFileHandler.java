package gui;

import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import krypto.AesDecryption;
import krypto.AesEncryption;

import java.io.File;
import java.nio.file.Files;

public class AesFileHandler {

    public static byte[] parseKey(String keyStr) throws Exception {
        int len = keyStr.length();

        if (len == 32 || len == 48 || len == 64) {
            byte[] keyBytes = new byte[len / 2];
            for (int i = 0; i < len / 2; i++) {
                keyBytes[i] = (byte) Integer.parseInt(keyStr.substring(i * 2, i * 2 + 2), 16);
            }
            return keyBytes;
        }
        else if (len == 16 || len == 24 || len == 32) {
            return keyStr.getBytes("UTF-8");
        } else {
            throw new Exception("wrong length");
        }
    }

    public static void encryptFile(Stage stage, String keyStr, Label statusLabel) {
        try {
            byte[] keyBytes = parseKey(keyStr);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik do zaszyfrowania");
            File inputFile = fileChooser.showOpenDialog(stage);

            if (inputFile != null) {
                byte[] fileData = Files.readAllBytes(inputFile.toPath());

                AesEncryption enc = new AesEncryption(keyBytes);
                byte[] cipherData = enc.encode(fileData);

                fileChooser.setTitle("Gdzie zapisać zaszyfrowany plik?");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki AES", "*.aes"));
                File outputFile = fileChooser.showSaveDialog(stage);

                if (outputFile != null) {
                    Files.write(outputFile.toPath(), cipherData);
                    statusLabel.setText("Sukces! Plik zaszyfrowano i zapisano.");
                }
            }
        } catch (Exception ex) {
            statusLabel.setText("Błąd pliku: " + ex.getMessage());
        }
    }

    public static void decryptFile(Stage stage, String keyStr, Label statusLabel) {
        try {
            byte[] keyBytes = parseKey(keyStr);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz zaszyfrowany plik (.aes)");
            File inputFile = fileChooser.showOpenDialog(stage);

            if (inputFile != null) {
                byte[] cipherData = Files.readAllBytes(inputFile.toPath());

                AesEncryption enc = new AesEncryption(keyBytes);
                AesDecryption dec = new AesDecryption(enc.getKeyWords());
                byte[] plainData = dec.decode(cipherData);

                fileChooser.setTitle("Gdzie zapisać odszyfrowany plik?");
                fileChooser.getExtensionFilters().clear();
                File outputFile = fileChooser.showSaveDialog(stage);

                if (outputFile != null) {
                    Files.write(outputFile.toPath(), plainData);
                    statusLabel.setText("Sukces! Plik przywrócono do oryginału.");
                }
            }
        } catch (Exception ex) {
            statusLabel.setText("Błąd pliku: " + ex.getMessage());
        }
    }
}