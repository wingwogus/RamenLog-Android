package kkj.mjc.ramenlog.request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class EmailVerificationRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/auth/sendEmail";

    public EmailVerificationRequest(String email,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
        super(Method.POST, URL, buildRequestBody(email), listener, errorListener);
    }

    private static JSONObject buildRequestBody(String email) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
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