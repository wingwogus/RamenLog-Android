package kkj.mjc.ramenlog.request;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/restaurant/random";
    private final String jwtToken;

    public HomeRequest(String jwtToken, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        // POST 요청으로 super 호출
        super(Method.GET, URL, null, listener, errorListener);
        this.jwtToken = jwtToken;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}
