import lombok.Data;

/**
 * Representation of an Auction Item.
 */
@Data
public class Item {

    private String title;
    private double MSRP;
    private String condition;
    private String description;
    private String lotCode;
    private String additionalInfo;
    private double currentBid;
    private String pickupLocation;
    private String URL;

    public Item(String title, double msrp, String condition, String description,
                String additionalInfo, double currentBid, String pickupLocation, String lotCode, String currentUrl) {
        this.title = title;
        this.MSRP = msrp;
        this.condition = condition;
        this.description = description;
        this.additionalInfo = additionalInfo;
        this.currentBid = currentBid;
        this.pickupLocation = pickupLocation;
        this.lotCode = lotCode;
        this.URL = currentUrl;
    }

}
