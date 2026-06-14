package student1;

import java.time.LocalDate;
import student1.HealthStatus;

public class HealthEvent {
    private LocalDate date;
    private String description;
    private HealthStatus status;

    public HealthEvent(LocalDate localDate, String string, HealthStatus healthStatus) {
        this.date = localDate;
        this.description = string;
        this.status = healthStatus;
    }

    public void displayEvent() {
        System.out.println(String.valueOf(this.date) + " - " + String.valueOf((Object)this.status) + ": " + this.description);
    }

    public LocalDate getDate() {
        return this.date;
    }

    public String getDescription() {
        return this.description;
    }

    public HealthStatus getStatus() {
        return this.status;
    }
}
