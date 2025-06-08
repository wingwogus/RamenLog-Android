package kkj.mjc.ramenlog.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import kkj.mjc.ramenlog.R;
import kkj.mjc.ramenlog.detail.DetailReviewAdapter;
import kkj.mjc.ramenlog.mylog.ReviewItem;

public class DetailReviewAdapter
        extends RecyclerView.Adapter<DetailReviewAdapter.ViewHolder> {

    private final List<DetailReviewItem> items;

    public DetailReviewAdapter(List<DetailReviewItem> items) {
        this.items = items;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_detail_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        DetailReviewItem item = items.get(pos);
        h.tvNickName.setText(item.getNickname());
        h.tvRating.setText(String.valueOf(item.getRating()));
        h.tvContent.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickName, tvRating, tvContent;
        ImageView ivPic1, ivPic2, ivPic3, ivStar;

        ViewHolder(View itemView) {
            super(itemView);
            tvNickName = itemView.findViewById(R.id.tvUserName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvContent = itemView.findViewById(R.id.tvContent);
            ivPic1 = itemView.findViewById(R.id.ivPic1);
            ivPic2 = itemView.findViewById(R.id.ivPic2);
            ivPic3 = itemView.findViewById(R.id.ivPic3);
            ivStar = itemView.findViewById(R.id.ivStar);
        }
    }
}
