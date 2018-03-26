package io.appsfly.sample;

import io.appsfly.core.AppInstance;
import io.appsfly.core.Callback;
import io.appsfly.util.json.JSONObject;

public class App {

    public static void main(String args[]) {
        org.apache.log4j.BasicConfigurator.configure();

        AppInstance.AFConfig config = new AppInstance.AFConfig("https://microapps.appsfly.io", "3384354330428323", "abb3f71c-a8cc-4f2a-90aa-23ac3771f5f7");
        AppInstance clearTrip = new AppInstance(config, "io.appsfly.msctpactivities");
        clearTrip.exec("fetch_cities", new JSONObject() {{
            this.put("city_name", "shimla");
        }}, new Callback() {
            @Override
            public void onResponse(Object response) {
                System.out.println(response);
                // Payment Done Response
                // We have already verified the checksum from you
            }

            @Override
            public void onError(JSONObject error) {
                System.out.println(error);
            }
        });


//        AppInstanceJwt bookMyShow = new AppInstanceJwt(config, "com.cleartrip.microservices.default");
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
