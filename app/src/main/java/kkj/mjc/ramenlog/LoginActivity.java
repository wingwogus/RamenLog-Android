package kkj.mjc.ramenlog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import kkj.mjc.ramenlog.request.LoginRequest;

public class LoginActivity extends AppCompatActivity {

    private EditText etId, etPasswd;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId = findViewById(R.id.etId);
        etPasswd = findViewById(R.id.etPasswd);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // 회원가입 버튼 클릭 시 회원가입 화면으로 넘어감
        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 로그인 버튼 클릭 시
        btnLogin.setOnClickListener(view -> {
            String email = etId.getText().toString();
            String password = etPasswd.getText().toString();

            LoginRequest request = new LoginRequest(
                    email,
                    password,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            if (!success) {
                                String msg = response.getString("message");
                                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 토큰 추출
                            JSONObject data = response.getJSONObject("data");
                            String token = data.getString("accessToken");

                            // 토큰 저장
                            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                            prefs.edit().putString("accessToken", token).apply();

                            // 메인 화면 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            int status = error.networkResponse.statusCode;
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            // 서버 응답 파싱
                            try {
                                JSONObject errJson = new JSONObject(body);
                                String serverMsg = errJson.optString("message");
                                Toast.makeText(this, serverMsg, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                // 서버 오류 상태 출력
                                Toast.makeText(this, "서버 오류: " + status, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 네트워크 오류
                            Toast.makeText(this, "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(request);
        });
    }
}