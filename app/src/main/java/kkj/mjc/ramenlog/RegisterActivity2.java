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

public class RegisterActivity2 extends AppCompatActivity {

    private TextView tvEmail;

    private EditText etPassword, etPasswordConfirm;

    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_register2);

        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        tvEmail = findViewById(R.id.tvEmail);

        // 이전 화면에서 전달된 이메일 값 추출 후 텍스트뷰에 표시
        String email = getIntent().getStringExtra("email");
        tvEmail.setText(email);

        // 다음 버튼 클릭 시
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            // 비밀번호 및 확인값 추출
            String password = etPassword.getText().toString().trim();
            String confirm  = etPasswordConfirm.getText().toString().trim();

            // 입력값 중 하나라도 비어 있으면 경고
            if (password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 비밀번호 불일치 시 경고
            if (!password.equals(confirm)) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 다음 화면으로 이동 (이메일, 비밀번호 함께 전달)
            Intent intent = new Intent(RegisterActivity2.this, RegisterActivity3.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        });



    }
}