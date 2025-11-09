    package com.example.kcalogo;

    import androidx.appcompat.app.AppCompatActivity;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.database.Cursor;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    public class LoginActivity extends AppCompatActivity {

        private EditText etUsername, etPassword;
        private Button btnLogin;
        private TextView tvRegisterLink, tvForgotPassword;

        private DatabaseManager dbManager;
        private SharedPreferences sharedPreferences;
        private static final String PREF_NAME = "user_status";
        private static final String KEY_IS_LOGGED_IN = "is_logged_in";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            dbManager = new DatabaseManager(this);
            dbManager.open();

            sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

            etUsername = findViewById(R.id.et_username);
            etPassword = findViewById(R.id.et_password);
            btnLogin = findViewById(R.id.btn_login);
            tvRegisterLink = findViewById(R.id.tv_register_link);
            tvForgotPassword = findViewById(R.id.tv_forgot_password);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    // Kiểm tra tài khoản và mật khẩu trong database
                    if (dbManager.checkUser(username, password)) {
                        // Lấy thông tin người dùng từ database
                        Cursor cursor = dbManager.getUserInfo(username);
                        if (cursor != null && cursor.moveToFirst()) {
                            int tenUserIndex = cursor.getColumnIndex("tenuser");
                            int sdtIndex = cursor.getColumnIndex("sdt");
                            int chucVuIndex = cursor.getColumnIndex("chucvu");

                            if (tenUserIndex != -1 && sdtIndex != -1 && chucVuIndex != -1) {
                                String tenUser = cursor.getString(tenUserIndex);
                                String sdt = cursor.getString(sdtIndex);
                                int chucVu = cursor.getInt(chucVuIndex);

                                // Lưu thông tin người dùng vào SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(KEY_IS_LOGGED_IN, true);
                                editor.putString("tenuser", tenUser);
                                editor.putString("sdt", sdt);
                                editor.putInt("chucvu", chucVu);
                                editor.apply();

                                cursor.close();
                            } else {
                                Log.e("LoginActivity", "Không tìm thấy cột 'tenuser', 'sdt' hoặc 'chucvu' trong Cursor.");
                            }
                        }

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            tvRegisterLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            tvForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            dbManager.close();
        }
    }