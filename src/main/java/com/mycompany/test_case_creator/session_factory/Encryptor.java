/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.test_case_creator.session_factory;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author joseramon.gago
 */
public class Encryptor {  
    private final String publicModulus = "8a0325982a4f6c8df358d4ff26f1ad5f9c819ae63f9d0553b8355b6a6f3ecab681b5ac518604c42645081ee98e28a9ccbbf49c6a490daa7ebf9d5ea67e8a06dc896028d9b1d990f67044f9dd7cc0a2d7d58da23e6c6448111ceab99a6ac680b3de303195305bd86af9989e83ed88495fbb1a51b1bf9635cf3fb463597adc90a2bdd68c87226144607795459dd9cad2c6e823d16634c24d38df455e9cfb3d2e4621711a52cfb3fc88e41b76cbf4f7ef6e887f26baeae6ce56d08101cb811cde84fc7117e83280b1765212c625817e6c38bb7c596829486b0cfa10a2fa55e0511963e750b248699e4c33263a72fe365f106e6dccb7df9230748fd0dfca645ceb49";
    private final String publicExponent = "10001";
    private final String prefix = "ENCRYPTED:";
    private PublicKey publicKey;

    private BigInteger n = null;
    private Integer e = 0;

    public Encryptor() {
        this.n = null;
        this.e = 0;
    }

    public String encrypt(String inputString) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        createPublicKey();
        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = c.doFinal(inputString.getBytes());
        return prefix + bytesToHex(encryptedData);
    }
    
    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    private void createPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger keyInt = new BigInteger(publicModulus, 16);
        BigInteger exponentInt = new BigInteger(publicExponent, 16);
        RSAPublicKeySpec keySpeck = new RSAPublicKeySpec(keyInt, exponentInt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(keySpeck);
    }
}
