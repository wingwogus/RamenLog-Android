package kkj.mjc.ramenlog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import kkj.mjc.ramenlog.request.EmailVerificationRequest;
import kkj.mjc.ramenlog.request.SignUpRequest;
import kkj.mjc.ramenlog.request.VerifiedCodeRequest;
import kkj.mjc.ramenlog.request.VerifiedNicknameRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etCode, etNickname, etPassword, etPasswordConfirm;
    private Button btnSendCode, btnVerifyCode, btnCheckDuplicate, btnRegister;
    private RequestQueue queue;
    private boolean isVerified = false;
    private boolean isNicknameAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail           = findViewById(R.id.etEmail);
        etCode            = findViewById(R.id.etCode);
        etNickname        = findViewById(R.id.etNickname);
        etPassword        = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);

        btnSendCode       = findViewById(R.id.btnSendCode);
        btnVerifyCode     = findViewById(R.id.btnVerifyCode);
        btnCheckDuplicate = findViewById(R.id.btnCheckDuplicate);
        btnRegister       = findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);

        queue = Volley.newRequestQueue(this);

        // 1) 이메일 인증번호 요청
        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "이메일 전송 중 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error ->  {
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
            String code  = etCode.getText().toString().trim();
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
                                btnRegister.setEnabled(false);
                                return;
                            }

                            isVerified = true;
                            btnRegister.setEnabled(true);
                            etEmail.setEnabled(false);
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
                                isNicknameAvailable = false;
                                btnRegister.setEnabled(false);
                                return;
                            }

                            isNicknameAvailable = true;
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
            if (!isVerified) {
                Toast.makeText(this, "이메일 인증을 먼저 완료해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isNicknameAvailable) {
                Toast.makeText(this, "닉네임 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            String pwd      = etPassword.getText().toString();
            String confirm  = etPasswordConfirm.getText().toString();
            if (pwd.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pwd.equals(confirm)) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            String email    = etEmail.getText().toString().trim();
            String nickname = etNickname.getText().toString().trim();
            String code     = etCode.getText().toString().trim();

            SignUpRequest req = new SignUpRequest(
                    email,
                    pwd,
                    nickname,
                    code,
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