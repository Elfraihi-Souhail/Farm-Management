package student2;

import student1.GeographicalZone;

public class WaterSensor extends Sensor {
    private WaterSensorType type;
    private String unit;

    public WaterSensor(GeographicalZone geographicalZone, double d, double d2, WaterSensorType waterSensorType, String string2) {
        super(geographicalZone, d, d2);
        this.type = waterSensorType;
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

    public WaterSensorType getType() {
        return this.type;
    }

    @Override
    public String getUnit() {
        return this.unit;
    }
    @Override
    public String getName() {
        return "WaterSensor";
    }
}
