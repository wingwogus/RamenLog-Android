package kkj.mjc.ramenlog.mylog;

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

// RecyclerView.Adapter를 상속하여 어댑터 구현 → RecyclerView와 데이터를 연결하는 역할
public class MyLogAdapter
        extends RecyclerView.Adapter<MyLogAdapter.ViewHolder> {

    // 표시할 데이터 리스트 (내 리뷰 목록)
    private final List<ReviewItem> items;

    // 외부에서 데이터 리스트를 전달받음
    public MyLogAdapter(List<ReviewItem> items) {
        this.items = items;
    }

    // ViewHolder객체를 생성하여 반환 → item_mylog.xml레이아웃을 inflate해서 ViewHolder에 연결
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mylog, parent, false);
        return new ViewHolder(v);
    }

    // RecyclerView가 각 아이템을 표시할 때 호출됨 → 데이터와 ViewHolder를 바인딩하는 역할
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        // 현재 위치에 해당하는 리뷰 데이터 가져오기
        ReviewItem r = items.get(pos);

        // ViewHolder 의 각 뷰에 데이터 설정
        h.tvName.setText(r.getRestaurantName());
        h.tvRating.setText(String.valueOf(r.getRating()));
        h.tvContent.setText(r.getContent());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime dateTime = LocalDateTime.parse(r.getCreatedAt(), inputFormatter);
        h.tvDate.setText(dateTime.format(outputFormatter));
        // 이미지 로딩 필요하면: Glide.with(h.itemView.getContext())
    }

    // RecyclerView에 표시할 아이템 개수 반환
    @Override
    public int getItemCount() {
        return items.size();
    }

    // RecyclerView에서 각 아이템 뷰를 재활용할 때 사용
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

