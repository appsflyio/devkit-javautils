# appsfly.io_Dev_Kit_Java_Utils

Use below method of jar to generate checksum and verify checksum as per your JDK version

  1. Generate Checksum For Transaction Request:  
    public String getChecksum(String Key, TreeMap<String, String> paramap)
  2. Verify Checksum:  
    public boolean verifychecksum(String masterKey, TreeMap<String, String>  paramap,String responseCheckSumString)
