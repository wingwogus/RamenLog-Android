package kkj.mjc.ramenlog.like;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kkj.mjc.ramenlog.R;

class LikeItemAdapter extends RecyclerView.Adapter<LikeItemAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(LikeItem item);
    }

    private final List<LikeItem> likeItems;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public LikeItemAdapter(List<LikeItem> likeItems) {
        this.likeItems = likeItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_like, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(likeItems.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(likeItems.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return likeItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textName;
        private final TextView textRating;
        private final TextView textAddress;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textRating = itemView.findViewById(R.id.textRating);
            textAddress = itemView.findViewById(R.id.textAddress);
            imageView = itemView.findViewById(R.id.imageThumbnail);
        }

        public void bind(LikeItem item) {
            textName.setText(item.name);
            textRating.setText("★ " + item.score);
            textAddress.setText(item.address);
            String imageUrl = item.imageUrl;
            if (!imageUrl.equals("null")) {
                Picasso.get()
                        .load(imageUrl)
                        .into(imageView);
            }
        }
    }
}