package domain.item;

public interface Growable {
    void plant(); // 재료를 심음
    boolean isReadyToHarvest(); // 수확 가능 여부
    void harvest(); // 수확
}