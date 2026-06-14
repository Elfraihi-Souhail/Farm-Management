package student1;

import common.SensorStatus;
import common.Suspendable;
import java.util.ArrayList;
import java.util.List;
import student2.Sensor;

public abstract class GeographicalZone
implements Suspendable {
    private static int counter = 0;
    private final String code;
    private String name;
    private ZoneStatus status;
    private final List<Sensor> sensors;
    private final List<ProductionRecord> productionHistory;
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    public GeographicalZone(String string2) {
        this.code = "Z" + String.format("%03d", ++GeographicalZone.counter);
        this.name = string2;
        this.status = ZoneStatus.ACTIVE;
        this.sensors = new ArrayList<Sensor>();
        this.productionHistory = new ArrayList<ProductionRecord>();
        this.minLatitude = -90.0;
        this.maxLatitude = 90.0;
        this.minLongitude = -180.0;
        this.maxLongitude = 180.0;
    }

    @Override
    public void suspend() {
        this.status = ZoneStatus.SUSPENDED;
        for (Sensor sensor : this.sensors) {
            sensor.suspend();
        }
    }

    @Override
    public void reactivate() {
        this.status = ZoneStatus.ACTIVE;
        for (Sensor sensor : this.sensors) {
            if (sensor.getStatus() != SensorStatus.SUSPENDED) continue;
            sensor.activate();
        }
    }

    public void addSensor(Sensor sensor) {
        if (sensor == null) {
            return;
        }
        sensor.setZone(this);
        this.sensors.add(sensor);
    }

    public void removeSensor(String string) {
        this.sensors.removeIf(sensor -> sensor.getCode().equals(string));
    }

    public void displaySensors() {
        System.out.println("Sensors for zone " + this.code + " - " + this.name);
        for (Sensor sensor : this.sensors) {
            System.out.println(sensor);
        }
    }

    public void addProductionRecord(ProductionRecord productionRecord) {
        if (productionRecord != null) {
            this.productionHistory.add(productionRecord);
        }
    }

    public boolean containsPosition(double d, double d2) {
        return d >= this.minLatitude && d <= this.maxLatitude && d2 >= this.minLongitude && d2 <= this.maxLongitude;
    }

    public void setGeographicalLimits(double d, double d2, double d3, double d4) {
        this.minLatitude = d;
        this.maxLatitude = d2;
        this.minLongitude = d3;
        this.maxLongitude = d4;
    }

    public abstract int getEntityCount();

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public ZoneStatus getStatus() {
        return this.status;
    }

    public List<Sensor> getSensors() {
        return this.sensors;
    }

    public List<ProductionRecord> getProductionHistory() {
        return this.productionHistory;
    }

    public double getMinLatitude() {
        return this.minLatitude;
    }

    public double getMaxLatitude() {
        return this.maxLatitude;
    }

    public double getMinLongitude() {
        return this.minLongitude;
    }

    public double getMaxLongitude() {
        return this.maxLongitude;
    }
}
