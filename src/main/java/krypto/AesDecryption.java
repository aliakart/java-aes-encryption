package krypto;

import java.util.Arrays;

public class AesDecryption extends AesCore{

    private int Nr;


    public AesDecryption(byte[][] generatedKeys){
        this.keyWords=generatedKeys;
        this.Nr=(generatedKeys.length / 4)-1;
    }


    public byte[] decode(byte[] data) throws Exception {

        if (data == null || data.length % 16 != 0) {
            throw new Exception("data.length % 16 != 0");
        }

        byte[] tmp = new byte[data.length];
        byte[] block = new byte[16];

        for (int i = 0; i < data.length; i += 16) {

            System.arraycopy(data, i, block, 0, 16);

            byte[] decryptedBlock = decryptBlock(block);

            System.arraycopy(decryptedBlock, 0, tmp, i, 16);
        }

        int zeros = 0;
        for (int j = tmp.length - 1; j >= tmp.length - 16 && j >= 0; j--) {
            if (tmp[j] == 0) { // 0 - это и есть '\0' в байтах
                zeros++;
            } else {
                break;
            }
        }


        return Arrays.copyOf(tmp, tmp.length - zeros);
    }

    private void invShiftRows(byte[] block) {
        byte tmp;

        // wiersz 0 ozostaje bez zmian

        // wiersz 1 przesunięcie o 1 miejsce w prawo
        tmp = block[13];
        block[13] = block[9];
        block[9] = block[5];
        block[5] = block[1];
        block[1] = tmp;

        // wiersz 2 przesunięcie o 2 miejsca w prawo
        tmp = block[2];
        block[2] = block[10];
        block[10] = tmp;
        tmp = block[6];
        block[6] = block[14];
        block[14] = tmp;

        // wiersz 3 przesunięcie o 3 miejsca w prawo
        tmp = block[3];
        block[3] = block[7];
        block[7] = block[11];
        block[11] = block[15];
        block[15] = tmp;
    }

    private void invSubBytes(byte[] block){
        for (int i=0;i<block.length;i++){
            block[i]=SBox_RCON.invSubBytes(block[i]);
        }
    }

    private byte[] decryptBlock(byte[] completeBlock) throws Exception {
        byte[] tmp = new byte[completeBlock.length];
        System.arraycopy(completeBlock, 0, tmp, 0, completeBlock.length);

        addRoundKey(tmp, Nr);
        invShiftRows(tmp);
        invSubBytes(tmp);

        for (int i=Nr-1; i>0; i--) {
            addRoundKey(tmp,i);
            invMixColumns(tmp);
            invShiftRows(tmp);
            invSubBytes(tmp);
        }

        addRoundKey(tmp,0);

        return tmp;
    }

    private byte mul9(byte b) {
        // b*8 ^ b*1
        return (byte) (mul2(mul2(mul2(b))) ^ b);
    }

    private byte mul11(byte b) {
        // b*8 ^ b*2 ^ b*1
        return (byte) (mul2(mul2(mul2(b))) ^ mul2(b) ^ b);
    }

    private byte mul13(byte b) {
        // b*8 ^ b*4 ^ b*1
        return (byte) (mul2(mul2(mul2(b))) ^ mul2(mul2(b)) ^ b);
    }

    private byte mul14(byte b) {
        // b*8 ^ b*4 ^ b*2
        return (byte) (mul2(mul2(mul2(b))) ^ mul2(mul2(b)) ^ mul2(b));
    }

    private void invMixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;

            byte a0 = state[i];
            byte a1 = state[i + 1];
            byte a2 = state[i + 2];
            byte a3 = state[i + 3];

            state[i]     = (byte) (mul14(a0) ^ mul11(a1) ^ mul13(a2) ^ mul9(a3));
            state[i + 1] = (byte) (mul9(a0)  ^ mul14(a1) ^ mul11(a2) ^ mul13(a3));
            state[i + 2] = (byte) (mul13(a0) ^ mul9(a1)  ^ mul14(a2) ^ mul11(a3));
            state[i + 3] = (byte) (mul11(a0) ^ mul13(a1) ^ mul9(a2)  ^ mul14(a3));
        }
    }

}
