package io.appsfly.core;

import io.appsfly.crypto.CtyptoUtil;
import io.appsfly.jwt.JwtUtil;
import io.appsfly.util.json.JSONException;
import io.appsfly.util.json.JSONObject;
import io.appsfly.util.json.JSONTokener;
import okhttp3.*;

import java.io.IOException;

public class AppInstance {

    public static class AFConfig {

        String repoUrl;
        String secretKey;
        String appKey;

        public AFConfig(String repoUrl, String secretKey, String appKey) {
            this.repoUrl = repoUrl;
            this.secretKey = secretKey;
            this.appKey = appKey;
        }

    }

    private AFConfig config;
    private String microModuleId;
    public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType RAW = MediaType.parse("text/plain; charset=utf-8");

    public AppInstance(AFConfig config, String microModuleId) {
        this.config = config;
        this.microModuleId = microModuleId;
    }

    public void exec(final String intent, final JSONObject intentData, final Callback callback) {
        exec(intent, intentData, "generic", callback);
    }

    public void exec(final String intent, final JSONObject intentData, final String userID, final Callback callback) {
        final JSONObject body = new JSONObject() {{
            put("data", intentData);
            put("intent", intent);
        }};
        JwtUtil jwtUtil = new JwtUtil();

        String checksum = jwtUtil.generateChecksum(body.toString(), config.secretKey);
        OkHttpClient httpClient = new OkHttpClient();
        Request request;
         request = new Request.Builder()
                .url(this.config.repoUrl + "/executor/exec")
                .addHeader("X-Module-Handle", microModuleId)
                .addHeader("X-App-Key", config.appKey)
                .addHeader("X-UUID", userID)
                .addHeader("X-Encrypted", "true")
                .post(RequestBody.create(RAW, checksum))
                .build();
        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getStackTrace());
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                JwtUtil jwtUtil = new JwtUtil();
                String token = response.body().string();
                String verified = jwtUtil.verifyCheckSum(token, config.secretKey);
                    if (verified != null) {
                        try {
                            callback.onResponse(new JSONTokener(verified).nextValue());
                        } catch (JSONException e) {
                            callback.onResponse(new JSONObject());
                        }
                    } else {
                        callback.onError(new JSONObject() {{
                            put("message", "Checksum Validation Failed");
                        }});
                    }
            }
        });
    }

    public Object execSync(final String intent, final JSONObject intentData) throws AppsflyException {
        return execSync(intent, intentData, "generic");
    }

    public Object execSync(final String intent, final JSONObject intentData, final String userID) throws AppsflyException {
        final JSONObject body = new JSONObject() {{
            put("intent", intent);
            put("data", intentData);
        }};
        String payload = body + "|" + microModuleId + "|" + config.appKey + "|" + userID;
        String checksum = CtyptoUtil.getInstance().getChecksum(payload.getBytes(), config.secretKey);
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.config.repoUrl + "/executor/exec")
                .addHeader("X-Module-Handle", microModuleId)
                .addHeader("X-App-Key", config.appKey)
                .addHeader("X-Checksum", checksum)
                .addHeader("X-UUID", userID)
                .post(RequestBody.create(JSON, body.toString()))
                .build();
        try {
            final Response response = httpClient.newCall(request).execute();
            String responseChecksum = response.headers().get("X-Checksum");
            if (responseChecksum != null) {
                byte[] bytes = response.body().bytes();
                boolean verified = CtyptoUtil.getInstance().verifychecksum(bytes, responseChecksum, config.secretKey);
                if (verified) {
                    return new JSONTokener(new String(bytes)).nextValue();
                } else {
                    throw new AppsflyException("Checksum Validation Failed");
                }
            } else {
                final JSONObject responseBody = new JSONObject(response.body().string());
                if (responseBody.has("error")) {
                    throw new AppsflyException(responseBody.getJSONObject("error").getString("message"));
                } else {
                    return responseBody;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
