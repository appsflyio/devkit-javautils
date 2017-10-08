package io.appsfly.core;

import io.appsfly.util.json.JSONObject;

public interface Callback {
    void onResponse(JSONObject response);
    void onError(JSONObject error);
}
