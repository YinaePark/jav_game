package domain.item;

/**
 * Represents a harvestable item that can be planted, grown, and sold.
 */
public class HarvestItem extends Item implements Growable, Purchasable {
    private double price;          // Price of the item when purchased
    private int growthTime;        // Time required for the item to fully grow (in seconds)
    private long plantedTimestamp; // Timestamp of when the item was planted (in milliseconds)
    private boolean isHarvested;   // Indicates if the item has been harvested

    public HarvestItem(String name, double price, int growthTime) {
        super(name);
        this.price = price;
        this.growthTime = growthTime;
        this.plantedTimestamp = -1; // Initial state: not planted
        this.isHarvested = false;   // Initial state: not harvested
    }

    /**
     * Plants the item, initializing its growth process.
     */
    @Override
    public void plant() {
        System.out.println(getName() + " has been planted.");
        this.plantedTimestamp = System.currentTimeMillis(); // Record planting time
        this.isHarvested = false; // Mark as currently growing
    }

    /**
     * Checks if the item is ready to be harvested based on elapsed growth time.
     * @return true if the item is fully grown, false otherwise.
     */
    @Override
    public boolean isReadyToHarvest() {
        return getTimeElapsed() >= this.growthTime;
    }

    /**
     * Marks the item as harvested if it is ready, otherwise notifies that it cannot be harvested yet.
     */
    @Override
    public void harvest() {
        if (isReadyToHarvest()) {
            System.out.println(getName() + " has been harvested.");
            this.isHarvested = true; // Mark as harvested
        } else {
            System.out.println(getName() + " is not ready to harvest yet.");
        }
    }

    /**
     * Retrieves the purchase price of the item.
     * @return the price of the item.
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * Calculates the time elapsed since the item was planted.
     * @return the elapsed time in seconds. Returns 0 if not planted or already harvested.
     */
    private int getTimeElapsed() {
        if (plantedTimestamp == -1 || isHarvested) {
            return 0; // Not planted or growth stops after harvest
        }
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = currentTime - plantedTimestamp;
        return (int) (elapsedMillis / 1000); // Convert milliseconds to seconds
    }

    /**
     * Provides the growth progress of the item as a percentage.
     * @return the growth progress as an integer percentage (0-100).
     */
    public int getGrowthProgress() {
        int elapsed = getTimeElapsed();
        return (int) ((double) elapsed / this.growthTime * 100);
    }

    /**
     * Prints the current status of the item, including growth progress and readiness for harvest.
     * Useful for debugging or in-game status updates.
     */
    public void printStatus() {
        System.out.println("Name: " + getName());
        System.out.println("Growth Progress: " + getGrowthProgress() + "%");
        System.out.println("Ready to Harvest: " + isReadyToHarvest());
    }
}
