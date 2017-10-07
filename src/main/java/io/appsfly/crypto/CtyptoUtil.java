/**
 * Copyright 2017 appsfly.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.appsfly.crypto;



import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
            return encrypted;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private byte [] decrypt(byte [] data,String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec("@@@@&&&&####$$$$".getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(data);

            return original;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private byte[] getSalt(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private byte[] getBytes(byte[] salt, byte[] data, String algo) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algo);
            digest.update(ArrayUtils.addAll(salt, data));
            return getHexBytes(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getHexBytes(byte[] digested) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digested.length; i++) {
            sb.append(Integer.toString((digested[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString().getBytes();
    }

    private byte[] sha256sum(byte [] salt, byte [] data){
        return getBytes(salt, data, "SHA-256");
    }

    public String getChecksum(byte [] data, String key){
        byte[] salt = getSalt(4);
        byte[] encrypt = encrypt(ArrayUtils.addAll(sha256sum(salt, data), salt), key);
        return new String(Base64.encodeBase64(encrypt));
    }

    public boolean verifychecksum(byte [] data, String checksumParam, String key){
        byte[] checksum = decrypt(Base64.decodeBase64(checksumParam.getBytes()), key);
        byte[] salt = Arrays.copyOfRange(checksum, checksum.length-4, checksum.length);
        byte[] sha256 = Arrays.copyOfRange(checksum, 0, checksum.length-4);
        System.out.println(new String(salt));
        System.out.println(new String(sha256));
        return Arrays.equals(sha256,sha256sum(salt,data));
    }

}
