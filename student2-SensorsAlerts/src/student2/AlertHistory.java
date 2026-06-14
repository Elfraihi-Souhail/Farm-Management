package student2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import student1.GeographicalZone;

public class AlertHistory {
    private List<Alert> alerts = new ArrayList<>();

    public void addAlert(Alert alert) {
        if (alert != null) {
            this.alerts.add(alert);
        }
    }
    //Adds alerts from an exesting reading
    public void addAlertFromReading(Reading reading) {
        if (reading == null || reading.getStatus() == ReadingStatus.NORMAL) {
            return;
        }
        SeverityLevel severityLevel = reading.getStatus() == ReadingStatus.CRITICAL ? SeverityLevel.CRITICAL : SeverityLevel.WARNING;
        this.addAlert(new Alert(reading, severityLevel));
    }

    public void dismissAlert(int i){
        this.getAlerts().remove(i);
    }

    //Sorted with the severity level
    public void displayActiveAlerts() {
        ArrayList<Alert> arrayList = new ArrayList<>();
        for (Alert alert : this.alerts) {
            if (alert.getStatus() != AlertStatus.ACTIVE) continue;
            arrayList.add(alert);
        }
        arrayList.sort(Comparator.comparing(Alert::getSeverityLevel).reversed()); // Using reverse sort so that the critical alert would be first
        for (Alert alert : arrayList) {
            System.out.println(alert);
        }
    }

    public void displayAlertHistory() {
        for (Alert alert : this.alerts) {
            System.out.println(alert);
        }
    }
    // Alerts filtered by zone
    public List<Alert> filterByZone(String string) {
        ArrayList<Alert> arrayList = new ArrayList<>();
        for (Alert alert : this.alerts) {
            if (!this.belongsToZone(alert, string)) continue;
            arrayList.add(alert);
        }
        return arrayList;
    }

    // Alerts filtered by severity level
    public List<Alert> filterBySeverity(SeverityLevel severityLevel) {
        ArrayList<Alert> arrayList = new ArrayList<>();
        for (Alert alert : this.alerts) {
            if (alert.getSeverityLevel() != severityLevel) continue;
            arrayList.add(alert);
        }
        return arrayList;
    }

    //filter by the sensor type
    public List<Alert> filterBySensorType(String string) {
        ArrayList<Alert> arrayList = new ArrayList<>();
        for (Alert alert : this.alerts) {
            Sensor sensor = this.getSensor(alert);
            if (sensor == null || !sensor.getSensorTypeName().equalsIgnoreCase(string)) continue;
            arrayList.add(alert);
        }
        return arrayList;
    }

    // Filter by a period interval
    public List<Alert> filterByPeriod(LocalDateTime localDateTime, LocalDateTime localDateTime2) {
        ArrayList<Alert> arrayList = new ArrayList<>();
        for (Alert alert : this.alerts) {
            LocalDateTime localDateTime3 = alert.getCreatedAt();
            boolean bl2 = localDateTime == null || !localDateTime3.isBefore(localDateTime);
            boolean bl = localDateTime2 == null || !localDateTime3.isAfter(localDateTime2);
            if (!bl2 || !bl) continue;
            arrayList.add(alert);
        }
        return arrayList;
    }

    // Filter by all the above
    public List<Alert> filter(String string, String string2, SeverityLevel severityLevel, LocalDateTime localDateTime, LocalDateTime localDateTime2) {
        ArrayList<Alert> arrayList = new ArrayList<>();
        for (Alert alert : this.alerts) {

            boolean bl = string == null || this.belongsToZone(alert, string);
            boolean bl2 = string2 == null || this.matchesSensorType(alert, string2);
            boolean bl3 = severityLevel == null || alert.getSeverityLevel() == severityLevel;
            boolean bl4 = this.isInsidePeriod(alert, localDateTime, localDateTime2);

            if (!bl || !bl2 || !bl3 || !bl4) continue;
            arrayList.add(alert);
        }
        return arrayList;
    }

    // Check if an alert belongs to a zone
    private boolean belongsToZone(Alert alert, String string) {
        Sensor sensor = this.getSensor(alert);

        if (sensor == null) {
            return false;
        }
        GeographicalZone geographicalZone = sensor.getZone();
        return geographicalZone != null && geographicalZone.getCode().equals(string);
    }
    // this private methods are used for the boolean filtering
    private Sensor getSensor(Alert alert) {
        if (alert == null || alert.getReading() == null) {
            return null;
        }
        return alert.getReading().getSensor();
    }

    private boolean matchesSensorType(Alert alert, String string) {
        Sensor sensor = this.getSensor(alert);
        return sensor != null && sensor.getSensorTypeName().equalsIgnoreCase(string);
    }

    private boolean isInsidePeriod(Alert alert, LocalDateTime localDateTime, LocalDateTime localDateTime2) {
        LocalDateTime localDateTime3 = alert.getCreatedAt();
        boolean bl = localDateTime == null || !localDateTime3.isBefore(localDateTime);
        boolean bl2 = localDateTime2 == null || !localDateTime3.isAfter(localDateTime2);
        return bl && bl2;
    }

    // After getting a list by a filter we use this method to display this list
    public void displayFilteredAlerts(List<Alert> alerts, String filterDescription) {
        System.out.println("Alerts [" + filterDescription + "] — " + alerts.size() + " result(s):");
        if (alerts.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        for (Alert a : alerts) {
            System.out.println(a);
        }
    }

    // Get all the alerts as a list
    public List<Alert> getAlerts() {
        return this.alerts;
    }
}
