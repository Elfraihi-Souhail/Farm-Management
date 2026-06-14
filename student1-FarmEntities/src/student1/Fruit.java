package student1;

import java.time.LocalDate;
import student1.Crop;
import student1.CropFamily;
import student1.FruitType;
import student1.GrowthStage;

public class Fruit
extends Crop {
    private final FruitType type;

    public Fruit(String string, FruitType fruitType, LocalDate localDate, LocalDate localDate2, GrowthStage growthStage) {
        super(string, fruitType.name(), CropFamily.FRUIT, localDate, localDate2, growthStage, 5.5, 7.0, 50.0, 80.0);
        this.type = fruitType;
    }

    public FruitType getType() {
        return this.type;
    }
}
