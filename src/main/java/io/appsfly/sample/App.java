package io.appsfly.sample;

import io.appsfly.core.AppInstance;
import io.appsfly.core.Callback;
import io.appsfly.util.json.JSONObject;

public class App {

    public static void main(String args[]){
        org.apache.log4j.BasicConfigurator.configure();

        AppInstance.AFConfig config = new AppInstance.AFConfig("http://testurl", "1234567890123456", "dcbbank");

        AppInstance clearTrip = new AppInstance(config, "com.cleartrip.microservices.local");
        clearTrip.exec("doBooking", new JSONObject() {{
            //Set Params Here
            // We will take care of checksum
        }}, new Callback() {
            @Override
            public void onResponse(JSONObject response) {
                // Payment Done Response
                // We have already verified the checksum from you
            }

            @Override
            public void onError(JSONObject error) {
                // On Error
            }
        });


        AppInstance bookMyShow = new AppInstance(config, "com.cleartrip.microservices.default");
        bookMyShow.exec("doBooking", new JSONObject() {{
            //Set Params Here
            // We will take care of checksum
        }}, new Callback() {
            @Override
            public void onResponse(JSONObject response) {
                // Payment Done Response
                // We have already verified the checksum from you
            }

            @Override
            public void onError(JSONObject error) {
                // On Error
            }
        });


    }
}
