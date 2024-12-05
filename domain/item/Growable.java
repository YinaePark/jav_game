package domain.item;

public interface Growable {
    void plant();
    boolean isReadyToHarvest();
    void harvest();
}