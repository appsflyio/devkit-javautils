package io.appsfly.sample;

import io.appsfly.core.AppInstance;
import io.appsfly.core.AppsflyException;
import io.appsfly.core.Callback;
import io.appsfly.util.json.JSONObject;

public class App {

    public static void main(String args[]) {
        org.apache.log4j.BasicConfigurator.configure();

        /**
         *Plain Request
         */
        AppInstance.AFConfig InSecureConfig = new AppInstance.AFConfig("XXXXXXXXXX");
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
        AppInstance.AFConfig secureConfig = new AppInstance.AFConfig("XXXXXXX", "XXXXXXXXXX");
        AppInstance clearTripSecure = new AppInstance(secureConfig, "XXXXXXXX");
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
