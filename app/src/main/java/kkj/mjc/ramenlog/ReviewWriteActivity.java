package kkj.mjc.ramenlog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kkj.mjc.ramenlog.request.ReviewRequest;

public class ReviewWriteActivity extends AppCompatActivity {
    private static final int REQ_PICK_IMAGES = 1001;
    private LinearLayout btnuploadphoto;
    private LinearLayout btnsubmitreview;
    private RatingBar ratingBar;
    private EditText edtReview;
    private Long restaurantId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        ratingBar  = findViewById(R.id.ratingBar);
        edtReview  = findViewById(R.id.edt_Review);
        btnsubmitreview = findViewById(R.id.btn_submit_review);
        btnuploadphoto = findViewById(R.id.btn_upload_photo);


        // 1) 사진 업로드 버튼
        btnuploadphoto.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(i, "사진 선택"), REQ_PICK_IMAGES);
        });

        // 2) 리뷰 등록 버튼
        btnsubmitreview.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage("리뷰를 등록하시겠습니까?")
                    .setPositiveButton("확인", (d, w) -> uploadReview())
                    .setNegativeButton("취소", null)
                    .show();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        // ✅ 이걸 먼저 호출해서 시각적 강조만 적용
        bottomNav.setSelectedItemId(R.id.nav_search);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_search) { // ✅ 현재 화면이 "리뷰 작성"이면 검색 탭 클릭도 무시
                // if (!(this instanceof SearchActivity)) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (id == R.id.nav_rank) {
                startActivity(new Intent(this, RankActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

    }

    private void uploadReview() {
        // 1) EditText 와 RatingBar 에서 값 꺼내기
        String content = edtReview.getText().toString().trim();
        float ratingFloat = ratingBar.getRating();
        Double rating = (double) ratingFloat;  // DTO 가 Double 이니까 변환

        // 2) DTO 에 세팅
        ReviewRequest dto = new ReviewRequest(restaurantId, rating, content,
                response -> {
                    try {
                        String data = response.getString("data");
                        Toast.makeText(this, "리뷰 성공", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {


                    }

                },
                        error -> {

                        });

        RequestQueue queue = Volley.newRequestQueue(ReviewWriteActivity.this);
        queue.add(dto);

    }


}
