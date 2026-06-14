package student2;

import java.time.LocalDateTime;

public class Alert {
    private static int counter = 0;
    private final String id;
    private Reading reading;
    private SeverityLevel severityLevel;
    private AlertStatus status;
    private LocalDateTime createdAt;

    public Alert(Reading reading, SeverityLevel severityLevel) {
        this.id = "ALT-" + String.format("%03d", ++Alert.counter);
        this.reading = reading;
        this.severityLevel = severityLevel;
        this.status = AlertStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public void acknowledge() {
        this.status = AlertStatus.ACKNOWLEDGED;
    }

    public void delete() {
        this.status = AlertStatus.DELETED;
    }
    // I changed it to be empty until i need it
    public void displayAlert() {
        System.out.println();
    }

    public String getId() {
        return this.id;
    }

    public Reading getReading() {
        return this.reading;
    }

    public SeverityLevel getSeverityLevel() {
        return this.severityLevel;
    }

    public AlertStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public String toString() {
        Reading reading = this.getReading();
        Sensor sensor = reading.getSensor();
        return String.format("[%s] %s | %-11s | %-8s| %-8s | status: %s",
            this.id, sensor.getCode(), sensor.getSensorTypeName(),
            reading.getValue() + reading.getUnit(),
            this.severityLevel, this.status);
    }
}
