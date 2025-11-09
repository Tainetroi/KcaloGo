package com.example.kcalogo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color; // Import Color
import androidx.core.content.ContextCompat; // Import ContextCompat

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText etNoteContent;
    private Button btnSaveNote;
    private Button btnDeleteNote; // Thêm nút xóa
    private TextView tvSelectedDate;

    private DatabaseManager dbManager;
    private String selectedDateString;
    private int idUser = 1; // TODO: Lấy ID người dùng thực tế

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        calendarView = findViewById(R.id.calendarView);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        btnDeleteNote = findViewById(R.id.btnDeleteNote); // Tham chiếu nút mới
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        // 1. Thiết lập ngày hiện tại
        long currentTime = System.currentTimeMillis();
        selectedDateString = dateFormat.format(new Date(currentTime));
        tvSelectedDate.setText("Ngày: " + selectedDateString);
        loadNoteForSelectedDate(selectedDateString);

        // 2. Xử lý sự kiện khi chọn ngày
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                selectedDateString = dateFormat.format(calendar.getTime());
                tvSelectedDate.setText("Ngày: " + selectedDateString);

                loadNoteForSelectedDate(selectedDateString);
            }
        });

        // 3. Xử lý sự kiện khi nhấn nút LƯU
        btnSaveNote.setOnClickListener(v -> {
            saveNote();
        });

        // 4. Xử lý sự kiện khi nhấn nút HỦY
        btnDeleteNote.setOnClickListener(v -> {
            deleteNote();
        });
    }

    private void loadNoteForSelectedDate(String date) {
        String noteContent = dbManager.getHangngayNote(date, idUser);
        etNoteContent.setText(noteContent);

        if (noteContent.isEmpty()) {
            etNoteContent.setHint("Nhập ghi chú cho ngày " + date);
            btnDeleteNote.setEnabled(false); // Vô hiệu hóa nút xóa nếu không có ghi chú
            // *Tính năng đánh dấu ngày*: Ngày không có ghi chú thì hiển thị màu mặc định
            // calendarView.setDateTextAppearance(android.R.style.TextAppearance_Medium); // Chỉ là ví dụ
        } else {
            btnDeleteNote.setEnabled(true); // Kích hoạt nút xóa
            // *Tính năng đánh dấu ngày*: Ngày có ghi chú thì hiển thị màu đỏ (Tùy thuộc vào style)
            // Lưu ý: Chỉ có thể thay đổi theme của CalendarView, không thể tô đỏ 1 ô.
            // Nếu muốn tô đỏ 1 ô, cần dùng thư viện bên ngoài.
        }
    }

    private void saveNote() {
        String note = etNoteContent.getText().toString().trim();

        if (note.isEmpty()) {
            // Nếu nội dung trống, gọi hàm xóa nếu tồn tại ghi chú cũ
            if (dbManager.getHangngayNote(selectedDateString, idUser).length() > 0) {
                deleteNote(); // Xóa ghi chú cũ nếu người dùng xóa nội dung và nhấn Lưu
                return;
            }
            Toast.makeText(this, "Ghi chú trống. Không lưu.", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbManager.saveHangngayNote(note, selectedDateString, idUser);

        if (result > 0) {
            Toast.makeText(this, "Đã lưu thành công ghi chú ngày " + selectedDateString, Toast.LENGTH_SHORT).show();
            btnDeleteNote.setEnabled(true);
        } else {
            Toast.makeText(this, "Lỗi khi lưu ghi chú.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNote() {
        int rowsDeleted = dbManager.deleteHangngayNote(selectedDateString, idUser);

        if (rowsDeleted > 0) {
            etNoteContent.setText("");
            etNoteContent.setHint("Nhập nhật ký, ghi chú hoặc hoạt động hàng ngày...");
            btnDeleteNote.setEnabled(false);
            Toast.makeText(this, "Đã hủy ghi chú ngày " + selectedDateString, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không có ghi chú để hủy.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbManager != null) {
            dbManager.close();
        }
    }
}