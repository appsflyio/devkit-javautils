package io.appsfly.core;

import io.appsfly.crypto.CtyptoUtil;
import io.appsfly.util.json.JSONObject;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import java.io.IOException;

public class AppInstance {

    public static class AFConfig {

        String repoUrl;
        String secretKey;
        String appId;

        public AFConfig(String repoUrl, String secretKey, String appId) {
            this.repoUrl = repoUrl;
            this.secretKey = secretKey;
            this.appId = appId;
        }

    }

    private AFConfig config;
    private String microModuleId;

    public AppInstance(AFConfig config, String microModuleId) {
        this.config = config;
        this.microModuleId = microModuleId;
    }


    public void exec(final String intent, final JSONObject intentData, final Callback callback){
        final JSONObject body = new JSONObject() {{
            put("intent", intent);
            put("data", intentData);
        }};

        JSONObject payload = new JSONObject() {{
            put("body", body);
            put("module-id", microModuleId);
        }};
        String checksum = CtyptoUtil.getInstance().getChecksum(payload.toString().getBytes(), config.secretKey);

        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        asyncHttpClient.preparePost(this.config.repoUrl+"/exec")
                .addHeader("x-module-id", microModuleId)
                .addHeader("x-app-id", config.appId)
                .addHeader("x-checksum", checksum)
                .setBody(body.toString().getBytes())
                .execute(new AsyncCompletionHandler<Integer>(){

                    @Override
                    public Integer onCompleted(Response response) throws Exception{
                        String checksum = response.getHeaders().get("x-checksum");
                        boolean verifychecksum = CtyptoUtil.getInstance().verifychecksum(response.getResponseBody().getBytes(), checksum, config.secretKey);
                        if (verifychecksum){
                            callback.onResponse(new JSONObject(response.getResponseBody()));
                        }
                        else{
                            callback.onError(new JSONObject(){{
                                put("message", "Checksum Validation Failed");
                            }});
                        }
                        return response.getStatusCode();
                    }

                    @Override
                    public void onThrowable(Throwable t){
                        // Something wrong happened.
                    }
                });
    }
}
