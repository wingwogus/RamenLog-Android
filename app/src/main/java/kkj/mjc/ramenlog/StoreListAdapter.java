package kkj.mjc.ramenlog;

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

// RecyclerView에 맛집 리스트를 표시하는 어댑터 클래스
public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreViewHolder> {

    // 데이터 리스트: Restaurant 객체 목록
    private final List<Restaurant> restaurantList;

    // 외부에서 클릭 이벤트를 받을 수 있도록 인터페이스 선언
    private OnItemClickListener onItemClickListener;

    // 생성자: 어댑터 생성 시 맛집 리스트를 전달받음
    public StoreListAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    // 아이템 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);  // 클릭된 Restaurant 객체 전달
    }

    // 외부에서 클릭 리스너를 설정할 수 있도록 메서드 제공
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // ViewHolder가 처음 만들어질 때 실행: 아이템 뷰 inflate 후 ViewHolder 생성
    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    // ViewHolder가 재사용될 때 데이터를 바인딩하는 메서드
    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Restaurant r = restaurantList.get(position);

        // 가게명, 평점, 랭킹 텍스트 설정
        holder.name.setText(r.getName());
        holder.rating.setText("⭐ " + r.getAvgRating());
        holder.ranking.setText("RoR : 3위"); // 랭킹은 예시 값 (실제 값으로 변경 가능)

        // 이미지 로드 (Picasso 라이브러리 사용)
        Picasso.get().load(r.getImageUrl()).into(holder.image);

        // 아이템 클릭 시 설정된 리스너 호출
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(r);
            }
        });
    }

    // 전체 아이템 개수 반환
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // RecyclerView의 각 아이템 뷰를 담는 ViewHolder 클래스
    static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView name, rating, ranking;
        ImageView image;

        // ViewHolder 생성자: 뷰 객체들을 바인딩
        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.store_name);     // 가게 이름
            rating = itemView.findViewById(R.id.store_rating); // 평점
            ranking = itemView.findViewById(R.id.store_rank);  // 랭킹
            image = itemView.findViewById(R.id.store_image);   // 썸네일 이미지
        }
    }
}