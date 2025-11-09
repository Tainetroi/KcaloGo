package com.example.kcalogo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import java.util.List;

public class SinhHoatAdapter extends RecyclerView.Adapter<SinhHoatAdapter.SinhHoatViewHolder> {
    private List<SinhHoat> sinhHoatList;
    private Context context;
    private OnItemInteractionListener listener;
    private final IsCompletedChecker completedChecker;

    // Interface để giao tiếp ngược lại với Activity
    public interface OnItemInteractionListener {
        void onCompleteClick(SinhHoat sinhHoat);
        void onEditClick(SinhHoat sinhHoat);
        void onDeleteClick(SinhHoat sinhHoat);
    }

    // Interface để kiểm tra trạng thái hoàn thành
    public interface IsCompletedChecker {
        boolean isCompletedForToday(int idsh);
    }

    public SinhHoatAdapter(Context context, List<SinhHoat> sinhHoatList, OnItemInteractionListener listener, IsCompletedChecker completedChecker) {
        this.context = context;
        this.sinhHoatList = sinhHoatList;
        this.listener = listener;
        this.completedChecker = completedChecker;
    }

    @NonNull
    @Override
    public SinhHoatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sinh_hoat, parent, false);
        return new SinhHoatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SinhHoatViewHolder holder, int position) {
        SinhHoat sinhHoat = sinhHoatList.get(position);
        holder.tvTenSH.setText(sinhHoat.getTensh());
        holder.tvMoTa.setText(sinhHoat.getMota());

        // Hiển thị ngày lặp lại (ví dụ: T2, T4, CN)
        holder.tvThu.setText(sinhHoat.getNgaylaplai().replace("Thứ ", "T").replace("Chủ Nhật", "CN"));

        // Hiển thị khoảng thời gian lặp lại (MỚI)
        int interval = sinhHoat.getKhoangthoigian();
        if (interval > 0) {
            holder.tvInterval.setText(interval + " phút");
        } else {
            holder.tvInterval.setText("Không lặp");
        }


        // 1. Kiểm tra trạng thái hoàn thành cho ngày hôm nay
        boolean isCompleted = completedChecker.isCompletedForToday(sinhHoat.getIdsh());

        // Cập nhật giao diện dựa trên trạng thái
        if (isCompleted) {
            holder.btnCheckComplete.setImageResource(R.drawable.ic_check_white);
            holder.btnCheckComplete.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green_completed));
            holder.tvTenSH.setAlpha(0.5f);
            holder.tvMoTa.setAlpha(0.5f);
            holder.tvThu.setAlpha(0.5f);
            holder.tvInterval.setAlpha(0.5f); // Làm mờ cả thông tin lặp lại
        } else {
            holder.btnCheckComplete.setImageResource(R.drawable.ic_check_gray);
            holder.btnCheckComplete.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray_uncompleted));
            holder.tvTenSH.setAlpha(1.0f);
            holder.tvMoTa.setAlpha(1.0f);
            holder.tvThu.setAlpha(1.0f);
            holder.tvInterval.setAlpha(1.0f);
        }

        // 2. Xử lý sự kiện hoàn thành
        holder.btnCheckComplete.setOnClickListener(v -> {
            listener.onCompleteClick(sinhHoat);
        });

        // 3. Xử lý menu tùy chọn (Sửa/Xóa)
        holder.btnOptions.setOnClickListener(v -> {
            showPopupMenu(v, sinhHoat);
        });
    }

    @Override
    public int getItemCount() {
        return sinhHoatList.size();
    }

    public void updateList(List<SinhHoat> newList) {
        this.sinhHoatList = newList;
        notifyDataSetChanged();
    }

    private void showPopupMenu(View view, SinhHoat sinhHoat) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.menu_options);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    listener.onEditClick(sinhHoat);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    listener.onDeleteClick(sinhHoat);
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public static class SinhHoatViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTenSH, tvMoTa, tvThu, tvInterval; // <--- ĐÃ THÊM tvInterval
        public ImageButton btnCheckComplete, btnOptions;

        public SinhHoatViewHolder(View itemView) {
            super(itemView);
            tvTenSH = itemView.findViewById(R.id.tvTenSH);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            tvThu = itemView.findViewById(R.id.tvThu);
            tvInterval = itemView.findViewById(R.id.tvInterval); // <--- Tham chiếu MỚI
            btnCheckComplete = itemView.findViewById(R.id.btnCheckComplete);
            btnOptions = itemView.findViewById(R.id.btnOptions);
        }
    }
}