package kkj.mjc.ramenlog.mylog;

public class ReviewItem {
    private String restaurantName;
    private double rating;
    private String imageUrl1, imageUrl2, imageUrl3;
    private String content;

    private String createdAt;

    public ReviewItem(String restaurantName, double rating, String imageUrl1, String imageUrl2, String imageUrl3, String content, String createdAt) {
        this.restaurantName = restaurantName;
        this.rating = rating;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.content   = content;
        this.createdAt = createdAt;
    }
    public String getRestaurantName() { return restaurantName; }
    public double getRating()         { return rating; }
    public String getImageUrl1()      { return imageUrl1; }
    public String getImageUrl2()      { return imageUrl2; }
    public String getImageUrl3()      { return imageUrl3; }
    public String getContent()        { return content;   }

    public String getCreatedAt() {return createdAt;}
}
