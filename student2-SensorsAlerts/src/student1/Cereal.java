package student1;

import java.time.LocalDate;

public class Cereal
extends Crop {
    private final CerealType type;

    public Cereal(CerealType cerealType, LocalDate localDate, LocalDate localDate2, GrowthStage growthStage) {
        super(cerealType.name(), CropFamily.CEREAL, localDate, localDate2, growthStage, 5.5, 7.5, 40.0, 70.0);
        this.type = cerealType;
    }

    public CerealType getType() {
        return this.type;
    }
}
