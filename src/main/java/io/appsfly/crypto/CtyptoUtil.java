package io.appsfly.crypto;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;


public class CtyptoUtil {

    private static CtyptoUtil instance = new CtyptoUtil();

    public static CtyptoUtil getInstance(){
        return instance;
    }

    private byte [] encrypt(byte [] data,String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec("$$appsfly.io##$$".getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(data);
            return Base64.encodeBase64(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private byte [] decrypt(byte [] data,String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec("$$appsfly.io##$$".getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(data));

            return original;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getSalt(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeBase64(salt);
    }

    private byte[] getBytes(byte[] data, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(data);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] sha256sum(byte [] salt, byte [] data){
        return Hex.encodeHexString(getBytes(ArrayUtils.addAll(salt, data), "SHA-256")).getBytes();
    }

    public String getChecksum(byte [] data, String key){
        byte[] salt = getSalt(4);
        byte[] sha256sum = sha256sum(salt, data);
        byte[] checksum = ArrayUtils.addAll(sha256sum, salt);
        byte[] encrypt = encrypt(checksum, key);
        return new String(encrypt);
    }

    public boolean verifychecksum(byte [] data, String checksumParam, String key){
        byte[] checksum = decrypt(checksumParam.getBytes(), key);
        byte[] salt = Arrays.copyOfRange(checksum, checksum.length-4, checksum.length);
        byte[] sha256 = Arrays.copyOfRange(checksum, 0, checksum.length-4);
        return Arrays.equals(sha256,sha256sum(salt,data));
    }

}
