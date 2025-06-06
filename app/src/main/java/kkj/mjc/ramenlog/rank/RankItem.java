package kkj.mjc.ramenlog.rank;

public class RankItem {
    private final int rank;
    private final String restaurantName;
    private final double rating;

    public RankItem(int rank, String restaurantName, double rating) {
        this.rank = rank;
        this.restaurantName = restaurantName;
        this.rating = rating;
    }

    public int getRank() { return rank; }
    public String getRestaurantName() { return restaurantName; }
    public double getRating() { return rating; }
}
