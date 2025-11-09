package com.example.kcalogo;

public class User {
    private int id;
    private String tenUser;
    private String taiKhoan;
    private String matKhau;
    private String sdt;
    private String gioiTinh;
    private int chucVu;

    public User(int id, String tenUser, String taiKhoan, String matKhau, String sdt, String gioiTinh, int chucVu) {
        this.id = id;
        this.tenUser = tenUser;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
        this.chucVu = chucVu;
    }

    // Thêm các phương thức getter và setter
    public int getId() {
        return id;
    }

    public String getTenUser() {
        return tenUser;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public String getSdt() {
        return sdt;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public int getChucVu() {
        return chucVu;
    }
}