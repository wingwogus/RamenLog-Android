package kkj.mjc.ramenlog.rank;

public class RankItem {

    private final long id;
    private final int rank;
    private final String restaurantName;
    private final double rating;

    public RankItem(long id, int rank, String restaurantName, double rating) {
        this.id = id;
        this.rank = rank;
        this.restaurantName = restaurantName;
        this.rating = rating;
    }

    public long getId() {return id;}
    public int getRank() { return rank; }
    public String getRestaurantName() { return restaurantName; }
    public double getRating() { return rating; }
}
