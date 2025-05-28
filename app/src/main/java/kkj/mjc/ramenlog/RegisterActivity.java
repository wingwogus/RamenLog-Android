package kkj.mjc.ramenlog;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import kkj.mjc.ramenlog.request.EmailVerificationRequest;
import kkj.mjc.ramenlog.request.VerifiedCodeRequest;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvCodeLabel;

    private EditText etEmail, etCode;
    private Button btnSendCode, btnVerifyCode, btnNext;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvCodeLabel = findViewById(R.id.tvCodeLabel);
        etEmail = findViewById(R.id.etEmail);
        etCode = findViewById(R.id.etCode);

        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setEnabled(false);

        queue = Volley.newRequestQueue(this);

        // 1) 이메일 인증번호 요청
        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            etEmail.setEnabled(false);
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            EmailVerificationRequest req = new EmailVerificationRequest(
                    email,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            if (!success) {
                                Toast.makeText(this, "이메일 전송에 실패했습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(this, "이메일 전송 완료", Toast.LENGTH_SHORT).show();
                            tvCodeLabel.setVisibility(VISIBLE);
                            etCode.setVisibility(VISIBLE);
                            btnVerifyCode.setVisibility(VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "이메일 전송 중 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        // 2) 인증번호 확인
        btnVerifyCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String code = etCode.getText().toString().trim();

            if (email.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "이메일과 인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            VerifiedCodeRequest req = new VerifiedCodeRequest(
                    email,
                    code,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            if (!success) {
                                Toast.makeText(this, "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                btnNext.setEnabled(false);
                                return;
                            }

                            btnNext.setEnabled(true);
                            Toast.makeText(this, "이메일 인증 완료", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "인증 확인 중 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
            intent.putExtra("email", etEmail.getText().toString().trim());
            startActivity(intent);
            finish();
        });
    }
}