package kkj.mjc.ramenlog.request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/auth/login";

    public LoginRequest(String email, String password,
                        Response.Listener<JSONObject> listener,
                        Response.ErrorListener errorListener) {
        super(Method.POST, URL, buildRequestBody(email, password), listener, errorListener);
    }

    private static JSONObject buildRequestBody(String email, String password) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}