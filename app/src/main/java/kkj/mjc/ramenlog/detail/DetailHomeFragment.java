package kkj.mjc.ramenlog.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import kkj.mjc.ramenlog.R;

// 맛집 상세 페이지의 "홈 탭" 역할을 하는 Fragment
// 지도, 주소, 영업시간 등을 표시함
public class DetailHomeFragment extends Fragment implements OnMapReadyCallback {

    // 지도 객체
    private GoogleMap mMap;

    // 화면의 텍스트뷰들
    private TextView tvAddress, tvOpeningHours, tvBreakTime;

    // 지도와 정보 표시를 위한 데이터
    private double latitude;
    private double longitude;
    private String restaurantName;
    private String fullAddress;

    // 프래그먼트 레이아웃 생성
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_home, container, false);
    }

    // 뷰가 생성된 후 호출되어 UI 요소 초기화 및 데이터 바인딩
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 레이아웃 요소 참조
        tvAddress = view.findViewById(R.id.tv_address);
        tvOpeningHours = view.findViewById(R.id.tv_opening_hours);
        tvBreakTime = view.findViewById(R.id.tv_break_time);

        // Bundle로부터 위도, 경도, 주소, 가게명 전달받기
        Bundle args = getArguments();
        if (args != null) {
            latitude = args.getDouble("latitude");
            longitude = args.getDouble("longitude");
            fullAddress = args.getString("fullAddress");
            restaurantName = args.getString("restaurantName");
        }

        // 지도 프래그먼트를 현재 프래그먼트 내부에 동적으로 추가
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)  // map_container는 FrameLayout ID
                .commit();

        // 구글맵 비동기 초기화
        mapFragment.getMapAsync(this);

        // 텍스트뷰에 정보 표시
        tvAddress.setText(fullAddress); // 전체 주소
        tvOpeningHours.setText("오늘 ??:?? ~ ??:??"); // 영업시간 예시
        tvBreakTime.setText("-"); // 브레이크 타임 없을 경우
    }

    // 지도 준비 완료 시 호출되는 콜백
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // 마커를 추가하고 카메라를 해당 위치로 이동
        LatLng position = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 20f)); // 줌 레벨 높게 설정 (가까이 보기)
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(restaurantName)); // 마커에 가게 이름 표시
    }
}