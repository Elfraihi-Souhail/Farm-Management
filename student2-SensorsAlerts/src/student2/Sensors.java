package student2;

import common.SensorStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import student1.GeographicalZone;

public class Sensors {
    private Sensors() {
    }

    public static void addAndConfigureSensor(GeographicalZone geographicalZone, Sensor sensor, double d, double d2) {
        if (geographicalZone == null || sensor == null) {
            return;
        }
        sensor.updateThresholds(d, d2);
        geographicalZone.addSensor(sensor);
    }

    public static void displaySensor(Sensor sensor) {
        if (sensor != null) {
            System.out.println(sensor);
        }
    }

    public static void displayReadingsDashboardByZone(GeographicalZone geographicalZone) {
        if (geographicalZone == null) {
            return;
        }
        String separator = "═".repeat(70);
        String separator2 = "-".repeat(70);
        System.out.println(separator);
        System.out.println("Zone : " + geographicalZone.getCode()+ " | " + geographicalZone.getName()+ " | " + geographicalZone.getStatus());
        System.out.println(separator2);
        // getting the sensors of the zone
        for (Sensor sensor : geographicalZone.getSensors()) {
            //getting the readings of each sensor
            List<Reading> readings = sensor.getReadings();

            if (readings.isEmpty()) continue;

            Reading last = readings.get(readings.size() - 1);
            String indicator;
            // Using indicator to show the sevirity level of the reading
            switch (last.getStatus()) {
                case CRITICAL -> indicator = "[***CRITICAL***]";
                case WARNING  -> indicator = "[**WARNING**]";
                default -> indicator = "[*NORMAL*]";
            }
            System.out.printf("[%s] %-16s | %s | last: %-8s%s%n",sensor.getCode(),sensor.getSensorTypeName(),sensor.getStatus(),last.getValue() + last.getUnit(),indicator);
        }

        System.out.println(separator);
    }

    // displaying the summeries of each sensor by passing a list of sensors
    public static void displaySensorSummaries(List<Sensor> sensors) {
        for (Sensor s : sensors) {
            System.out.println(s.getReadingSummary());
        }
    }

    public static void displayReadingHistory(Sensor sensor, List<Reading> readings) {
        System.out.println("Reading history for [" + sensor.getCode() + "] "+ sensor.getSensorTypeName()+ " (" + readings.size() + " result(s)):");

        if (readings.isEmpty()) {
            System.out.println("  (no readings match the criteria)");
            return;
        }

        for (Reading r : readings) {
            r.displayReading();
        }
    }

    public static void displayStats(Sensor sensor) {
        sensor.computeStats().display();
    }

    public static List<Reading> getSensorReadingsHistory(Sensor sensor, LocalDateTime localDateTime, LocalDateTime localDateTime2) {
        if (sensor == null) {
            return Collections.emptyList();
        }
        return sensor.filterReadingsByPeriod(localDateTime, localDateTime2);
    }

    public static void changeSensorStatus(Sensor sensor, SensorStatus sensorStatus) {
        if (sensor == null || sensorStatus == null) {
            return;
        }
        switch (sensorStatus) {
            case ACTIVE -> sensor.activate();
            case SUSPENDED -> sensor.suspend();
            default -> sensor.markDefective();
        }
    }

    public static void displayEvolutionBySensor(Sensor sensor) {
        if (sensor == null) {
            return;
        }
        System.out.println("Evolution for sensor " + sensor.getCode());
        for (Reading reading : sensor.getReadings()) {
            System.out.println(String.valueOf(reading.getDateTime()) + " -> " + reading.getValue() + " " + reading.getUnit());
        }
    }

    public static void displayEvolutionByZone(GeographicalZone geographicalZone) {
        if (geographicalZone == null) {
            return;
        }
        for (Sensor sensor : geographicalZone.getSensors()) {
            Sensors.displayEvolutionBySensor(sensor);
        }
    }
}
