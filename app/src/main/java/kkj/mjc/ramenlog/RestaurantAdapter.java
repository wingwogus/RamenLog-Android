package kkj.mjc.ramenlog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kkj.mjc.ramenlog.dto.Restaurant;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurantList;

    public RestaurantAdapter(List<Restaurant> list) {
        this.restaurantList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, rating, ror;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.restaurant_name);
            rating = itemView.findViewById(R.id.rating);
            ror = itemView.findViewById(R.id.ror);
            image = itemView.findViewById(R.id.restaurant_image);
        }
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant item = restaurantList.get(position);
        holder.name.setText(item.getName());
        holder.rating.setText(" ⭐ " + item.getAvgRating());
        holder.ror.setText("RoR : 3위");

        // 이미지가 있다면 Glide 사용 (선택사항)
        // Glide.with(holder.image.getContext()).load(item.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void updateList(List<Restaurant> list) {
        this.restaurantList = list;
        notifyDataSetChanged();
    }
}
