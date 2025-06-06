package kkj.mjc.ramenlog.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LikeListRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/likes/";
    private final String jwtToken;

    public LikeListRequest(String jwtToken,
                        Response.Listener<JSONObject> listener,
                        Response.ErrorListener errorListener) {
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
