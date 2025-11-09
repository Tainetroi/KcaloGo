package com.example.kcalogo;

public class SinhHoat {
    private int idsh;
    private String tensh;
    private String mota;
    private String ngaylaplai;
    private int khoangthoigian; // <--- THUỘC TÍNH MỚI: Khoảng thời gian lặp lại (phút)
    private int iduser;

    // ===================================================================
    // CONSTRUCTOR (MỚI - SỬ DỤNG CHO DB MANAGER READ)
    // ===================================================================
    public SinhHoat(int idsh, String tensh, String mota, String ngaylaplai, int khoangthoigian, int iduser) {
        this.idsh = idsh;
        this.tensh = tensh;
        this.mota = mota;
        this.ngaylaplai = ngaylaplai;
        this.khoangthoigian = khoangthoigian;
        this.iduser = iduser;
    }

    // GHI CHÚ: Nếu cần duy trì constructor cũ cho mục đích tương thích
    /*
    public SinhHoat(int idsh, String tensh, String mota, String ngaylaplai, int iduser) {
        this.idsh = idsh;
        this.tensh = tensh;
        this.mota = mota;
        this.ngaylaplai = ngaylaplai;
        this.khoangthoigian = 0; // Gán mặc định
        this.iduser = iduser;
    }
    */

    // ===================================================================
    // GETTERS AND SETTERS (ĐÃ CẬP NHẬT)
    // ===================================================================
    public int getIdsh() { return idsh; }
    public String getTensh() { return tensh; }
    public String getMota() { return mota; }
    public String getNgaylaplai() { return ngaylaplai; }
    public int getKhoangthoigian() { return khoangthoigian; } // <--- GETTER MỚI
    public int getIduser() { return iduser; }

    // Bạn có thể thêm setters nếu cần thiết
    /*
    public void setTensh(String tensh) { this.tensh = tensh; }
    // ...
    public void setKhoangthoigian(int khoangthoigian) { this.khoangthoigian = khoangthoigian; }
    */
}