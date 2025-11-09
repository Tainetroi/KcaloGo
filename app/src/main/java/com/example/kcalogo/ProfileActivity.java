package com.example.kcalogo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_status";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private TextView tvUsername, tvPhoneNumber;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các thành phần
        tvUsername = findViewById(R.id.tv_username);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        btnLogout = findViewById(R.id.btn_logout);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Lấy thông tin người dùng từ SharedPreferences và hiển thị
        String tenUser = sharedPreferences.getString("tenuser", "Không có tên");
        String sdt = sharedPreferences.getString("sdt", "Không có SĐT");
        tvUsername.setText("Tên người dùng: " + tenUser);
        tvPhoneNumber.setText("Số điện thoại: " + sdt);

        // Xử lý sự kiện khi nhấn nút Đăng xuất
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa tất cả dữ liệu người dùng đã lưu
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(ProfileActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

                // Chuyển về màn hình đăng nhập và xóa lịch sử các màn hình trước
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}