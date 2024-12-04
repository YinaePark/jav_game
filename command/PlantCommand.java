package command;

public class PlantCommand implements Command {
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify a crop to plant.");
            return;
        }
        String crop = args[0];
        System.out.println("Planting " + crop + "...");
    }
}
