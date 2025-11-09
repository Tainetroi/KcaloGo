package com.example.kcalogo;



import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.database.Cursor;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;

import android.widget.LinearLayout;

import android.widget.Toast;

import java.util.ArrayList;

import java.util.List;



public class AdminActivity extends AppCompatActivity {



    private RecyclerView rvUsers;

    private DatabaseManager dbManager;

    private Button btnManageUsers, btnManageBanners, btnManageSchedules, btnManageFoods, btnLogout;

    private Button btnAddUser;

    private LinearLayout layoutUserManagement;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);



        dbManager = new DatabaseManager(this);

        dbManager.open();



        rvUsers = findViewById(R.id.rv_users);

        btnManageUsers = findViewById(R.id.btn_manage_users);

        btnManageBanners = findViewById(R.id.btn_manage_banners);

        btnManageSchedules = findViewById(R.id.btn_manage_schedules);

        btnManageFoods = findViewById(R.id.btn_manage_foods);

        btnLogout = findViewById(R.id.btn_logout);



// The add button remains, but the edit and delete buttons are now in each list item

        btnAddUser = findViewById(R.id.btn_add_user);

        layoutUserManagement = findViewById(R.id.layout_user_management);



        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        rvUsers.setVisibility(View.GONE);

        layoutUserManagement.setVisibility(View.GONE);



        setupButtonListeners();

    }



    @Override

    protected void onResume() {

        super.onResume();

// Reloads user list every time this activity resumes

// This ensures the list is updated after adding, editing, or deleting a user

        if (rvUsers.getVisibility() == View.VISIBLE) {

            loadUsers();

        }

    }



    private void loadUsers() {

        List<User> userList = new ArrayList<>();

        Cursor cursor = dbManager.getAllUsers();



        if (cursor != null && cursor.moveToFirst()) {

            int idIndex = cursor.getColumnIndex("iduser");

            int tenUserIndex = cursor.getColumnIndex("tenuser");

            int taiKhoanIndex = cursor.getColumnIndex("taikhoan");

            int sdtIndex = cursor.getColumnIndex("sdt");

            int chucVuIndex = cursor.getColumnIndex("chucvu");

            int matKhauIndex = cursor.getColumnIndex("matkhau");

            int gioiTinhIndex = cursor.getColumnIndex("gioitinh");



            if (idIndex != -1 && tenUserIndex != -1 && taiKhoanIndex != -1 && sdtIndex != -1 && chucVuIndex != -1) {

                do {

                    int id = cursor.getInt(idIndex);

                    String tenUser = cursor.getString(tenUserIndex);

                    String taiKhoan = cursor.getString(taiKhoanIndex);

                    String matKhau = cursor.getString(matKhauIndex);

                    String sdt = cursor.getString(sdtIndex);

                    String gioiTinh = cursor.getString(gioiTinhIndex);

                    int chucVu = cursor.getInt(chucVuIndex);



                    User user = new User(id, tenUser, taiKhoan, matKhau, sdt, gioiTinh, chucVu);

                    userList.add(user);

                } while (cursor.moveToNext());

            } else {

                Toast.makeText(this, "Lỗi: Một hoặc nhiều cột dữ liệu không tồn tại.", Toast.LENGTH_LONG).show();

            }

            cursor.close();

        } else {

            Toast.makeText(this, "Không có người dùng nào được tìm thấy.", Toast.LENGTH_SHORT).show();

        }



        UserAdapter adapter = new UserAdapter(userList, new UserAdapter.OnItemClickListener() {

            @Override

            public void onEditClick(int position) {

                User userToEdit = userList.get(position);

                Intent intent = new Intent(AdminActivity.this, EditUserActivity.class);

                intent.putExtra("user_id", userToEdit.getId());

                intent.putExtra("ten_user", userToEdit.getTenUser());

                intent.putExtra("tai_khoan", userToEdit.getTaiKhoan());

                intent.putExtra("mat_khau", userToEdit.getMatKhau());

                intent.putExtra("sdt", userToEdit.getSdt());

                intent.putExtra("gioi_tinh", userToEdit.getGioiTinh());

                intent.putExtra("chuc_vu", userToEdit.getChucVu());

                startActivity(intent);

            }



            @Override

            public void onDeleteClick(int position) {

                User userToDelete = userList.get(position);

                int deletedRows = dbManager.deleteUser(userToDelete.getId());

                if (deletedRows > 0) {

// Update list and notify adapter

                    ((UserAdapter) rvUsers.getAdapter()).removeUser(position);

                    Toast.makeText(AdminActivity.this, "Đã xóa người dùng: " + userToDelete.getTenUser(), Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(AdminActivity.this, "Xóa thất bại.", Toast.LENGTH_SHORT).show();

                }

            }

        });

        rvUsers.setAdapter(adapter);

    }



    private void setupButtonListeners() {

        btnManageUsers.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                loadUsers();

                rvUsers.setVisibility(View.VISIBLE);

                layoutUserManagement.setVisibility(View.VISIBLE);

                Toast.makeText(AdminActivity.this, "Đang hiển thị danh sách người dùng", Toast.LENGTH_SHORT).show();

            }

        });



        btnManageBanners.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Toast.makeText(AdminActivity.this, "Chuyển đến màn hình quản lý Banner", Toast.LENGTH_SHORT).show();

                rvUsers.setVisibility(View.GONE);

                layoutUserManagement.setVisibility(View.GONE);

            }

        });



        btnManageSchedules.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Toast.makeText(AdminActivity.this, "Chuyển đến màn hình quản lý Lịch tập luyện", Toast.LENGTH_SHORT).show();

                rvUsers.setVisibility(View.GONE);

                layoutUserManagement.setVisibility(View.GONE);

            }

        });



        btnManageFoods.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Toast.makeText(AdminActivity.this, "Chuyển đến màn hình quản lý Đồ ăn", Toast.LENGTH_SHORT).show();

                rvUsers.setVisibility(View.GONE);

                layoutUserManagement.setVisibility(View.GONE);

            }

        });



        btnAddUser.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent(AdminActivity.this, AddUserActivity.class);

                startActivity(intent);

            }

        });



        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Toast.makeText(AdminActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);

                startActivity(intent);

                finish();

            }

        });

    }



    @Override

    protected void onDestroy() {

        super.onDestroy();

        if (dbManager != null) {

            dbManager.close();

        }

    }

}