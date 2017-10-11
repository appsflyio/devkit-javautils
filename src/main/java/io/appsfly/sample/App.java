package io.appsfly.sample;

import io.appsfly.core.AppInstance;
import io.appsfly.core.Callback;
import io.appsfly.crypto.CtyptoUtil;
import io.appsfly.util.json.JSONObject;
import org.apache.commons.crypto.Crypto;

public class App {

    public static void main(String args[]){
        org.apache.log4j.BasicConfigurator.configure();

        AppInstance.AFConfig config = new AppInstance.AFConfig("https://microapps.appsfly.io", "1234567890123456", "92ae2562-aebc-468f-bc9e-aa3cdd9d39b1");
        AppInstance clearTrip = new AppInstance(config, "com.cleartrip.msactivities");
        clearTrip.exec("doBooking",  new JSONObject() {{
            //Set Params Here
            // We will take care of checksum
        }}, new Callback() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                // Payment Done Response
                // We have already verified the checksum from you
            }

            @Override
            public void onError(JSONObject error) {
                System.out.println(error);
            }
        });


//        AppInstance bookMyShow = new AppInstance(config, "com.cleartrip.microservices.default");
//        bookMyShow.exec("doBooking",new JSONObject() {{
//            //Set Params Here
//            // We will take care of checksum
//        }}, new Callback() {
//            @Override
//            public void onResponse(JSONObject response) {
//                // Payment Done Response
//                // We have already verified the checksum from you
//            }
//
//            @Override
//            public void onError(JSONObject error) {
//                // On Error
//            }
//        });


    }
}
