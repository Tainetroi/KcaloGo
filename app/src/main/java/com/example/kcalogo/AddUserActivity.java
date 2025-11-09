package com.example.kcalogo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddUserActivity extends AppCompatActivity {

    private EditText etTenUser, etTaiKhoan, etMatKhau, etSdt;
    private Button btnSave;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        etTenUser = findViewById(R.id.et_new_tenuser);
        etTaiKhoan = findViewById(R.id.et_new_taikhoan);
        etMatKhau = findViewById(R.id.et_new_matkhau);
        etSdt = findViewById(R.id.et_new_sdt);
        btnSave = findViewById(R.id.btn_save_user);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenUser = etTenUser.getText().toString();
                String taiKhoan = etTaiKhoan.getText().toString();
                String matKhau = etMatKhau.getText().toString();
                String sdt = etSdt.getText().toString();

                // Kiểm tra các trường không được trống
                if (tenUser.isEmpty() || taiKhoan.isEmpty() || matKhau.isEmpty()) {
                    Toast.makeText(AddUserActivity.this, "Vui lòng nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gọi phương thức thêm người dùng từ DatabaseManager
                long result = dbManager.addUser(tenUser, taiKhoan, matKhau, sdt, "nam", 1); // Giới tính và chức vụ có thể tùy chọn
                if (result != -1) {
                    Toast.makeText(AddUserActivity.this, "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();
                    // Đóng màn hình này và quay lại AdminActivity
                    finish();
                } else {
                    Toast.makeText(AddUserActivity.this, "Thêm người dùng thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}