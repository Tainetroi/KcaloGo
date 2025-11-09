package com.example.kcalogo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_status";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



// Khởi tạo và mở kết nối cơ sở dữ liệu

        dbManager = new DatabaseManager(this);

        dbManager.open();



// Khởi tạo SharedPreferences để kiểm tra trạng thái đăng nhập

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);



// ---------------------------------------------

// Cấu hình và thêm sự kiện cho các nút "Bí quyết"

// (Đã loại bỏ logic chèn dữ liệu mẫu trong onCreate)

// ---------------------------------------------



        setupButton(R.id.btn_lich_tap, R.drawable.ic_lich_tap, "Lịch tập luyện", v -> {

            Toast.makeText(MainActivity.this, "Chuyển đến Lịch tập luyện", Toast.LENGTH_SHORT).show();

// TODO: Start Intent cho LichtapActivity

        });



// Đã SỬA: CHỈ giữ lại logic mở SinhHoatActivity

        setupButton(R.id.btn_sinh_hoat, R.drawable.ic_sinh_hoat, "Sinh hoạt", v -> {

            Intent intent = new Intent(MainActivity.this, SinhHoatActivity.class);

            startActivity(intent);

        });



        setupButton(R.id.btn_do_an, R.drawable.ic_do_an, "Đồ ăn", v -> {

            Toast.makeText(MainActivity.this, "Chuyển đến màn hình Đồ ăn", Toast.LENGTH_SHORT).show();

// TODO: Start Intent cho DoAnActivity

        });



// ---------------------------------------------

// Cấu hình và thêm sự kiện cho các nút "Dịch vụ khác"

// (Đã loại bỏ logic chèn dữ liệu mẫu trong onCreate)

// ---------------------------------------------



        setupButton(R.id.btn_hang_ngay, R.drawable.ic_hang_ngay, "Hàng ngày", new View.OnClickListener() {

            @Override

            public void onClick(View v) {

// CHỈNH SỬA: Chuyển đến CalendarActivity

                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);

                startActivity(intent);

            }

        });



        setupButton(R.id.btn_tinh_kcalo, R.drawable.ic_tinh_kcalo, "Tính kcalo", v -> {

            Intent intent = new Intent(MainActivity.this, KcalActivity.class);

            startActivity(intent);

        });



        setupButton(R.id.btn_tinh_bmi, R.drawable.ic_tinh_bmi, "Tính BMI", v -> {

            Intent intent = new Intent(MainActivity.this, BmiActivity.class);

            startActivity(intent);

        });



// ---------------------------------------------

// Cấu hình và thêm sự kiện cho các nút của Bottom Navigation Bar

// ---------------------------------------------



        setupNavButton(R.id.btn_bmi_nav, R.drawable.ic_bmi_nav, "BMI", new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, BmiActivity.class);

                startActivity(intent);

            }

        });



        setupNavButton(R.id.btn_kcal_nav, R.drawable.ic_kcal_nav, "kcal", new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, KcalActivity.class);

                startActivity(intent);

            }

        });



        setupNavButton(R.id.btn_calendar_nav, R.drawable.ic_calendar_nav, "Lịch", new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);

                startActivity(intent);

            }

        });



// Nút Scan (giữa)

        findViewById(R.id.btn_scan_nav).setOnClickListener(v -> {

            Toast.makeText(MainActivity.this, "Chuyển đến màn hình Quét (Scan)", Toast.LENGTH_SHORT).show();

// TODO: start Intent cho ScanActivity

        });



// Nút Tài khoản đã được chỉnh sửa để kiểm tra trạng thái đăng nhập

        setupNavButton(R.id.btn_profile_nav, R.drawable.ic_profile_nav, "Tài khoản", new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

                Intent intent;



                if (isLoggedIn) {

                    int chucVu = sharedPreferences.getInt("chucvu", 1);

                    if (chucVu == 2) {

                        intent = new Intent(MainActivity.this, AdminActivity.class);

                        Toast.makeText(MainActivity.this, "Chào mừng Admin!", Toast.LENGTH_SHORT).show();

                    } else {

                        intent = new Intent(MainActivity.this, ProfileActivity.class);

                        Toast.makeText(MainActivity.this, "Chào mừng Người dùng!", Toast.LENGTH_SHORT).show();

                    }

                } else {

                    intent = new Intent(MainActivity.this, LoginActivity.class);

                    Toast.makeText(MainActivity.this, "Bạn chưa đăng nhập. Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();

                }

                startActivity(intent);

            }

        });

    }



// (Các phương thức setupButton, setupNavButton, onDestroy giữ nguyên)

    private void setupButton(int viewId, int iconId, String text, View.OnClickListener listener) {

        View buttonView = findViewById(viewId);

        ImageView iconView = buttonView.findViewById(R.id.iv_icon);

        TextView textView = buttonView.findViewById(R.id.tv_text);



        if (iconView != null) iconView.setImageResource(iconId);

        if (textView != null) textView.setText(text);



        buttonView.setOnClickListener(listener);

    }



    private void setupNavButton(int viewId, int iconId, String text, View.OnClickListener listener) {

        LinearLayout buttonView = findViewById(viewId);

        ImageView iconView = (ImageView) buttonView.getChildAt(0);

        TextView textView = (TextView) buttonView.getChildAt(1);



        iconView.setImageResource(iconId);

        textView.setText(text);

        buttonView.setOnClickListener(listener);

    }



    @Override

    protected void onDestroy() {

        super.onDestroy();

        if (dbManager != null) {

            dbManager.close();

        }

    }

}