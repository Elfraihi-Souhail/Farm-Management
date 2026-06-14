package farm;

import java.util.ArrayList;
import java.util.List;
import student1.Animal;
import student1.AquacultureSpecies;
import student1.AquacultureZone;
import student1.Crop;
import student1.CropZone;
import student1.GeographicalZone;
import student1.LivestockZone;
import student1.ProductionRecord;
import student2.AlertHistory;

public class Farm {
    private String name;
    private final List<GeographicalZone> zones;
    private final AlertHistory alertHistory;

    public Farm(String string) {
        this.name = string;
        this.zones = new ArrayList<>();
        this.alertHistory = new AlertHistory();
    }

    public void addZone(GeographicalZone geographicalZone) {
        if (geographicalZone != null) {
            this.zones.add(geographicalZone);
        }
    }

    public void updateZone(String string, GeographicalZone geographicalZone) {
        for (int i = 0; i < this.zones.size(); ++i) {
            if (!this.zones.get(i).getCode().equals(string)) continue;
            this.zones.set(i, geographicalZone);
            return;
        }
    }

    public void deactivateZone(String string) {
        GeographicalZone geographicalZone = this.findZoneByCode(string);
        if (geographicalZone != null) {
            geographicalZone.suspend();
        }
    }

    public void reactivateZone(String string) {
        GeographicalZone geographicalZone = this.findZoneByCode(string);
        if (geographicalZone != null) {
            geographicalZone.reactivate();
        }
    }

    public void displayZonesOverview() {
        System.out.println("Farm: " + this.name);
        for (GeographicalZone geographicalZone : this.zones) {
            System.out.println(geographicalZone.getCode() + " - " + geographicalZone.getName() + " | status=" + String.valueOf((Object)geographicalZone.getStatus()) + " | entities=" + geographicalZone.getEntityCount() + " | sensors=" + geographicalZone.getSensors().size());
        }
    }

    public void registerProduction(String string, ProductionRecord productionRecord) {
        GeographicalZone geographicalZone = this.findZoneByCode(string);
        if (geographicalZone != null) {
            geographicalZone.addProductionRecord(productionRecord);
        }
    }

    public void assignCropToZone(String string, Crop crop) {
        GeographicalZone geographicalZone = this.findZoneByCode(string);
        if (geographicalZone instanceof CropZone) {
            ((CropZone)geographicalZone).addCrop(crop);
        }
    }

    public void assignAnimalToZone(String string, Animal animal) {
        GeographicalZone geographicalZone = this.findZoneByCode(string);
        if (geographicalZone instanceof LivestockZone) {
            ((LivestockZone)geographicalZone).addAnimal(animal);
        }
    }

    public void assignSpeciesToZone(String string, AquacultureSpecies aquacultureSpecies) {
        GeographicalZone geographicalZone = this.findZoneByCode(string);
        if (geographicalZone instanceof AquacultureZone) {
            ((AquacultureZone)geographicalZone).addSpecies(aquacultureSpecies);
        }
    }

    public GeographicalZone findZoneByCode(String string) {
        for (GeographicalZone geographicalZone : this.zones) {
            if (!geographicalZone.getCode().equals(string)) continue;
            return geographicalZone;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public List<GeographicalZone> getZones() {
        return this.zones;
    }

    public AlertHistory getAlertHistory() {
        return this.alertHistory;
    }
}
