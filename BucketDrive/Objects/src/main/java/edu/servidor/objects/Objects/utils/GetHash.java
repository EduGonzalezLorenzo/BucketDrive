package edu.servidor.objects.Objects.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class GetHash {
    public static void main(String[]args) throws NoSuchAlgorithmException {
        String text = "hola";
        String hash = hashString(text);
    }

    public static String hashString(String textToHash) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(textToHash.getBytes());
        byte[] digest = md.digest();
        return Arrays.toString(digest).toUpperCase();
    }
}
