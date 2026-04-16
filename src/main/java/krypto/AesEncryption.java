package krypto;

import java.security.SecureRandom;
import static krypto.SBox_RCON.RCON;

public class AesEncryption extends AesCore{

    private byte[] entrenceKey;
    private int Nkw;
    private int Nr;

    public byte[][] getKeyWords() {
        return this.keyWords;
    }

    public AesEncryption(byte[] mainKey) throws Exception{
        this.entrenceKey = mainKey;
        this.Nkw = this.entrenceKey.length/4;
        switch (Nkw){
            case 4:
                this.Nr = 10;
                break;
            case 6:
                this.Nr = 12;
                break;
            case 8:
                this.Nr = 14;
                break;
            default:
                throw new Exception("wrong length");
        }
        this.keyWords = generateSubKey(entrenceKey);
    }

    static public byte[] generateKey(int lengthInBytes){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[lengthInBytes];
        random.nextBytes(bytes);
        return bytes;
    }

    public byte[] encode(byte[] data) throws Exception {

        int bytesToEncode;

        if (data.length == 0) {
            bytesToEncode = 16;
        }
        else if (data.length % 16 == 0) {
            bytesToEncode = data.length;
        }
        else {
            int blocksCount = data.length / 16;
            bytesToEncode = (blocksCount + 1) * 16;
        }
        byte[] block=new byte[16];
        byte[] result = new byte[bytesToEncode];
        byte[] temp = new byte[bytesToEncode];

        //append 0 ,temp array
        for (int i=0 ; i<bytesToEncode ; ++i ){
            if (i < data.length){
                temp[i]=data[i];
            }
            else{
                temp[i]=0;
            }
        }

        int i = 0;
        while (i < temp.length) {
            for (int j = 0; j < 16; ++j) {
                block[j] = temp[i++];
            }

            block = this.encryptBlock(block);
            System.arraycopy(block, 0, result, i - 16, block.length);
        }

        return result;

    }


    private byte[] encryptBlock(byte[] completeBlock) throws Exception {
        byte[] tmp = new byte[completeBlock.length];
        System.arraycopy(completeBlock, 0, tmp, 0, completeBlock.length);

        addRoundKey(tmp,0);

        for (int i=1; i<Nr; i++) {
            subBytes(tmp);
            shiftRows(tmp);
            mixColumns(tmp);
            addRoundKey(tmp,i);
        }

        subBytes(tmp);
        shiftRows(tmp);
        addRoundKey(tmp,Nr);

        return tmp;
    }

    private byte[] xorKeyWords(byte[] keyWord1 , byte[] keyWord2){
        if (keyWord1.length == keyWord2.length){
            byte[] tmp=new byte[keyWord1.length];
            for (int i=0; i<keyWord1.length; i++){
                tmp[i]=(byte) (keyWord1[i]^keyWord2[i]);
            }
            return tmp;
        }
        else{
            return null;
        }
    }
    private byte[][] generateSubKey(byte[] inputKey) {
        int totalWords=4*(Nr+1);
        byte[][] tmp=new byte[totalWords][4];
        int b=0;
        for (int i=0; i<Nkw; i++){
            for (int j=0; j<4; j++){
                tmp[i][j]=inputKey[b++];
            }
        }

        for (int i = Nkw; i < totalWords; i++) {
            byte[] temp=new byte[4];
            System.arraycopy(tmp[i-1], 0, temp, 0, 4);

            if(i%Nkw==0){
                temp = g(temp,i/Nkw);
            }
            else if(Nkw == 8 && i%Nkw==4){
               temp = subWord(temp);
            }
            tmp[i] = xorKeyWords(tmp[i-Nkw],temp);
        }

        return tmp;
    }

    private byte[] subWord(byte[] word){
        byte[] tmp=new byte[4];
        for  (int i=0; i<4; i++){
            tmp[i]=SBox_RCON.subBytes(word[i]);
        }
        return tmp;
    }

    private byte[] g(byte[] word,int round) {
        byte[] temp = new  byte[4];
        System.arraycopy(word, 0, temp, 0, 4);

        temp[0] = SBox_RCON.subBytes(word[1]);
        temp[1] = SBox_RCON.subBytes(word[2]);
        temp[2] = SBox_RCON.subBytes(word[3]);
        temp[3] = SBox_RCON.subBytes(word[0]);


        temp[0] = (byte) (temp[0] ^ RCON[round]);
        return temp;
    }





    private void subBytes(byte[] block){
        for (int i=0;i<block.length;i++){
            block[i]=SBox_RCON.subBytes(block[i]);
        }
    }
    private void shiftRows(byte[] block) {
        byte tmp;

        // wiersz 0 ozostaje bez zmian

        // wiersz 1 przesunięcie o 1 miejsce w lewo
        tmp = block[1];
        block[1] = block[5];
        block[5] = block[9];
        block[9] = block[13];
        block[13] = tmp;

        // wiersz 2 przesunięcie o 2 miejsca w lewo
        tmp = block[2];
        block[2] = block[10];
        block[10] = tmp;
        tmp = block[6];
        block[6] = block[14];
        block[14] = tmp;

        // wiersz 3 przesunięcie o 3 miejsca w lewo
        tmp = block[15];
        block[15] = block[11];
        block[11] = block[7];
        block[7] = block[3];
        block[3] = tmp;
    }




    private void mixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;

            byte a0 = state[i];
            byte a1 = state[i + 1];
            byte a2 = state[i + 2];
            byte a3 = state[i + 3];

            state[i]     = (byte) (mul2(a0) ^ mul2(a1) ^ a1 ^ a2 ^ a3);
            state[i + 1] = (byte) (a0 ^ mul2(a1) ^ mul2(a2) ^ a2 ^ a3);
            state[i + 2] = (byte) (a0 ^ a1 ^ mul2(a2) ^ mul2(a3) ^ a3);
            state[i + 3] = (byte) (mul2(a0) ^ a0 ^ a1 ^ a2 ^ mul2(a3));
        }
    }






}
