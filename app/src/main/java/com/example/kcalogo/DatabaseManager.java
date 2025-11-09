package com.example.kcalogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        // Mở kết nối ghi (writable connection)
        database = dbHelper.getWritableDatabase();
        // Thêm người dùng mẫu nếu chưa có
        addSampleUserIfEmpty();
    }

    public void close() {
        // Đóng kết nối cơ sở dữ liệu
        if (database != null && database.isOpen()) {
            dbHelper.close();
        }
    }

    // ===================================================================
    //  QUẢN LÝ NGƯỜI DÙNG (USER)
    // ===================================================================

    public long addUser(String tenUser, String taiKhoan, String matKhau, String sdt, String gioiTinh, int chucVu) {
        ContentValues values = new ContentValues();
        values.put("tenuser", tenUser);
        values.put("taikhoan", taiKhoan);
        values.put("matkhau", matKhau);
        values.put("sdt", sdt);
        values.put("gioitinh", gioiTinh);
        values.put("chucvu", chucVu);
        return database.insert("user", null, values);
    }

    public boolean checkUser(String taiKhoan, String matKhau) {
        Cursor cursor = null;
        try {
            String query = "SELECT iduser FROM user WHERE taikhoan = ? AND matkhau = ?";
            cursor = database.rawQuery(query, new String[]{taiKhoan, matKhau});
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DatabaseManager", "Lỗi khi kiểm tra người dùng: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Cursor getUserInfo(String taiKhoan) {
        String query = "SELECT iduser, tenuser, taikhoan, matkhau, sdt, gioiTinh, chucvu FROM user WHERE taikhoan = ?";
        return database.rawQuery(query, new String[]{taiKhoan});
    }

    public Cursor getAllUsers() {
        return database.query(
                "user",
                new String[]{"iduser", "tenuser", "taikhoan", "matkhau", "sdt", "gioitinh", "chucvu"},
                null,
                null,
                null,
                null,
                null
        );
    }

    public int updateUser(int id, String tenUser, String taiKhoan, String matKhau, String sdt, String gioiTinh, int chucVu) {
        ContentValues values = new ContentValues();
        values.put("tenuser", tenUser);
        values.put("taikhoan", taiKhoan);
        values.put("matkhau", matKhau);
        values.put("sdt", sdt);
        values.put("gioitinh", gioiTinh);
        values.put("chucvu", chucVu);
        return database.update("user", values, "iduser=?", new String[]{String.valueOf(id)});
    }

    public int deleteUser(int id) {
        return database.delete("user", "iduser=?", new String[]{String.valueOf(id)});
    }

    private void addSampleUserIfEmpty() {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT COUNT(*) FROM user", null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                if (count == 0) {
                    addUser("Test User", "test", "123", "0123456789", "nam", 1);
                    addUser("Admin User", "admin", "admin123", "0987654321", "nu", 2);
                    Log.d("DatabaseManager", "Đã thêm người dùng mẫu: test/123 và admin/admin123");
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseManager", "Lỗi khi kiểm tra và thêm người dùng mẫu: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // ===================================================================
    //  QUẢN LÝ SINH HOẠT (SINHHOAT)
    // ===================================================================

    // Thêm hoạt động sinh hoạt mới (CREATE) - Có khoangThoiGian
    public long insertSinhHoat(String tenSh, String moTa, String ngayLapLai, int idUser, int khoangThoiGian) {
        ContentValues values = new ContentValues();
        values.put("tensh", tenSh);
        values.put("mota", moTa);
        values.put("ngaylaplai", ngayLapLai);
        values.put("khoangthoigian", khoangThoiGian);
        values.put("iduser", idUser);

        try {
            long result = database.insertOrThrow("sinhhoat", null, values);
            Log.d("DB_INSERT_SUCCESS", "Successfully inserted row ID: " + result);
            return result;
        } catch (Exception e) {
            Log.e("DB_INSERT_ERROR", "Error inserting into sinhhoat: " + e.getMessage());
            return -1;
        }
    }

    // Cập nhật hoạt động sinh hoạt (UPDATE) - Có khoangThoiGian
    public int updateSinhHoat(int idsh, String tensh, String mota, String ngayLapLai, int iduser, int khoangThoiGian) {
        ContentValues values = new ContentValues();
        values.put("tensh", tensh);
        values.put("mota", mota);
        values.put("ngaylaplai", ngayLapLai);
        values.put("khoangthoigian", khoangThoiGian);
        values.put("iduser", iduser);

        String selection = "idsh = ?";
        String[] selectionArgs = { String.valueOf(idsh) };

        return database.update("sinhhoat", values, selection, selectionArgs);
    }

    // Xóa hoạt động sinh hoạt (DELETE)
    public int deleteSinhHoat(int idsh) {
        return database.delete("sinhhoat", "idsh=?", new String[]{String.valueOf(idsh)});
    }

    // Lấy danh sách Sinh Hoạt theo ngày (READ - Lọc) - Đã có khoangthoigian
    public List<SinhHoat> getSinhHoatByDay(String dayName, int idUser) {
        List<SinhHoat> sinhHoatList = new ArrayList<>();
        Cursor cursor = null;
        try {
            // Thêm cột khoangthoigian vào truy vấn
            String query = "SELECT idsh, tensh, mota, ngaylaplai, khoangthoigian, iduser FROM sinhhoat " +
                    "WHERE iduser = ? AND ngaylaplai LIKE ?";

            String[] selectionArgs = { String.valueOf(idUser), "%" + dayName + "%" };

            cursor = database.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    int idsh = cursor.getInt(cursor.getColumnIndexOrThrow("idsh"));
                    String tensh = cursor.getString(cursor.getColumnIndexOrThrow("tensh"));
                    String mota = cursor.getString(cursor.getColumnIndexOrThrow("mota"));
                    String ngaylaplai = cursor.getString(cursor.getColumnIndexOrThrow("ngaylaplai"));
                    int khoangthoigian = cursor.getInt(cursor.getColumnIndexOrThrow("khoangthoigian"));
                    int iduser = cursor.getInt(cursor.getColumnIndexOrThrow("iduser"));

                    // Sử dụng constructor 6 tham số
                    SinhHoat sinhHoat = new SinhHoat(idsh, tensh, mota, ngaylaplai, khoangthoigian, iduser);
                    sinhHoatList.add(sinhHoat);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Lỗi khi lấy SinhHoat theo ngày: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sinhHoatList;
    }




    // Lấy TẤT CẢ Sinh Hoạt (READ - Debug) - Đã có khoangthoigian
    public List<SinhHoat> getAllSinhHoat() {
        List<SinhHoat> sinhHoatList = new ArrayList<>();
        Cursor cursor = null;
        try {
            // Thêm cột khoangthoigian vào truy vấn
            String query = "SELECT idsh, tensh, mota, ngaylaplai, khoangthoigian, iduser FROM sinhhoat";
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int idsh = cursor.getInt(cursor.getColumnIndexOrThrow("idsh"));
                    String tensh = cursor.getString(cursor.getColumnIndexOrThrow("tensh"));
                    String mota = cursor.getString(cursor.getColumnIndexOrThrow("mota"));
                    String ngaylaplai = cursor.getString(cursor.getColumnIndexOrThrow("ngaylaplai"));
                    int khoangthoigian = cursor.getInt(cursor.getColumnIndexOrThrow("khoangthoigian"));
                    int iduser = cursor.getInt(cursor.getColumnIndexOrThrow("iduser"));

                    // Sử dụng constructor 6 tham số
                    SinhHoat sinhHoat = new SinhHoat(idsh, tensh, mota, ngaylaplai, khoangthoigian, iduser);
                    sinhHoatList.add(sinhHoat);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Lỗi khi lấy tất cả SinhHoat: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sinhHoatList;
    }

    // ===================================================================
    //  QUẢN LÝ LỊCH SỬ SINH HOẠT (LICHSUSINHHOAT)
    // ===================================================================

    // Chèn bản ghi hoàn thành
    public long insertLichSuSinhHoat(int idsh, int iduser, String ngaythuchien, String trangthaihoanthanh) {
        ContentValues values = new ContentValues();
        values.put("idsh", idsh);
        values.put("iduser", iduser);
        values.put("ngaythuchien", ngaythuchien);
        values.put("trangthaihoanthanh", trangthaihoanthanh);
        return database.insert("lichsusinhhoat", null, values);
    }

    // Kiểm tra trạng thái hoàn thành trong ngày
    public boolean isSinhHoatCompleted(int idsh, int iduser, String currentDate) {
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM lichsusinhhoat " +
                    "WHERE idsh = ? AND iduser = ? AND ngaythuchien = ?";
            String[] selectionArgs = { String.valueOf(idsh), String.valueOf(iduser), currentDate };

            cursor = database.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
            return false;
        } catch (Exception e) {
            Log.e("DatabaseManager", "Lỗi khi kiểm tra hoàn thành: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Xóa bản ghi hoàn thành (Hủy hoàn thành)
    public int deleteLichSuSinhHoat(int idsh, int iduser, String currentDate) {
        String whereClause = "idsh = ? AND iduser = ? AND ngaythuchien = ?";
        String[] whereArgs = { String.valueOf(idsh), String.valueOf(iduser), currentDate };
        return database.delete("lichsusinhhoat", whereClause, whereArgs);
    }

    // ===================================================================
    //  QUẢN LÝ NHẬT KÝ HÀNG NGÀY (HANGNGAY)
    // ===================================================================

    /**
     * Thêm hoặc cập nhật ghi chú Hàng ngày (Sử dụng ngày và ID người dùng làm khóa).
     * @param noiDung Nội dung ghi chú.
     * @param ngay Ngày (dạng yyyy-MM-dd).
     * @param idUser ID người dùng.
     */
    public long saveHangngayNote(String noiDung, String ngay, int idUser) {
        ContentValues values = new ContentValues();
        values.put("noidung", noiDung);
        values.put("ngay", ngay);
        values.put("iduser", idUser);

        // 1. Thử cập nhật trước
        int rows = database.update(
                "hangngay",
                values,
                "ngay = ? AND iduser = ?",
                new String[]{ngay, String.valueOf(idUser)}
        );

        // 2. Nếu không có hàng nào được cập nhật, thì chèn mới
        if (rows == 0) {
            try {
                return database.insertOrThrow("hangngay", null, values);
            } catch (Exception e) {
                Log.e("DB_HANGNGAY", "Lỗi khi chèn mới ghi chú: " + e.getMessage());
                return -1;
            }
        }
        return rows; // Trả về số hàng được cập nhật (1)
    }

    /**
     * Lấy nội dung ghi chú Hàng ngày theo ngày.
     * @param ngay Ngày (dạng yyyy-MM-dd).
     * @param idUser ID người dùng.
     * @return Nội dung ghi chú hoặc chuỗi rỗng nếu không tìm thấy.
     */
    public String getHangngayNote(String ngay, int idUser) {
        Cursor cursor = null;
        String noiDung = "";
        try {
            String query = "SELECT noidung FROM hangngay WHERE ngay = ? AND iduser = ?";
            cursor = database.rawQuery(query, new String[]{ngay, String.valueOf(idUser)});

            if (cursor.moveToFirst()) {
                noiDung = cursor.getString(cursor.getColumnIndexOrThrow("noidung"));
            }
        } catch (Exception e) {
            Log.e("DB_CALENDAR", "Lỗi khi lấy ghi chú hàng ngày: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return noiDung;
    }


    // ===================================================================
    //  CÁC PHƯƠNG THỨC INSERT KHÁC
    // ===================================================================

    public void insertBanner(String hinhAnh) {
        ContentValues values = new ContentValues();
        values.put("hinhanh", hinhAnh);
        database.insert("banner", null, values);
    }

    public void insertDoAn(String tenDa, String moTa, String hinh, double calo, double chatBeo, double chatDam, String ngay, String loai) {
        ContentValues values = new ContentValues();
        values.put("tenda", tenDa);
        values.put("mota", moTa);
        values.put("hinh", hinh);
        values.put("calo", calo);
        values.put("chatbeo", chatBeo);
        values.put("chatdam", chatDam);
        values.put("ngay", ngay);
        values.put("loai", loai);
        database.insert("doan", null, values);
    }

    public void insertHangngay(String noiDung, String ngay, int idUser) {
        ContentValues values = new ContentValues();
        values.put("noidung", noiDung);
        values.put("ngay", ngay);
        values.put("iduser", idUser);
        database.insert("hangngay", null, values);
    }
    public int deleteHangngayNote(String ngay, int idUser) {
        String whereClause = "ngay = ? AND iduser = ?";
        String[] whereArgs = { ngay, String.valueOf(idUser) };
        return database.delete("hangngay", whereClause, whereArgs);
    }
    public void insertCuongDo(String tenCd) {
        ContentValues values = new ContentValues();
        values.put("tencd", tenCd);
        database.insert("cuongdo", null, values);
    }

    public void insertLichtapluven(String tenLtl, String moTa, String thuLtl, int idCd, int idUser) {
        ContentValues values = new ContentValues();
        values.put("tenltl", tenLtl);
        values.put("mota", moTa);
        values.put("thultl", thuLtl);
        values.put("idcd", idCd);
        values.put("iduser", idUser);
        database.insert("lichtapluven", null, values);
    }
}