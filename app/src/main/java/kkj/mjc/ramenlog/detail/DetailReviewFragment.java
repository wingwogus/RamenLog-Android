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

import kkj.mjc.ramenlog.R;
import kkj.mjc.ramenlog.ReviewWriteActivity;

public class DetailReviewFragment extends Fragment {
    TextView btnWriteReview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_review, container, false);

        // 리뷰 쓰기 버튼 클릭 시 이동
        btnWriteReview = view.findViewById(R.id.btn_write_review);
        btnWriteReview.setOnClickListener(v -> {
            Long currentRestaurantId = -1L;
            if (getArguments() != null) {
                currentRestaurantId = getArguments().getLong("restaurantId", -1L);
            }
            Intent intent = new Intent(getActivity(), ReviewWriteActivity.class);
            intent.putExtra("restaurantId", currentRestaurantId);  // ⭐ 여기에 현재 화면에서 보고 있는 restaurantId 를 넘겨야 함
            startActivity(intent);
        });
        return view;
    }
}
