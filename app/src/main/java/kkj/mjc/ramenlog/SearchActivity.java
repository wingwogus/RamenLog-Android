package kkj.mjc.ramenlog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import kkj.mjc.ramenlog.dto.ApiResponse;
import kkj.mjc.ramenlog.dto.Restaurant;
import kkj.mjc.ramenlog.service.RestaurantService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 검색 기능을 제공하는 액티비티
public class SearchActivity extends AppCompatActivity {

    // UI 컴포넌트
    private AutoCompleteTextView searchBox;
    private ImageView backButton;
    private ListView resultList;
    private ArrayAdapter<Restaurant> adapter;
    private Drawable clearDrawable;
    private TextView storeTitle;

    // Context 및 Retrofit 관련 서비스 객체
    private Context context;
    private RestaurantService restaurantService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Retrofit 초기화 (로컬 서버 접속)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restaurantService = retrofit.create(RestaurantService.class);

        // Context 및 뷰 요소 바인딩
        context = this;
        searchBox = findViewById(R.id.searchBox);
        backButton = findViewById(R.id.backButton);
        resultList = findViewById(R.id.resultList);
        storeTitle = findViewById(R.id.storeTitle);

        // 검색창 오른쪽에 붙는 텍스트 clear 버튼 drawable 설정
        clearDrawable = ContextCompat.getDrawable(this, R.drawable.ic_clear_circle);
        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());

        // 검색 결과를 표시할 리스트뷰 어댑터 설정
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        resultList.setAdapter(adapter);

        // 검색 결과 항목 클릭 시 상세화면으로 이동
        resultList.setOnItemClickListener((parent, view, position, id) -> {
            Restaurant selected = adapter.getItem(position);
            if (selected != null) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("restaurantId", selected.getId());
                startActivity(intent);
            }
        });

        // 검색창 및 뒤로가기 버튼 설정
        setupSearchBox();
        setupBackButton();
    }

    // 검색창 관련 기능 설정
    private void setupSearchBox() {
        updateClearIcon(); // 초기 아이콘 설정

        // 텍스트 변경 감지 후 처리
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            // 텍스트 변경 후 동작 정의
            @Override
            public void afterTextChanged(Editable s) {
                updateClearIcon(); // clear 버튼 노출 여부 갱신
                if (s.length() >= 1) {
                    fetchSearchSuggestions(s.toString()); // 검색 수행
                    storeTitle.setVisibility(TextView.VISIBLE); // 결과 타이틀 표시
                } else {
                    storeTitle.setVisibility(TextView.GONE); // 타이틀 숨김
                    adapter.clear(); // 결과 초기화
                }
            }
        });

        // 검색창 오른쪽 clear 버튼 터치 감지
        searchBox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP &&
                    searchBox.getCompoundDrawables()[2] != null) {
                int drawableWidth = clearDrawable.getBounds().width();
                // clear 버튼 영역 터치 시 텍스트 제거
                if (event.getX() >= (searchBox.getWidth() - searchBox.getPaddingEnd() - drawableWidth)) {
                    searchBox.setText("");
                    updateClearIcon();
                    return true;
                }
            }
            return false;
        });
    }

    // 검색창 오른쪽에 clear 버튼을 동적으로 표시 또는 제거
    private void updateClearIcon() {
        if (searchBox.getText().length() > 0) {
            searchBox.setCompoundDrawables(null, null, clearDrawable, null);
        } else {
            searchBox.setCompoundDrawables(null, null, null, null);
        }
    }

    // 뒤로가기 버튼 동작 설정: 현재 액티비티 종료
    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish());
    }

    // 검색 키워드를 기반으로 맛집 목록을 검색하여 리스트뷰에 반영
    private void fetchSearchSuggestions(String keyword) {
        restaurantService.search(keyword).enqueue(new Callback<ApiResponse<List<Restaurant>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Restaurant>>> call, Response<ApiResponse<List<Restaurant>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Restaurant> restaurants = response.body().getData();
                    adapter.clear();
                    adapter.addAll(restaurants);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Restaurant>>> call, Throwable t) {
                Log.e("API_ERROR", "검색 실패: " + t.getMessage());
            }
        });
    }
}