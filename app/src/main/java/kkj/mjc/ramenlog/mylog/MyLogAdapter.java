package kkj.mjc.ramenlog.mylog;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import kkj.mjc.ramenlog.R;

public class MyLogAdapter
        extends RecyclerView.Adapter<MyLogAdapter.ViewHolder> {

    private final List<ReviewItem> items;


    public MyLogAdapter(List<ReviewItem> items) {
        this.items = items;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mylog, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ReviewItem r = items.get(pos);
        h.tvName.setText(r.getRestaurantName());
        h.tvRating.setText(String.valueOf(r.getRating()));
        h.tvContent.setText(r.getContent());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime dateTime = LocalDateTime.parse(r.getCreatedAt(), inputFormatter);
        h.tvDate.setText(dateTime.format(outputFormatter));
        // 이미지 로딩 필요하면: Glide.with(h.itemView.getContext())
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRating, tvContent, tvDate;
        ImageView ivPic1, ivPic2, ivPic3, ivStar;

        ViewHolder(View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tvRestaurantName);
            tvRating  = itemView.findViewById(R.id.tvRating);
            tvContent = itemView.findViewById(R.id.tvContent);
            ivPic1    = itemView.findViewById(R.id.ivPic1);
            ivPic2    = itemView.findViewById(R.id.ivPic2);
            ivPic3    = itemView.findViewById(R.id.ivPic3);
            ivStar    = itemView.findViewById(R.id.ivStar);
            tvDate    = itemView.findViewById(R.id.tvDate);
        }
    }
}

