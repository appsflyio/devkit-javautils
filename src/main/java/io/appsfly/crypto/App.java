package io.appsfly.crypto;

public class App {
    public static void main(String args[]){
        String key = "1234567890123456";
        String checksum = CtyptoUtil.getInstance().getChecksum("testing".getBytes(), key);
        System.out.println(CtyptoUtil.getInstance().verifychecksum("testing123".getBytes(), checksum, key));
    }
}
