package kkj.mjc.ramenlog.mylog;

import android.graphics.Typeface;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kkj.mjc.ramenlog.R;
import kkj.mjc.ramenlog.request.ReviewListRequest;
@RequiresApi(api = Build.VERSION_CODES.O)
public class MyLogFragment extends Fragment {
    private RecyclerView recycler;

    private TextView tvSortByDate, tvSortByRating;
    private MyLogAdapter adapter;
    private List<ReviewItem> reviewItemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mylog, container, false);

        recycler = view.findViewById(R.id.recycler_mylog);
        tvSortByDate = view.findViewById(R.id.tvSortByDate);
        tvSortByRating = view.findViewById(R.id.tvSortByRating);
        // 1) 레이아웃 매니저
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        // 어댑터 연결
        adapter = new MyLogAdapter(reviewItemList);
        recycler.setAdapter(adapter);

        tvSortByDate.setOnClickListener(v -> {
            // Sort by date (latest first)
            reviewItemList = reviewItemList.stream()
                    .sorted((a, b) -> {
                        java.time.LocalDateTime dateA = java.time.LocalDateTime.parse(a.getCreatedAt());
                        java.time.LocalDateTime dateB = java.time.LocalDateTime.parse(b.getCreatedAt());
                        return dateB.compareTo(dateA);
                    })
                    .collect(java.util.stream.Collectors.toList());
            adapter.notifyDataSetChanged();

            tvSortByDate.setTypeface(null, Typeface.BOLD);
            tvSortByRating.setTypeface(null, Typeface.NORMAL);
        });

        tvSortByRating.setOnClickListener(v -> {
            reviewItemList = reviewItemList.stream()
                    .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                    .collect(java.util.stream.Collectors.toList());

            tvSortByDate.setTypeface(null, Typeface.BOLD);
            tvSortByRating.setTypeface(null, Typeface.NORMAL);

            adapter.notifyDataSetChanged();
        });


        loadMyReviews();

        return view;
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
                                reviewItemList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    String name = obj.getString("restaurantName");
                                    double rating = obj.getDouble("rating");
                                    String content = obj.getString("content");
                                    String createdAt = obj.getString("createdAt");

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
                                            content, createdAt
                                    ));
                                }
                                reviewItemList = reviewItemList.stream()
                                        .sorted((a, b) -> {
                                            LocalDateTime dateA = LocalDateTime.parse(a.getCreatedAt());
                                            LocalDateTime dateB = LocalDateTime.parse(b.getCreatedAt());
                                            return dateB.compareTo(dateA); // 최신순
                                        })
                                        .collect(Collectors.toList());

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