package student1;

import java.util.ArrayList;
import java.util.List;

public class AquacultureZone
extends GeographicalZone {
    private final List<AquacultureSpecies> species = new ArrayList<AquacultureSpecies>();

    public AquacultureZone(String string2) {
        super(string2);
    }

    public void addSpecies(AquacultureSpecies aquacultureSpecies) {
        if (aquacultureSpecies != null) {
            this.species.add(aquacultureSpecies);
        }
    }

    public void removeSpecies(String string) {
        this.species.removeIf(aquacultureSpecies -> aquacultureSpecies.getSpeciesName().equals(string));
    }

    public void displaySpecies() {
        System.out.println("Aquaculture species for zone " + this.getCode() + " - " + this.getName());
        for (AquacultureSpecies aquacultureSpecies : this.species) {
            aquacultureSpecies.displayInfo();
        }
    }

    @Override
    public int getEntityCount() {
        int n = 0;
        for (AquacultureSpecies aquacultureSpecies : this.species) {
            n += aquacultureSpecies.getNumberOfAnimals();
        }
        return n;
    }

    public List<AquacultureSpecies> getSpecies() {
        return this.species;
    }
}
