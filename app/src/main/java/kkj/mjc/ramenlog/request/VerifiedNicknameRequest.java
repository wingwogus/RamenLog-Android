package kkj.mjc.ramenlog.request;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifiedNicknameRequest extends JsonObjectRequest {

    private static final String URL = "http://10.0.2.2:8080/api/auth/verification-nickname";

    public VerifiedNicknameRequest(String nickname,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        super(Method.POST, URL, buildRequestBody(nickname), listener, errorListener);
    }

    private static JSONObject buildRequestBody(String nickname) {
        JSONObject json = new JSONObject();
        try {
            json.put("nickname", nickname);
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
