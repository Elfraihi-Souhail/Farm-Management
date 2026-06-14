package student2;

import student1.Animal;
import student1.GeographicalZone;

public class BiometricSensor
extends Sensor {
    private BiometricSensorType type;
    private Animal animal;
    private String unit;

    public BiometricSensor(GeographicalZone geographicalZone, double d, double d2, BiometricSensorType biometricSensorType, Animal animal, String string2) {
        super(geographicalZone, d, d2);
        this.type = biometricSensorType;
        this.animal = animal;
        this.unit = string2;
    }

    @Override
    public Reading generateReading() {
        return this.midpointReading(this.unit);
    }

    @Override
    public String getSensorTypeName() {
        return this.type.name();
    }

    public BiometricSensorType getType() {
        return this.type;
    }

    public Animal getAnimal() {
        return this.animal;
    }

    @Override
    public String getUnit() {
        return this.unit;
    }

    @Override
    public String getName() {
        return "BiometricSensor";
    }
}
