package student1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import student1.AnimalType;
import student1.GeographicalZone;
import student1.HealthEvent;
import student1.HealthStatus;
import student2.GPSCollar;

public class Animal {
    private final String id;
    private String species;
    private AnimalType type;
    private int age;
    private double weight;
    private HealthStatus healthStatus;
    private final List<HealthEvent> healthEvents;
    private GPSCollar gpsCollar;
    private GeographicalZone assignedZone;

    public Animal(String string, String string2, AnimalType animalType, int n, double d, HealthStatus healthStatus) {
        this.id = string;
        this.species = string2;
        this.type = animalType;
        this.age = n;
        this.weight = d;
        this.healthStatus = healthStatus;
        this.healthEvents = new ArrayList<HealthEvent>();
    }

    public void updateWeight(double d) {
        this.weight = d;
    }

    public void recordWeightEvolution(double d) {
        double d2 = this.weight;
        this.weight = d;
        this.addHealthEvent(new HealthEvent(LocalDate.now(), "Weight changed from " + d2 + " to " + d, this.healthStatus));
    }

    public void updateHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public void addHealthEvent(HealthEvent healthEvent) {
        if (healthEvent != null) {
            this.healthEvents.add(healthEvent);
            this.healthStatus = healthEvent.getStatus();
        }
    }

    public void assignGPSCollar(GPSCollar gPSCollar) {
        this.gpsCollar = gPSCollar;
        if (gPSCollar != null) {
            gPSCollar.setAnimal(this);
            gPSCollar.setZone(this.assignedZone);
        }
    }

    public void displayInfo() {
        System.out.println("Animal " + this.id + ": " + this.species);
        System.out.println("Type: " + String.valueOf((Object)this.type));
        System.out.println("Age: " + this.age);
        System.out.println("Weight: " + this.weight);
        System.out.println("Health: " + String.valueOf((Object)this.healthStatus));
        if (this.gpsCollar != null) {
            this.gpsCollar.displayPosition();
        }
    }

    public String getId() {
        return this.id;
    }

    public String getSpecies() {
        return this.species;
    }

    public AnimalType getType() {
        return this.type;
    }

    public int getAge() {
        return this.age;
    }

    public double getWeight() {
        return this.weight;
    }

    public HealthStatus getHealthStatus() {
        return this.healthStatus;
    }

    public List<HealthEvent> getHealthEvents() {
        return this.healthEvents;
    }

    public GPSCollar getGpsCollar() {
        return this.gpsCollar;
    }

    public GeographicalZone getAssignedZone() {
        return this.assignedZone;
    }

    public void setAssignedZone(GeographicalZone geographicalZone) {
        this.assignedZone = geographicalZone;
        if (this.gpsCollar != null) {
            this.gpsCollar.setZone(geographicalZone);
        }
    }
}
