package kkj.mjc.ramenlog.like;

import static com.android.volley.toolbox.Volley.newRequestQueue;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import kkj.mjc.ramenlog.DetailActivity;
import kkj.mjc.ramenlog.R;
import kkj.mjc.ramenlog.request.LikeListRequest;

public class LikeFragment extends Fragment {
    private RecyclerView recyclerView;
    private LikeItemAdapter adapter;
    private List<LikeItem> likeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 레이아웃 로드
        View view = inflater.inflate(R.layout.fragment_like, container, false);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recycler_like);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 어댑터 연결
        adapter = new LikeItemAdapter(likeList);
        recyclerView.setAdapter(adapter);

        // 상세 페이지 이동
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("restaurantId", item.getId());
            startActivity(intent);
        });

        // JWT 토큰 확인
        Bundle args = getArguments();
        if (args != null) {
            String token = args.getString("token");
            if (token != null) {
                // 서버 요청
                LikeListRequest request = new LikeListRequest(
                    token,
                    response -> {
                        try {
                            // JSON 파싱
                            JSONArray data = response.getJSONArray("data");
                            likeList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                JSONObject address = item.getJSONObject("address");
                                String fullAddress = address.getString("fullAddress");
                                likeList.add(
                                        new LikeItem(
                                        item.getLong("id"),
                                        item.getString("name"),
                                        fullAddress,
                                        item.getString("imageUrl"),
                                        item.getDouble("avgRating")
                                ));
                            }
                            // 리스트 갱신
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            // 예외 로그 출력
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            int status = error.networkResponse.statusCode;
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            // 서버 에러 응답 처리
                            try {
                                JSONObject errJson = new JSONObject(body);
                                String serverMsg = errJson.optString("message");
                                Toast.makeText(getContext(), serverMsg, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "서버 오류: " + status, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 네트워크 문제 처리
                            Toast.makeText(getContext(), "네트워크 연결을 확인하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                );
                // 요청 큐 등록
                RequestQueue queue = newRequestQueue(requireContext());
                queue.add(request);
            }
        }

        return view;
    }
}