package com.example.kcalogo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullname, etUsername, etPassword, etSdt;
    private RadioGroup rgGender;
    private Button btnRegister;
    private TextView tvLoginLink;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        etFullname = findViewById(R.id.et_fullname);
        etUsername = findViewById(R.id.et_username_reg);
        etPassword = findViewById(R.id.et_password_reg);
        etSdt = findViewById(R.id.et_sdt);
        rgGender = findViewById(R.id.rg_gender);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = etFullname.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String sdt = etSdt.getText().toString().trim();
                String gender = getSelectedGender();

                if (fullname.isEmpty() || username.isEmpty() || password.isEmpty() || sdt.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                } else {
                    // Sử dụng phương thức addUser() thay cho insertUser()
                    long result = dbManager.addUser(fullname, username, password, sdt, gender, 1);
                    if (result != -1) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Tài khoản có thể đã tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String getSelectedGender() {
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_male) {
            return "nam";
        } else if (selectedId == R.id.rb_female) {
            return "nu";
        } else {
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}