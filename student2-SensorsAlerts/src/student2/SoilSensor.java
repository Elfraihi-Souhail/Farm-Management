package student2;

import student1.GeographicalZone;

public class SoilSensor
extends Sensor {
    private SoilSensorType type;
    private String unit;

    public SoilSensor(GeographicalZone geographicalZone, double d, double d2, SoilSensorType soilSensorType, String string2) {
        super(geographicalZone, d, d2);
        this.type = soilSensorType;
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

    public SoilSensorType getType() {
        return this.type;
    }

    @Override
    public String getUnit() {
        return this.unit;
    }

    @Override
    public String getName() {
        return "SoilSensor";
    }
}
