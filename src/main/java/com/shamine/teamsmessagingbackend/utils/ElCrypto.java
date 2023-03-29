package com.shamine.teamsmessagingbackend.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

class ElCrypto
{
    static String authCrypto(String subject, String sT, boolean encryption)
    {
        try
        {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");

            String myString = "vPysRPQ6wgFF5la/PlWLQxd2Fiklv+BwAbW0CVIeX7g=";

            String builder = sT + myString + sT + myString;
            byte[] key = sha.digest(builder.getBytes());

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(Arrays.toString(key).toCharArray(), myString.getBytes(), 1000, 256);
            SecretKeySpec authKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            IvParameterSpec IVPar1 = new IvParameterSpec(new byte[16]);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            if (encryption)
            {
                cipher.init(Cipher.ENCRYPT_MODE, authKey, IVPar1);
                return Base64.getEncoder().encodeToString(cipher.doFinal(subject.getBytes(StandardCharsets.UTF_8)));
            }
            else
            {
                cipher.init(Cipher.DECRYPT_MODE, authKey, IVPar1);
                return new String(cipher.doFinal(Base64.getDecoder().decode(subject)));
            }
        }
        catch (Exception e)
        {
            return (encryption ? "Encryption Error: " : "Decryption Error: ") + e.toString();
        }
    }
}