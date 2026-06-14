package student2;

import student1.GeographicalZone;

public class EnvironmentalSensor
extends Sensor {
    private EnvironmentalSensorType type;
    private String unit;

    public EnvironmentalSensor(GeographicalZone geographicalZone, double d, double d2, EnvironmentalSensorType environmentalSensorType, String string2) {
        super(geographicalZone, d, d2);
        this.type = environmentalSensorType;
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

    public EnvironmentalSensorType getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return "EnvironmentalSensor";
    }

    @Override
    public String getUnit() {
        return this.unit;
    }
}
