package io.appsfly.crypto;

public class App {
    public static void main(String args[]){
        String key = "1234567890123456";
//        String checksum = CtyptoUtil.getInstance().getChecksum("{\"hi\":\"hello\"}".getBytes(), key);
        String checksum = "TSQmmubgzyBo48lEpTDlVKt+PRM9b64F0NDskym4jInK7KPbiVisU2o734LpRwydWT39VcCww+njhqyL+3WYukkZf9CCeSQ6uW4eWgDSc/0=";
        System.out.println(CtyptoUtil.getInstance().verifychecksum("{\"hi\":\"hello\"}".getBytes(), checksum, key));
    }
}
