package kkj.mjc.ramenlog.detail;

public class DetailReviewItem {
    private String nickName;
    private double rating;
    private String imageUrl1, imageUrl2, imageUrl3;
    private String content;

    public DetailReviewItem(String nickName, double rating, String imageUrl1, String imageUrl2, String imageUrl3, String content) {
        this.nickName = nickName;
        this.rating = rating;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.content   = content;
    }
    public String getNickname() { return nickName; }
    public double getRating()         { return rating; }
    public String getImageUrl1()      { return imageUrl1; }
    public String getImageUrl2()      { return imageUrl2; }
    public String getImageUrl3()      { return imageUrl3; }
    public String getContent()        { return content;   }
}

