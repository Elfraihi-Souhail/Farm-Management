package student1;

import java.time.LocalDate;
import student1.Crop;
import student1.CropFamily;
import student1.GrowthStage;
import student1.VegetableType;

public class Vegetable
extends Crop {
    private final VegetableType type;

    public Vegetable(String string, VegetableType vegetableType, LocalDate localDate, LocalDate localDate2, GrowthStage growthStage) {
        super(string, vegetableType.name(), CropFamily.VEGETABLE, localDate, localDate2, growthStage, 6.0, 7.5, 45.0, 75.0);
        this.type = vegetableType;
    }

    public VegetableType getType() {
        return this.type;
    }
}
