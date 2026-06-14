package student1;

import java.util.ArrayList;
import java.util.List;



public class CropZone
extends GeographicalZone {
    private final List<Crop> crops = new ArrayList<Crop>();

    public CropZone(String string2) {
        super(string2);
    }

    public void addCrop(Crop crop) {
        if (crop != null) {
            this.crops.add(crop);
        }
    }

    public void removeCrop(String string) {
        this.crops.removeIf(crop -> crop.getId().equals(string));
    }

    public void updateCropStage(String string, GrowthStage growthStage) {
        for (Crop crop : this.crops) {
            if (!crop.getId().equals(string)) continue;
            crop.updateGrowthStage(growthStage);
            return;
        }
    }

    public void generateCropReport() {
        System.out.println("Crop report for zone " + this.getCode() + " - " + this.getName());
        for (Crop crop : this.crops) {
            crop.displayInfo();
        }
    }

    @Override
    public int getEntityCount() {
        return this.crops.size();
    }

    public List<Crop> getCrops() {
        return this.crops;
    }
}
