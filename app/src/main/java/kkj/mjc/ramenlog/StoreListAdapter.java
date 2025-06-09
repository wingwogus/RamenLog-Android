package kkj.mjc.ramenlog;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kkj.mjc.ramenlog.dto.Restaurant;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreViewHolder> {

    private final List<Restaurant> restaurantList;
    private OnItemClickListener onItemClickListener;

    public StoreListAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    // 👇 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Restaurant r = restaurantList.get(position);
        holder.name.setText(r.getName());
        holder.rating.setText("⭐ " + r.getAvgRating());
        holder.ranking.setText("RoR : 3위");

        Picasso.get().load(r.getImageUrl()).into(holder.image);

        // 👇 클릭 시 인터페이스 메서드 호출
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(r);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView name, rating, ranking;
        ImageView image;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.store_name);
            rating = itemView.findViewById(R.id.store_rating);
            ranking = itemView.findViewById(R.id.store_rank);
            image = itemView.findViewById(R.id.store_image);
        }
    }
}