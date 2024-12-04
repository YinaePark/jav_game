package command;

import game.tile.FarmTile;

public class PlantCommand implements Command {
    private final FarmTile tile;
    private final String cropType;

    public PlantCommand(FarmTile tile, String cropType) {
        this.tile = tile;
        this.cropType = cropType;
    }

    @Override
    public void execute(String[] args) {
        if (tile.isTilled() && !tile.hasCrop()) {
            tile.plant(cropType);
            System.out.println("Successfully planted " + cropType);
        } else {
            System.out.println("Cannot plant here. Make sure the tile is tilled and empty.");
        }
    }
}