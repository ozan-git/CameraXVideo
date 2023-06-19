package com.example.android.camerax.video.core.vimeo;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class VimeoResponse {

    private final JSONObject json;
    private final JSONObject headers;
    private final int statusCode;

    public VimeoResponse(JSONObject json, JSONObject headers, int statusCode) {
        this.json = json;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    public JSONObject getJson() {
        return json;
    }

    public JSONObject getHeaders() {
        return headers;
    }

    public int getRateLimit() throws JSONException {
        return getHeaders().getInt("X-RateLimit-Limit");
    }

    public int getRateLimitRemaining() throws JSONException {
        return getHeaders().getInt("X-RateLimit-Remaining");
    }

    public String getRateLimitReset() throws JSONException {
        return getHeaders().getString("X-RateLimit-Reset");
    }

    public int getStatusCode() {
        return statusCode;
    }

    @NonNull
    public String toString() {
        try {
            return "HTTP Status Code: \n" + getStatusCode() + "\nJson: \n" + getJson().toString(2) + "\nHeaders: \n" + getHeaders().toString(2);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}