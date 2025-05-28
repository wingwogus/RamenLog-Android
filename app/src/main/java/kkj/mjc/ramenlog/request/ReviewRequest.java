package kkj.mjc.ramenlog.request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReviewRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/reviews/";

    public ReviewRequest(
            Long restaurantId,
            Double rating,
            String content,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener
    ) {
        super(Method.POST,
                URL,
                buildRequestBody(restaurantId, rating, content),
                listener,
                errorListener);
    }

    private static JSONObject buildRequestBody(Long restaurantId, Double rating, String content) {
        JSONObject body = new JSONObject();
        try {
            body.put("restaurantId", restaurantId);
            body.put("rating", rating);
            body.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        // 서버에서 JSON 으로 받아들이도록 Content-Type 설정
        headers.put("Content-Type", "application/json; charset=utf-8");

        return headers;
    }
}
