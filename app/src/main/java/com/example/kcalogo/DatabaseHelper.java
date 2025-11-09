package com.example.kcalogo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "KcaloGo.db";
    // Tăng số phiên bản từ 1 lên 2
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Lệnh CREATE TABLE cho bảng user
        String CREATE_USER_TABLE = "CREATE TABLE user (" +
                "iduser INTEGER PRIMARY KEY," +
                "tenuser TEXT," +
                "taikhoan TEXT," +
                "matkhau TEXT," +
                "sdt TEXT," +
                "gioitinh TEXT," +
                "chucvu INTEGER" + // Đã thêm cột này
                ");";

        // Lệnh CREATE TABLE cho bảng banner
        String CREATE_BANNER_TABLE = "CREATE TABLE banner (" +
                "idhinhanh INTEGER PRIMARY KEY," +
                "hinhanh TEXT" +
                ");";

        // Lệnh CREATE TABLE cho bảng doan
        String CREATE_DOAN_TABLE = "CREATE TABLE doan (" +
                "idda INTEGER PRIMARY KEY," +
                "tenda TEXT," +
                "mota TEXT," +
                "hinh TEXT," +
                "calo REAL," +
                "chatbeo REAL," +
                "chatdam REAL," +
                "ngay DATE," +
                "loai TEXT" +
                ");";

        // Lệnh CREATE TABLE cho bảng hangngay (đã sửa)
        String CREATE_HANGNGAY_TABLE = "CREATE TABLE hangngay (" +
                "idhn INTEGER PRIMARY KEY," +
                "noidung TEXT," +
                "ngay DATE," +
                "iduser INTEGER," +
                "FOREIGN KEY (iduser) REFERENCES user(iduser)" +
                ");";

        // Lệnh CREATE TABLE cho bảng sinhhoat
        String CREATE_SINHHOAT_TABLE = "CREATE TABLE sinhhoat (" +
                "idsh INTEGER PRIMARY KEY," +
                "tensh TEXT," +
                "mota TEXT," +
                "ngaylaplai TEXT," + // Cột mới: "Thứ Hai,Thứ Ba,..."
                "iduser INTEGER," +
                "khoangthoigian INTEGER," +
                "FOREIGN KEY (iduser) REFERENCES user(iduser)" +
                ");";

        // Lệnh CREATE TABLE cho bảng cuongdo
        String CREATE_CUONGDO_TABLE = "CREATE TABLE cuongdo (" +
                "idcd INTEGER PRIMARY KEY," +
                "tencd TEXT" +
                ");";

        // Lệnh CREATE TABLE cho bảng lichtapluven
        String CREATE_LICHTAPLUVEN_TABLE = "CREATE TABLE lichtapluven (" +
                "idltl INTEGER PRIMARY KEY," +
                "tenltl TEXT," +
                "mota TEXT," +
                "thultl TEXT," +
                "idcd INTEGER," +
                "iduser INTEGER," +
                "FOREIGN KEY (idcd) REFERENCES cuongdo(idcd)," +
                "FOREIGN KEY (iduser) REFERENCES user(iduser)" +
                ");";

        String CREATE_LICHSUSINHHOAT_TABLE = "CREATE TABLE lichsusinhhoat (" +
                "idlsnh INTEGER PRIMARY KEY AUTOINCREMENT," + // Thêm khóa chính tự động tăng
                "idsh INTEGER," +
                "iduser INTEGER," +
                "ngaythuchien TEXT," +
                "trangthaihoanthanh TEXT," +
                "FOREIGN KEY (idsh) REFERENCES sinhhoat(idsh) ON DELETE CASCADE," +
                "FOREIGN KEY (iduser) REFERENCES user(iduser) ON DELETE CASCADE" + // Thêm ràng buộc khóa ngoại cho iduser
                ");";

        // Thực thi các lệnh tạo bảng
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_BANNER_TABLE);
        db.execSQL(CREATE_DOAN_TABLE);
        db.execSQL(CREATE_HANGNGAY_TABLE);
        db.execSQL(CREATE_SINHHOAT_TABLE);
        db.execSQL(CREATE_CUONGDO_TABLE);
        db.execSQL(CREATE_LICHTAPLUVEN_TABLE);
        db.execSQL(CREATE_LICHSUSINHHOAT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS lichtapluven");
        db.execSQL("DROP TABLE IF EXISTS cuongdo");
        db.execSQL("DROP TABLE IF EXISTS sinhhoat");
        db.execSQL("DROP TABLE IF EXISTS hangngay"); // đã sửa
        db.execSQL("DROP TABLE IF EXISTS doan");
        db.execSQL("DROP TABLE IF EXISTS banner");
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS lichsusinhhoat");

        // Tạo lại các bảng mới
        onCreate(db);
    }
}