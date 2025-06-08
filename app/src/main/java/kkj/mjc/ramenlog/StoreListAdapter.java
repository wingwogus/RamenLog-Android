/*
package kkj.mjc.ramenlog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kkj.mjc.ramenlog.dto.Restaurant;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreViewHolder> {

    private final List<Restaurant> restaurantList;

    public StoreListAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

*/
/*    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Restaurant r = restaurantList.get(position);
        holder.name.setText(r.getName());
        holder.rating.setText("⭐ " + r.getAvgRating());
        holder.ranking.setText("RoR : " + r.getRank() + "위");

        // Glide 또는 Picasso로 이미지 표시
        Picasso.with(holder.itemView.getContext())
                .load(r.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.image);
    }*//*


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
}*/
