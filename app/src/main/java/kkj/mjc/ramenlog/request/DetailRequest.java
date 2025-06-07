package kkj.mjc.ramenlog.request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/restuarant";
    private final String token;

    public DetailRequest(String token, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET, URL, null, listener, errorListener);
        this.token = token;
    }
    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        // 서버에서 JSON 으로 받아들이도록 Content-Type 설정
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}
