package kkj.mjc.ramenlog.mylog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kkj.mjc.ramenlog.R;
import kkj.mjc.ramenlog.request.ReviewListRequest;

public class MyLogFragment extends Fragment {
    private RecyclerView recycler;
    private MyLogAdapter adapter;
    private List<ReviewItem> reviewItemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mylog, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recycler = view.findViewById(R.id.recycler_mylog);
        // 1) 레이아웃 매니저
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // 2) 스크롤뷰 안에 있을 때 스무스하게
        recycler.setNestedScrollingEnabled(false);
        // 어댑터 연결
        adapter = new MyLogAdapter(reviewItemList);
        recycler.setAdapter(adapter);

        loadMyReviews();
    }
    private void loadMyReviews() {
        Bundle args = getArguments();
        if (args != null) {
            String token = args.getString("token");
            if(token != null) {
                JsonObjectRequest req = new ReviewListRequest(
                        token,
                        response -> {
                            try {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    String name = obj.getString("restaurantName");
                                    double rating = obj.getDouble("rating");
                                    String content = obj.getString("content");

                                    // images 배열이 있으면 최대 3개까지
                                    String url1 = null, url2 = null, url3 = null;
                                    if (obj.has("images")) {
                                        JSONArray imgs = obj.getJSONArray("images");
                                        if (imgs.length() > 0) url1 = imgs.getString(0);
                                        if (imgs.length() > 1) url2 = imgs.getString(1);
                                        if (imgs.length() > 2) url3 = imgs.getString(2);
                                    }

                                    reviewItemList.add(new ReviewItem(
                                            name, rating,
                                            url1, url2, url3,
                                            content
                                    ));
                                }
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            error.printStackTrace();
                        }
                );

                Volley.newRequestQueue(requireContext())
                        .add(req);
            }
        }
    }
}