package io.appsfly.crypto;

import java.io.UnsupportedEncodingException;


public class App {
    public static void main(String args[]){
            //byte[] test = new CtyptoUtil().encrypt("test".getBytes(), "1234567890123456");
            //System.out.println(new String(new CtyptoUtil().decrypt(test, "1234567890123456"), "UTF-8"));
        String key = "1234567890123456";
        String checksum = CtyptoUtil.getInstance().getChecksum("testing".getBytes(), key);
        System.out.println(CtyptoUtil.getInstance().verifychecksum("testing".getBytes(), checksum, key));
    }
}
