package kkj.mjc.ramenlog.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kkj.mjc.ramenlog.R;
import kkj.mjc.ramenlog.ReviewWriteActivity;
import kkj.mjc.ramenlog.mylog.MyLogAdapter;
import kkj.mjc.ramenlog.mylog.ReviewItem;
import kkj.mjc.ramenlog.request.DetailRequest;
import kkj.mjc.ramenlog.request.DetailReviewRequest;
import kkj.mjc.ramenlog.request.ReviewListRequest;

public class DetailReviewFragment extends Fragment {
    TextView btnWriteReview;
    private RecyclerView recycler;
    private DetailReviewAdapter adapter;
    private List<DetailReviewItem> reviewItemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_review, container, false);

        recycler = view.findViewById(R.id.recycler_detail_review);
        // 1) 레이아웃 매니저
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        // 2) 스크롤뷰 안에 있을 때 스무스하게
        recycler.setNestedScrollingEnabled(false);
        // 어댑터 연결
        adapter = new DetailReviewAdapter(reviewItemList);
        recycler.setAdapter(adapter);

        loadMyReviews();

        // 리뷰 쓰기 버튼 클릭 시 이동
        btnWriteReview = view.findViewById(R.id.btn_write_review);
        btnWriteReview.setOnClickListener(v -> {
            Long currentRestaurantId = -1L;
            String currentRestaurantName = null;
            if (getArguments() != null) {
                Bundle arguments = getArguments();
                currentRestaurantId = arguments.getLong("restaurantId", -1L);
                currentRestaurantName = arguments.getString("restaurantName", "없음");
            }
            Intent intent = new Intent(getActivity(), ReviewWriteActivity.class);
            intent.putExtra("restaurantId", currentRestaurantId);// ⭐ 여기에 현재 화면에서 보고 있는 restaurantId 를 넘겨야 함
            intent.putExtra("restaurantName", currentRestaurantName);
            startActivity(intent);
        });
        return view;
    }
    private void loadMyReviews() {
        Bundle args = getArguments();
        if (args != null) {
            String token = args.getString("token");
            if(token != null) {
                JsonObjectRequest req = new DetailReviewRequest(
                        token,
                        response -> {
                            try {
                                JSONArray data = response.getJSONArray("data");
                                reviewItemList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    String name = obj.getString("nickname");
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

                                    reviewItemList.add(new DetailReviewItem(
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
