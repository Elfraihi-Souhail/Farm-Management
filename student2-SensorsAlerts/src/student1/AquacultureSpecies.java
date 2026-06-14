package student1;


public class AquacultureSpecies {
    private String speciesName;
    private AquacultureType type;
    private int numberOfAnimals;
    private FeedingProgram feedingProgram;

    public AquacultureSpecies(String string, int n) {
        this(string, AquacultureType.FISH, n);
    }

    public AquacultureSpecies(String string, AquacultureType aquacultureType, int n) {
        this.speciesName = string;
        this.type = aquacultureType;
        this.numberOfAnimals = n;
    }

    public void updateNumber(int n) {
        this.numberOfAnimals = n;
    }

    public void setFeedingProgram(FeedingProgram feedingProgram) {
        this.feedingProgram = feedingProgram;
    }

    public void displayInfo() {
        System.out.println("Aquaculture species: " + this.speciesName);
        System.out.println("Type: " + String.valueOf((Object)this.type));
        System.out.println("Number of animals: " + this.numberOfAnimals);
        if (this.feedingProgram != null) {
            this.feedingProgram.displayProgram();
        }
    }

    public String getSpeciesName() {
        return this.speciesName;
    }

    public int getNumberOfAnimals() {
        return this.numberOfAnimals;
    }

    public AquacultureType getType() {
        return this.type;
    }

    public FeedingProgram getFeedingProgram() {
        return this.feedingProgram;
    }
}
