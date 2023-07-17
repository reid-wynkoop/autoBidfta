import lombok.Builder;
import lombok.Data;

/**
 * Representation of an Auction Item.
 */
@Data
public class Item {

    private String title;
    private String MSRP;
    private String condition;
    private String description;
    private String lotCode;
    private String additionalInfo;
    private String currentBid;
    private String pickupLocation;

    public Item() {

    }
}
