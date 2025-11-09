package com.example.kcalogo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditUserActivity extends AppCompatActivity {

    private EditText etTenUser, etTaiKhoan, etMatKhau, etSdt;
    private RadioGroup rgGioiTinh, rgChucVu;
    private Button btnUpdate;
    private DatabaseManager dbManager;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Khai báo và liên kết các View từ layout
        etTenUser = findViewById(R.id.et_edit_tenuser);
        etTaiKhoan = findViewById(R.id.et_edit_taikhoan);
        etMatKhau = findViewById(R.id.et_edit_matkhau);
        etSdt = findViewById(R.id.et_edit_sdt);

        rgGioiTinh = findViewById(R.id.rg_edit_gioitinh);
        rgChucVu = findViewById(R.id.rg_edit_chucvu);

        btnUpdate = findViewById(R.id.btn_update_user);

        // Lấy dữ liệu người dùng được truyền từ AdminActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getInt("user_id");
            etTenUser.setText(extras.getString("ten_user"));
            etTaiKhoan.setText(extras.getString("tai_khoan"));
            etMatKhau.setText(extras.getString("mat_khau"));
            etSdt.setText(extras.getString("sdt"));

            // Đặt giới tính đã chọn
            String gioiTinh = extras.getString("gioi_tinh");
            if ("nam".equalsIgnoreCase(gioiTinh)) {
                rgGioiTinh.check(R.id.rb_edit_nam);
            } else {
                rgGioiTinh.check(R.id.rb_edit_nu);
            }

            // Đặt chức vụ đã chọn
            int chucVu = extras.getInt("chuc_vu");
            if (chucVu == 2) {
                rgChucVu.check(R.id.rb_edit_admin);
            } else {
                rgChucVu.check(R.id.rb_edit_user);
            }
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenUser = etTenUser.getText().toString();
                String taiKhoan = etTaiKhoan.getText().toString();
                String matKhau = etMatKhau.getText().toString();
                String sdt = etSdt.getText().toString();

                if (tenUser.isEmpty() || taiKhoan.isEmpty() || matKhau.isEmpty()) {
                    Toast.makeText(EditUserActivity.this, "Vui lòng nhập đủ thông tin bắt buộc.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String gioiTinh = "";
                int selectedGioiTinhId = rgGioiTinh.getCheckedRadioButtonId();
                if (selectedGioiTinhId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedGioiTinhId);
                    gioiTinh = selectedRadioButton.getText().toString();
                }

                int chucVu = 1;
                int selectedChucVuId = rgChucVu.getCheckedRadioButtonId();
                if (selectedChucVuId == R.id.rb_edit_admin) {
                    chucVu = 2;
                }

                int rowsAffected = dbManager.updateUser(userId, tenUser, taiKhoan, matKhau, sdt, gioiTinh, chucVu);
                if (rowsAffected > 0) {
                    Toast.makeText(EditUserActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditUserActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
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