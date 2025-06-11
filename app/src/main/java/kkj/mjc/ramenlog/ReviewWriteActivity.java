package kkj.mjc.ramenlog;

import android.content.Intent;
import android.content.SharedPreferences;
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

import kkj.mjc.ramenlog.request.ReviewRequest;

public class ReviewWriteActivity extends AppCompatActivity {
    private static final int REQ_PICK_IMAGES = 1001;
    private LinearLayout btnuploadphoto;
    private LinearLayout btnsubmitreview;
    private RatingBar ratingBar;
    private EditText edtReview;
    private Long restaurantId;

    private TextView tvName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        ratingBar  = findViewById(R.id.ratingBar);
        edtReview  = findViewById(R.id.edt_Review);
        btnsubmitreview = findViewById(R.id.btn_submit_review);
        btnuploadphoto = findViewById(R.id.btn_upload_photo);
        tvName = findViewById(R.id.restaurant_name);

        Intent intent = getIntent();
        restaurantId = intent.getLongExtra("restaurantId", -1L);
        tvName.setText(intent.getStringExtra("restaurantName"));

        // 사진 업로드 버튼
        btnuploadphoto.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(i, "사진 선택"), REQ_PICK_IMAGES);
        });

        // 리뷰 등록 버튼
        btnsubmitreview.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage("리뷰를 등록하시겠습니까?")
                    .setPositiveButton("확인", (d, w) -> uploadReview()) // 확인 클릭 시 리뷰 업로드
                    .setNegativeButton("취소", null)
                    .show();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // 하단 네비게이션 메뉴 클릭 시 화면 이동 처리
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, FindMapActivity.class));
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
        // 현재 선택된 메뉴는 검색 아이콘으로 설정
        bottomNav.setSelectedItemId(-1);
    }

    private void uploadReview() {
        // EditText 와 RatingBar 에서 값 꺼내기
        String content = edtReview.getText().toString().trim();
        float ratingFloat = ratingBar.getRating();
        Double rating = (double) ratingFloat;  // DTO 가 Double 이니까 변환

        // 토큰 가져오기
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("accessToken", null);
        if (token == null) {
            Toast.makeText(this, "로그인 필요", Toast.LENGTH_SHORT).show();
            return;
        }
        // 리뷰 내용 정보를 요청하는 ReviewRequest
        ReviewRequest dto = new ReviewRequest(
                token,restaurantId, rating, content,
                response -> {
                    try {
                        String data = response.getString("data");
                        Toast.makeText(this, "리뷰 성공", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "리뷰 실패", Toast.LENGTH_SHORT).show();
                });
        RequestQueue queue = Volley.newRequestQueue(ReviewWriteActivity.this);
        queue.add(dto);
    }
}
