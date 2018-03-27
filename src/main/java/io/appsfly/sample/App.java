package io.appsfly.sample;

import io.appsfly.core.AppInstance;
import io.appsfly.core.Callback;
import io.appsfly.util.json.JSONObject;

public class App {

    public static void main(String args[]) {
        org.apache.log4j.BasicConfigurator.configure();

        /**
         *Plain Request
         */
        AppInstance.AFConfig InSecureConfig = new AppInstance.AFConfig("https://microapps.appsfly.io", null, "abb3f71c-a8cc-4f2a-90aa-23ac3771f5f7");
        AppInstance clearTripInsecure = new AppInstance(InSecureConfig, "io.appsfly.msctpactivities");
        clearTripInsecure.exec("fetch_cities", new JSONObject() {{
        }}, new Callback() {
            @Override
            public void onResponse(Object response) {
                System.out.println(response.toString());
                // Payment Done Response
                // We have already verified the checksum from you
            }

            @Override
            public void onError(JSONObject error) {
                System.out.println(error);
            }
        });

/**
 *JWT Request
 */
        AppInstance.AFConfig secureConfig = new AppInstance.AFConfig("https://microapps.appsfly.io", "3384354330428323", "abb3f71c-a8cc-4f2a-90aa-23ac3771f5f7");
        AppInstance clearTripSecure = new AppInstance(secureConfig, "io.appsfly.msctpactivities");
        clearTripSecure.exec("fetch_cities", new JSONObject() {{
        }}, new Callback() {
            @Override
            public void onResponse(Object response) {
                System.out.println(response.toString());
                // Payment Done Response
                // We have already verified the checksum from you
            }

            @Override
            public void onError(JSONObject error) {
                System.out.println(error);
            }
        });


    }
}
