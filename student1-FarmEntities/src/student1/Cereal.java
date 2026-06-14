package student1;

import java.time.LocalDate;
import student1.CerealType;
import student1.Crop;
import student1.CropFamily;
import student1.GrowthStage;

public class Cereal
extends Crop {
    private final CerealType type;

    public Cereal(String string, CerealType cerealType, LocalDate localDate, LocalDate localDate2, GrowthStage growthStage) {
        super(string, cerealType.name(), CropFamily.CEREAL, localDate, localDate2, growthStage, 5.5, 7.5, 40.0, 70.0);
        this.type = cerealType;
    }

    public CerealType getType() {
        return this.type;
    }
}
