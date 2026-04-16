# Custom AES Encryption

This is a custom implementation of the Advanced Encryption Standard (AES) algorithm built with Java. It does not rely on standard cryptography libraries like `javax.crypto` for the core encryption logic. 

The project was created to get a better practical understanding of the bitwise operations and mathematics behind AES, including the Rijndael key schedule, SubBytes, ShiftRows, and MixColumns transformations.

## Features
- Core AES algorithm implemented from scratch.
- Supports 128-bit, 192-bit, and 256-bit encryption modes.
- Text encryption and decryption with HEX output.
- File encryption (encrypts any file to `.aes` and decrypts it back).
- Simple GUI built with JavaFX.
- Maven-based build system.

## Technologies
- Java
- JavaFX 21
- Maven

## Running locally

Since this is a Maven project, you can run it directly from the terminal. Ensure you have Java and Maven installed.

1. Clone the repository:
   ```bash
   git clone [https://github.com/YOUR_USERNAME/java-aes-encryption.git](https://github.com/YOUR_USERNAME/java-aes-encryption.git)
   
2. Go to the project directory:
   cd java-aes-encryption
   
3. Run the application using the JavaFX Maven plugin:
   mvn clean javafx:run

## License
This project is licensed under the MIT License. See the LICENSE file for details.

