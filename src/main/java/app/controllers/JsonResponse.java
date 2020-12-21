package app.controllers;

import com.fasterxml.jackson.annotation.JsonGetter;

public class JsonResponse {

    private String payload;
    private boolean success;

    public JsonResponse(boolean b, String s) {
        this.success = b;
        this.payload = s;
    }

    @JsonGetter("payload")
    public String getPayload() {
        return payload;
    }

    @JsonGetter("success")
    public boolean isSuccess() {
        return success;
    }
}
