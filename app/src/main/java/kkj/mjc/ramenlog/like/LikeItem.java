package kkj.mjc.ramenlog.like;

public class LikeItem {
    long id;
    String name;
    String address;
    String imageUrl;
    double score;

    public LikeItem(long id, String name, String address, String imageUrl, double score) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.score = score;
    }

    public long getId() {
        return id;
    }
}
