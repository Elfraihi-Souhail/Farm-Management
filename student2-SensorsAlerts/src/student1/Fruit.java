package student1;

import java.time.LocalDate;

public class Fruit
extends Crop {
    private final FruitType type;

    public Fruit(FruitType fruitType, LocalDate localDate, LocalDate localDate2, GrowthStage growthStage) {
        super(fruitType.name(), CropFamily.FRUIT, localDate, localDate2, growthStage, 5.5, 7.0, 50.0, 80.0);
        this.type = fruitType;
    }

    public FruitType getType() {
        return this.type;
    }
}
