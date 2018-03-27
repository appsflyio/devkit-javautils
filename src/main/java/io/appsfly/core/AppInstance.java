package io.appsfly.core;

import io.appsfly.crypto.CtyptoUtil;
import io.appsfly.jwt.JwtUtil;
import io.appsfly.util.json.JSONException;
import io.appsfly.util.json.JSONObject;
import io.appsfly.util.json.JSONTokener;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();
        Request.Builder url = new Request.Builder()
                .url(this.config.repoUrl + "/executor/exec")
                .addHeader("X-Module-Handle", microModuleId)
                .addHeader("X-App-Key", config.appKey)
                .addHeader("X-UUID", userID);
        Request request = getRequest(body, jwtUtil, url);
        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                System.out.println(e.getStackTrace());
                callback.onResponse(new JSONObject() {{
                    this.put("error", e.getMessage());
                }});
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String verified = null;
                if (config.secretKey != null) {
                    JwtUtil jwtUtil = new JwtUtil();
                    String token = response.body().string();
                    verified = jwtUtil.verifyCheckSum(token, config.secretKey);
                } else {
                    verified = response.body().string();
                }
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
            put("data", intentData);
            put("intent", intent);
        }};
        JwtUtil jwtUtil = new JwtUtil();
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();
        Request request;
        Request.Builder url = new Request.Builder()
                .url(this.config.repoUrl + "/executor/exec")
                .addHeader("X-Module-Handle", microModuleId)
                .addHeader("X-App-Key", config.appKey)
                .addHeader("X-UUID", userID);
        request = getRequest(body, jwtUtil, url);
        try {
            final Response response = httpClient.newCall(request).execute();
            String verified = null;
            if (config.secretKey != null) {
                String token = response.body().string();
                verified = jwtUtil.verifyCheckSum(token, config.secretKey);
            } else {
                verified = response.body().string();
            }

            if (null != verified) {
                return new JSONTokener(verified).nextValue();
            } else {
                if (config.secretKey == null) {
                    final JSONObject responseBody = new JSONObject(response.body().string());
                    if (responseBody.has("error")) {
                        throw new AppsflyException(responseBody.getJSONObject("error").getString("message"));
                    } else {
                        return responseBody;
                    }
                } else {
                    return new JSONObject();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request getRequest(JSONObject body, JwtUtil jwtUtil, Request.Builder url) {
        Request request;
        if (config.secretKey != null) {
            String checksum = jwtUtil.generateChecksum(body.toString(), config.secretKey);
            url.addHeader("X-Encrypted", "true");
            url.addHeader("Content-Type", "application/json");
            request = url
                    .post(RequestBody.create(RAW, checksum))
                    .build();
        } else {
            request = url.post(RequestBody.create(JSON, body.toString())).build();
        }
        return request;
    }
}
