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

public class SearchActivity extends AppCompatActivity {

    private AutoCompleteTextView searchBox;
    private ImageView backButton;
    private ListView resultList;
    private ArrayAdapter<Restaurant> adapter;
    private Drawable clearDrawable;
    private TextView storeTitle;
    private Context context;

    private RestaurantService restaurantService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // ConstraintLayout 기반 XML

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restaurantService = retrofit.create(RestaurantService.class);

        context = this;

        searchBox = findViewById(R.id.searchBox);
        backButton = findViewById(R.id.backButton);
        resultList = findViewById(R.id.resultList);
        storeTitle = findViewById(R.id.storeTitle);

        clearDrawable = ContextCompat.getDrawable(this, R.drawable.ic_clear_circle);
        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());

        adapter = new ArrayAdapter<Restaurant>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        resultList.setAdapter(adapter);

        resultList.setOnItemClickListener((parent, view, position, id) -> {
            Restaurant selected = adapter.getItem(position);
            if (selected != null) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("restaurantId", selected.getId());
                startActivity(intent);
            }
        });

        setupSearchBox();
        setupBackButton();
    }

    private void setupSearchBox() {
        updateClearIcon();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateClearIcon();
                if (s.length() >= 1) {
                    fetchSearchSuggestions(s.toString());
                    storeTitle.setVisibility(TextView.VISIBLE);
                } else {
                    storeTitle.setVisibility(TextView.GONE);
                    adapter.clear();
                }
            }
        });

        // drawble 이미지의 반경범위를 지정해서 반경범위 안을 클릭하면 텍스트 삭제되게
        searchBox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP &&
                    searchBox.getCompoundDrawables()[2] != null) {
                int drawableWidth = clearDrawable.getBounds().width();
                if (event.getX() >= (searchBox.getWidth() - searchBox.getPaddingEnd() - drawableWidth)) {
                    searchBox.setText("");
                    updateClearIcon();
                    return true;
                }
            }
            return false;
        });
    }

    private void updateClearIcon() {
        if (searchBox.getText().length() > 0) {
            searchBox.setCompoundDrawables(null, null, clearDrawable, null);
        } else {
            searchBox.setCompoundDrawables(null, null, null, null);
        }
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish()); //내주변-찾기 화면으로 돌아가게
    }

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
