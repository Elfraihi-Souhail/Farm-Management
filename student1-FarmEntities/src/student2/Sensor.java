package student2;

import common.SensorStatus;
import common.Suspendable;
import student1.GeographicalZone;

public abstract class Sensor implements Suspendable {
    private SensorStatus status = SensorStatus.ACTIVE;

    public abstract String getCode();

    public SensorStatus getStatus() { return this.status; }

    public void activate() { this.status = SensorStatus.ACTIVE; }

    @Override
    public void suspend() { this.status = SensorStatus.SUSPENDED; }

    @Override
    public void reactivate() { this.status = SensorStatus.ACTIVE; }

    public void setZone(GeographicalZone zone) {}
}
