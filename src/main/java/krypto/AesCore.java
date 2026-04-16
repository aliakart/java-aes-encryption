package krypto;

public abstract class AesCore {
    protected byte[][] keyWords;

    public void addRoundKey(byte[] block, int round) {
        int x = 0;
        for (int i = round * 4; i < round * 4 + 4; i++) {
            for(int j = 0; j < 4; j++) {
                block[x] = (byte) (block[x] ^ keyWords[i][j]);
                x++;
            }
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public byte mul2(byte b) {
        byte result = (byte) (b << 1);

        if ((b & (byte) 0x80) != 0) {
            result = (byte) (result ^ (byte) 0x1B);
        }

        return result;
    }
}