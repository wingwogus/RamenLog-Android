package kkj.mjc.ramenlog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import kkj.mjc.ramenlog.request.SignUpRequest;
import kkj.mjc.ramenlog.request.VerifiedNicknameRequest;

public class RegisterActivity3 extends AppCompatActivity {

    private EditText etNickname;
    private Button btnCheckDuplicate, btnRegister;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register3);

        Intent intent = getIntent();

        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");

        btnCheckDuplicate = findViewById(R.id.btnCheckDuplicate);
        etNickname        = findViewById(R.id.etNickname);
        btnRegister       = findViewById(R.id.btnNext);

        queue = Volley.newRequestQueue(this);

        // 3) 닉네임 중복 확인
        btnCheckDuplicate.setOnClickListener(v -> {
            String nickname = etNickname.getText().toString().trim();
            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            VerifiedNicknameRequest req = new VerifiedNicknameRequest(
                    nickname,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            if (!success) {
                                Toast.makeText(this, "이미 존재하는 닉네임입니다", Toast.LENGTH_SHORT).show();
                                btnRegister.setEnabled(false);
                                return;
                            }

                            btnRegister.setEnabled(true);
                            etNickname.setEnabled(false);
                            Toast.makeText(this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "닉네임 조회 중 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(this, serverMsg, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(this, "서버 오류: " + status, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 네트워크 자체 문제
                            Toast.makeText(this, "네트워크 연결을 확인하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            queue.add(req);
        });

        // 4) 최종 회원가입
        btnRegister.setOnClickListener(v -> {

            String nickname = etNickname.getText().toString().trim();

            SignUpRequest req = new SignUpRequest(
                    email,
                    password,
                    nickname,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            if (!success) {
                                Toast.makeText(this, "회원가입 실패: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(this, serverMsg, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(this, "서버 오류: " + status, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 네트워크 자체 문제
                            Toast.makeText(this, "네트워크 연결을 확인하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            queue.add(req);
        });
    }
}