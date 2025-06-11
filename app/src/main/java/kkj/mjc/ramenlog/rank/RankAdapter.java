package kkj.mjc.ramenlog.rank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kkj.mjc.ramenlog.R;
import java.util.List;

public class RankAdapter
        extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RankItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private final List<RankItem> items;

    public RankAdapter(List<RankItem> items) {
        this.items = items;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rank, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        RankItem item = items.get(pos);
        h.tvRankNumber.setText(String.valueOf(item.getRank()));
        h.tvRestaurantName.setText(item.getRestaurantName());
        h.tvRating.setText(String.valueOf(item.getRating()));
        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankNumber, tvRestaurantName, tvRating;
        ImageView ivStar, ivArrow;

        ViewHolder(View itemView) {
            super(itemView);
            tvRankNumber      = itemView.findViewById(R.id.tvRankNumber);
            tvRestaurantName  = itemView.findViewById(R.id.tvRestaurantName);
            tvRating          = itemView.findViewById(R.id.tvRating);
            ivStar            = itemView.findViewById(R.id.ivStar);
            ivArrow           = itemView.findViewById(R.id.ivArrow);
        }
    }
}
