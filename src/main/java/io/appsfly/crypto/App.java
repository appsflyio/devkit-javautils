package io.appsfly.crypto;

public class App {
    public static void main(String args[]){
        String key = "1234567890123456";
        String checksum = CtyptoUtil.getInstance().getChecksum("{\"hi\":\"hello\"}".getBytes(), key);
        System.out.println(CtyptoUtil.getInstance().verifychecksum("{\"hi\":\"hello\"}".getBytes(), checksum, key));
    }
}
