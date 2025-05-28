package kkj.mjc.ramenlog.hometype;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import kkj.mjc.ramenlog.R;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.VH> {
    private final List<TypeItem> items = new ArrayList<>();

    public void setItems(List<TypeItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_type, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TypeItem it = items.get(position);
        holder.img.setImageResource(it.imageRes);
        holder.name.setText(it.name);
        holder.desc.setText(it.desc);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView  name, desc;

        VH(View itemView) {
            super(itemView);
            img  = itemView.findViewById(R.id.imgType);
            name = itemView.findViewById(R.id.tvTypeName);
            desc = itemView.findViewById(R.id.tvTypeDesc);
        }
    }
}
