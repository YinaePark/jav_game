package command;

import game.tile.FarmTile;

public class TillCommand implements Command {
    private final FarmTile tile;

    public TillCommand(FarmTile tile) {
        this.tile = tile;
    }

    @Override
    public void execute(String[] args) {
        tile.till();
        System.out.println("Tilled the soil.");
    }
}