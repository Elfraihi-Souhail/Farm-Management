package student1;

import java.util.ArrayList;
import java.util.List;

public class LivestockZone
extends GeographicalZone {
    private LivestockType livestockType;
    private final List<Animal> animals;
    private FeedingProgram feedingProgram;

    public LivestockZone(String string2, LivestockType livestockType) {
        super(string2);
        this.livestockType = livestockType;
        this.animals = new ArrayList<Animal>();
    }

    public void addAnimal(Animal animal) {
        if (animal != null) {
            animal.setAssignedZone(this);
            this.animals.add(animal);
        }
    }

    public void removeAnimal(String string) {
        this.animals.removeIf(animal -> animal.getId().equals(string));
    }

    public void setFeedingProgram(FeedingProgram feedingProgram) {
        this.feedingProgram = feedingProgram;
    }

    public void displayFeedingProgram() {
        if (this.feedingProgram == null) {
            System.out.println("No feeding program defined for zone " + this.getCode());
            return;
        }
        this.feedingProgram.displayProgram();
    }

    public void displayAnimals() {
        System.out.println("Animals for zone " + this.getCode() + " - " + this.getName());
        for (Animal animal : this.animals) {
            animal.displayInfo();
        }
    }

    @Override
    public int getEntityCount() {
        return this.animals.size();
    }

    public LivestockType getLivestockType() {
        return this.livestockType;
    }

    public List<Animal> getAnimals() {
        return this.animals;
    }

    public FeedingProgram getFeedingProgram() {
        return this.feedingProgram;
    }
}
