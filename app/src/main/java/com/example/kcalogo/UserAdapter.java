package com.example.kcalogo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    private List<User> userList;
    private OnItemClickListener listener;

    // Chỉnh sửa hàm khởi tạo để nhận thêm OnItemClickListener
    public UserAdapter(List<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUsername.setText(user.getTenUser());
        holder.tvAccount.setText("Tài khoản: " + user.getTaiKhoan());
        holder.tvPhone.setText("Số điện thoại: " + user.getSdt());

        // Gán sự kiện cho nút Sửa
        holder.btnEditItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(position);
            }
        });

        // Gán sự kiện cho nút Xóa
        holder.btnDeleteItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Thêm phương thức để xóa người dùng
    public void removeUser(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvAccount, tvPhone;
        public Button btnEditItem, btnDeleteItem;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_item_username);
            tvAccount = itemView.findViewById(R.id.tv_item_account);
            tvPhone = itemView.findViewById(R.id.tv_item_phone);
            btnEditItem = itemView.findViewById(R.id.btn_edit_item);
            btnDeleteItem = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}