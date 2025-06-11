package kkj.mjc.ramenlog.dto;

// 서버에서 전달받은 라멘집 정보를 담는 DTO 클래스
public class Restaurant {

    // 고유 식별자 (DB의 기본키)
    private long id;

    // 가게 이름
    private String name;

    // 전체 주소 (도로명 주소)
    private String address;

    // 위도 (위치 정보에 사용)
    private double latitude;

    // 경도 (위치 정보에 사용)
    private double longitude;

    // 평균 평점 (사용자 리뷰 기반)
    private double avgRating;

    // 썸네일 또는 대표 이미지 URL
    private String imageUrl;

    // getter/setter 메서드들
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAvgRating() {
        return avgRating;
    }
    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // ListView, RecyclerView 등의 UI 요소에서 문자열 형태로 출력될 때 사용됨
    @Override
    public String toString() {
        return name + "\n" + address;
    }
}