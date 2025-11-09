package com.example.kcalogo;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Thư viện cần thiết cho việc xin quyền
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SinhHoatActivity extends AppCompatActivity
        implements SinhHoatAdapter.OnItemInteractionListener, SinhHoatAdapter.IsCompletedChecker {

    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private FloatingActionButton fabAdd;
    private DatabaseManager dbManager;
    private SinhHoatAdapter adapter;
    private List<SinhHoat> sinhHoatList = new ArrayList<>();
    private int idUser = 1; // TODO: Thay bằng ID người dùng thực tế

    // HẰNG SỐ CHO THÔNG BÁO VÀ QUYỀN
    private static final String CHANNEL_ID = "SINH_HOAT_REMINDER_CHANNEL";
    private static final int REMINDER_REQUEST_CODE_BASE = 1000;
    private static final int PERMISSION_REQUEST_CODE = 101;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinh_hoat);

        // Khởi tạo DB và xin quyền
        dbManager = new DatabaseManager(this);
        dbManager.open();
        requestNotificationPermission(); // Xin quyền
        createNotificationChannel();

        recyclerView = findViewById(R.id.recyclerViewSinhHoat);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAdd = findViewById(R.id.fabAddSinhHoat);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SinhHoatAdapter(this, sinhHoatList, this, this);
        recyclerView.setAdapter(adapter);

        loadSinhHoatList();

        fabAdd.setOnClickListener(v -> {
            showAddEditDialog(null);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbManager != null) {
            dbManager.close();
        }
    }

    // Xử lý kết quả xin quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền thông báo!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thông báo có thể không hiển thị.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Hàm xin quyền POST_NOTIFICATIONS
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Hàm tạo Notification Channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nhắc nhở Sinh Hoạt";
            String description = "Kênh thông báo cho các mục tiêu sinh hoạt lặp lại.";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    // --- LOGIC TẢI DỮ LIỆU ---
    private void loadSinhHoatList() {
        // TẢI TẤT CẢ DỮ LIỆU ĐỂ DEBUG/KIỂM TRA (Tạm thời)
        sinhHoatList = dbManager.getAllSinhHoat();
        Log.d("SH_LOAD", "Loaded " + sinhHoatList.size() + " total items.");

        if (sinhHoatList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
            adapter.updateList(sinhHoatList);
        }
    }

    // --- LOGIC KIỂM TRA TRẠNG THÁI ---
    @Override
    public boolean isCompletedForToday(int idsh) {
        String currentDate = dateFormat.format(new Date());
        return dbManager.isSinhHoatCompleted(idsh, idUser, currentDate);
    }

    // --- LOGIC TƯƠNG TÁC RCV ---

    @Override
    public void onCompleteClick(SinhHoat sinhHoat) {
        String currentDate = dateFormat.format(new Date());
        boolean isCompleted = isCompletedForToday(sinhHoat.getIdsh());

        if (isCompleted) {
            dbManager.deleteLichSuSinhHoat(sinhHoat.getIdsh(), idUser, currentDate);
            Toast.makeText(this, "Đã hủy hoàn thành: " + sinhHoat.getTensh(), Toast.LENGTH_SHORT).show();
        } else {
            dbManager.insertLichSuSinhHoat(sinhHoat.getIdsh(), idUser, currentDate, "Hoàn thành");
            Toast.makeText(this, "Đã đánh dấu hoàn thành: " + sinhHoat.getTensh(), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClick(SinhHoat sinhHoat) {
        showAddEditDialog(sinhHoat);
    }

    @Override
    public void onDeleteClick(SinhHoat sinhHoat) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa mục tiêu '" + sinhHoat.getTensh() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbManager.deleteSinhHoat(sinhHoat.getIdsh());
                    cancelReminder(sinhHoat.getIdsh() + REMINDER_REQUEST_CODE_BASE); // Hủy báo thức
                    loadSinhHoatList();
                    Toast.makeText(this, "Đã xóa mục tiêu: " + sinhHoat.getTensh(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // --- LOGIC ĐẶT/HỦY BÁO THỨC ---

    private void cancelReminder(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.i("SH_ALARM", "Cancelled reminder for request code: " + requestCode);
        }
    }

    public void scheduleReminder(int idsh, String tenMucTieu, String ngayLapLai, int intervalMinutes) {
        if (intervalMinutes <= 0) {
            cancelReminder(idsh + REMINDER_REQUEST_CODE_BASE);
            return;
        }

        long intervalMillis = (long) intervalMinutes * 60 * 1000L;
        int requestCode = idsh + REMINDER_REQUEST_CODE_BASE; // Dùng ID mục tiêu làm request code duy nhất

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("TEN_MUC_TIEU", tenMucTieu);
        intent.putExtra("NGAY_LAP_LAI", ngayLapLai);
        intent.putExtra("ID_SH", idsh); // Truyền ID mục tiêu

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            // Kích hoạt lần đầu sau 1 chu kỳ để tránh kích hoạt ngay lập tức khi save
            long firstTriggerTime = System.currentTimeMillis() + intervalMillis;

            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    firstTriggerTime,
                    intervalMillis,
                    pendingIntent
            );

            Log.i("SH_ALARM", "Set reminder every " + intervalMinutes + " mins for ID: " + idsh);
            Toast.makeText(this, "Đã đặt lịch nhắc nhở mỗi " + intervalMinutes + " phút!", Toast.LENGTH_SHORT).show();
        }
    }

    // --- LOGIC LƯU DỮ LIỆU VÀO DB ---

    private void saveNewSinhHoat(String ten, String moTa, String ngayLapLai, int intervalMinutes) {
        long newId = dbManager.insertSinhHoat(ten, moTa, ngayLapLai, idUser, intervalMinutes);

        if (newId > 0) {
            Toast.makeText(this, "Đã thêm mục tiêu mới!", Toast.LENGTH_SHORT).show();
            scheduleReminder((int)newId, ten, ngayLapLai, intervalMinutes);
            loadSinhHoatList();
        } else {
            Log.e("SH_SAVE", "Insert failed. Result: " + newId);
            Toast.makeText(this, "Lỗi khi thêm mục tiêu. Thất bại DB.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSinhHoat(int idsh, String ten, String moTa, String ngayLapLai, int intervalMinutes) {
        int rowsAffected = dbManager.updateSinhHoat(idsh, ten, moTa, ngayLapLai, idUser, intervalMinutes);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Đã cập nhật mục tiêu!", Toast.LENGTH_SHORT).show();
            scheduleReminder(idsh, ten, ngayLapLai, intervalMinutes);
            loadSinhHoatList();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật mục tiêu.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- HÀM HỖ TRỢ LẤY DỮ LIỆU VÀ DIALOG ---

    private CheckBox getDayCheckbox(View dialogView, String dayName) {
        switch (dayName) {
            case "Thứ Hai": return dialogView.findViewById(R.id.cbT2);
            case "Thứ Ba": return dialogView.findViewById(R.id.cbT3);
            case "Thứ Tư": return dialogView.findViewById(R.id.cbT4);
            case "Thứ Năm": return dialogView.findViewById(R.id.cbT5);
            case "Thứ Sáu": return dialogView.findViewById(R.id.cbT6);
            case "Thứ Bảy": return dialogView.findViewById(R.id.cbT7);
            case "Chủ Nhật": return dialogView.findViewById(R.id.cbCN);
            default: return null;
        }
    }

    private String getSelectedDays(View dialogView) {
        StringBuilder sb = new StringBuilder();
        String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};

        for (String day : days) {
            CheckBox cb = getDayCheckbox(dialogView, day);
            if (cb != null && cb.isChecked()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(day);
            }
        }
        return sb.toString();
    }

    private void showAddEditDialog(SinhHoat sinhHoat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_sinhhoat, null);
        builder.setView(dialogView);

        // Tham chiếu Views
        EditText etTenSH = dialogView.findViewById(R.id.etTenSH);
        EditText etMoTa = dialogView.findViewById(R.id.etMoTa);
        EditText etIntervalMinutes = dialogView.findViewById(R.id.etIntervalMinutes);

        // Đặt giá trị mặc định cho khoảng thời gian
        etIntervalMinutes.setText("30");

        if (sinhHoat == null) {
            builder.setTitle("Thêm Mục Tiêu Mới");
        } else {
            builder.setTitle("Chỉnh Sửa Mục Tiêu");
            etTenSH.setText(sinhHoat.getTensh());
            etMoTa.setText(sinhHoat.getMota());

            // Nạp giá trị Khoảng thời gian
            etIntervalMinutes.setText(String.valueOf(sinhHoat.getKhoangthoigian()));

            // LOGIC NẠP NGÀY LẶP LẠI
            if (sinhHoat.getNgaylaplai() != null && !sinhHoat.getNgaylaplai().isEmpty()) {
                List<String> selectedDays = Arrays.asList(sinhHoat.getNgaylaplai().split(","));
                for (String day : selectedDays) {
                    CheckBox cb = getDayCheckbox(dialogView, day.trim());
                    if (cb != null) {
                        cb.setChecked(true);
                    }
                }
            }
        }

        // Thiết lập nút LƯU
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ten = etTenSH.getText().toString().trim();
            String moTa = etMoTa.getText().toString().trim();
            String ngayLapLai = getSelectedDays(dialogView);

            String intervalText = etIntervalMinutes.getText().toString().trim();
            int intervalMinutes = intervalText.isEmpty() ? 0 : Integer.parseInt(intervalText);

            if (ten.isEmpty()) {
                Toast.makeText(this, "Tên mục tiêu không được để trống.", Toast.LENGTH_SHORT).show();
            } else if (ngayLapLai.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ngày lặp lại.", Toast.LENGTH_SHORT).show();
            } else if (intervalMinutes <= 0) {
                Toast.makeText(this, "Khoảng thời gian phải lớn hơn 0 phút.", Toast.LENGTH_SHORT).show();
            } else {
                if (sinhHoat == null) {
                    saveNewSinhHoat(ten, moTa, ngayLapLai, intervalMinutes);
                } else {
                    updateSinhHoat(sinhHoat.getIdsh(), ten, moTa, ngayLapLai, intervalMinutes);
                }
            }
        });

        // Thiết lập nút HỦY
        builder.setNegativeButton("Hủy", null);

        builder.show();
    }
}