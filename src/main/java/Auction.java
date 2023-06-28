import java.util.List;

/**
 * Representation of a BidFTA auction.
 * Data will be stored in this class and output to a file.
 */
public class Auction {

    private String location;
    private String endDate;
    private List<String> pickupDates;

    private List<Item> items;

    public Auction(){
        Item i = Item.builder().build();
    }
}
