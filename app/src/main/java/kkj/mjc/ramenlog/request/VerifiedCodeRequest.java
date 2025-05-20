package kkj.mjc.ramenlog.request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class VerifiedCodeRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/auth/verification";

    public VerifiedCodeRequest(String email, String code,
                               Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener) {
        super(Method.POST, URL, buildRequestBody(email, code), listener, errorListener);
    }

    private static JSONObject buildRequestBody(String email, String code) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("code", code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}