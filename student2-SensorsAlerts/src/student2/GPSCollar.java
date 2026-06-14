package student2;

import common.SensorStatus;
import student1.Animal;
import student1.GeographicalZone;

public class GPSCollar extends Sensor {
    private Animal animal;
    private double latitude;
    private double longitude;

    public GPSCollar(GeographicalZone zone, Animal animal, double lat, double lon) {
        super(zone, 0.0, 0.0);
        this.animal = animal;
        this.latitude = lat;
        this.longitude = lon;
    }

    public GPSCollar(GeographicalZone zone, double lat, double lon) {
        this(zone, null, lat, lon);
    }

    public void updatePosition(double lat, double lon) {
        if (this.getStatus() != SensorStatus.ACTIVE) return;
        this.latitude = lat;
        this.longitude = lon;
    }

    public void sendGPSReading(double lat, double lon) {
        if (this.getStatus() == SensorStatus.ACTIVE) {
            this.latitude = lat;
            this.longitude = lon;
        }
    }

    public boolean isAnimalOutsideZone() {
        GeographicalZone zone = this.getZone();
        if (zone == null && this.animal != null) {
            zone = this.animal.getAssignedZone();
        }
        return zone != null && !zone.containsPosition(this.latitude, this.longitude);
    }

    public void displayPosition() {
        System.out.printf("%-8s -> lat: %s | lon: %s%n", "GPS", this.latitude, this.longitude);
    }

    public void displayGPS() {
        System.out.println(this);
    }

    @Override public String getSensorTypeName() { return "GPS"; }
    @Override public String getName()           { return "GPSCollar"; }
    @Override public String getUnit()           { return "coords"; }
    @Override public Reading generateReading()  { return null; }

    @Override
    public String toString() {
        String zoneCode = this.getZone() == null ? "no-zone" : this.getZone().getCode();
        return String.format("[%s] %s | %-16s | %-19s | status: %s",
            this.getCode(), zoneCode, this.getSensorTypeName(), this.getName(), this.getStatus());
    }

    public Animal getAnimal()               { return this.animal; }
    public void setAnimal(Animal animal)    { this.animal = animal; }
    public double getLatitude()             { return this.latitude; }
    public double getLongitude()            { return this.longitude; }
}
