package kkj.mjc.ramenlog.like;

import static com.android.volley.toolbox.Volley.newRequestQueue;

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
        View view = inflater.inflate(R.layout.fragment_like, container, false);

        recyclerView = view.findViewById(R.id.recycler_like);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LikeItemAdapter(likeList);
        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null) {
            String token = args.getString("token");
            if (token != null) {
                LikeListRequest request = new LikeListRequest(
                    token,
                    response -> {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            likeList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                JSONObject address = item.getJSONObject("address");
                                String fullAddress = address.getString("fullAddress");
                                likeList.add(
                                        new LikeItem(
                                        item.getInt("id"),
                                        item.getString("name"),
                                        fullAddress,
                                        item.getDouble("avgRating")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            int status = error.networkResponse.statusCode;
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            // 서버가 ErrorResponse 구조로 보냈다면 body에서 message 파싱 가능
                            try {
                                JSONObject errJson = new JSONObject(body);
                                String serverMsg = errJson.optString("message");
                                Toast.makeText(getContext(), serverMsg, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "서버 오류: " + status, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 네트워크 자체 문제
                            Toast.makeText(getContext(), "네트워크 연결을 확인하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                );
                RequestQueue queue = newRequestQueue(requireContext());
                queue.add(request);
            }
        }

        return view;
    }
}