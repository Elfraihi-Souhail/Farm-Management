package student1;

import java.time.LocalDate;

public class Vegetable
extends Crop {
    private final VegetableType type;

    public Vegetable(VegetableType vegetableType, LocalDate localDate, LocalDate localDate2, GrowthStage growthStage) {
        super(vegetableType.name(), CropFamily.VEGETABLE, localDate, localDate2, growthStage, 6.0, 7.5, 45.0, 75.0);
        this.type = vegetableType;
    }

    public VegetableType getType() {
        return this.type;
    }
}
