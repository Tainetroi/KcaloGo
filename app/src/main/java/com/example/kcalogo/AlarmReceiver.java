package com.example.kcalogo;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "SINH_HOAT_REMINDER_CHANNEL";
    private static final int DEFAULT_NOTIFICATION_ID = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lấy thông tin mục tiêu
        String tenMucTieu = intent.getStringExtra("TEN_MUC_TIEU");
        String ngayLapLai = intent.getStringExtra("NGAY_LAP_LAI");
        int idSh = intent.getIntExtra("ID_SH", -1); // ID mục tiêu

        // 1. Kiểm tra ngày hiện tại có nằm trong ngày lặp lại không
        Calendar calendar = Calendar.getInstance();
        // Dùng định dạng khớp với dữ liệu lưu trong DB ("Thứ Hai", "Chủ Nhật")
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
        String todayName = dayFormat.format(calendar.getTime());

        if (ngayLapLai == null || !ngayLapLai.contains(todayName)) {
            Log.d("AlarmReceiver", "Hôm nay (" + todayName + ") không phải ngày lặp lại cho " + tenMucTieu);
            return;
        }

        // 2. Nếu là ngày lặp lại, hiển thị thông báo
        Log.d("AlarmReceiver", "Hiển thị thông báo cho: " + tenMucTieu);
        showNotification(context, tenMucTieu, "Đã đến giờ cho mục tiêu: " + tenMucTieu, idSh);
    }

    private void showNotification(Context context, String title, String content, int idSh) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // TODO: Thay bằng icon của bạn
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Kiểm tra quyền POST_NOTIFICATIONS (API 33+)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("AlarmReceiver", "Quyền POST_NOTIFICATIONS chưa được cấp.");
            // Không thể gửi thông báo nếu thiếu quyền
            return;
        }

        // Sử dụng ID mục tiêu (idSh) làm ID thông báo để tránh thông báo bị ghi đè
        int notificationId = idSh > 0 ? idSh : DEFAULT_NOTIFICATION_ID;

        notificationManager.notify(notificationId, builder.build());
    }
}