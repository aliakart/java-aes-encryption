package krypto;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String keyStr = "SuperKlucz123456";
        byte[] key = keyStr.getBytes("UTF-8");

        System.out.print("Wpisz tekst do przetestowania AES: ");
        String plainText = scanner.nextLine();
        byte[] data = plainText.getBytes("UTF-8");

        AesEncryption encryptor = new AesEncryption(key);
        byte[] cipher = encryptor.encode(data);

        System.out.println("\n--- SZYFROWANIE ---");
        System.out.println("Zaszyfrowane (HEX): " + AesEncryption.bytesToHex(cipher));
        System.out.println("Rozmiar szyfru: " + cipher.length + " bajtow");

        AesDecryption decryptor = new AesDecryption(encryptor.getKeyWords());
        byte[] decodedBytes = decryptor.decode(cipher);


        String decodedText = new String(decodedBytes, "UTF-8");

        System.out.println("\n--- DESZYFROWANIE ---");
        System.out.println("Odzyskany tekst: " + decodedText);

        scanner.close();
    }
}