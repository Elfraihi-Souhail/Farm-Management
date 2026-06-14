import farm.Farm;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import student1.Animal;
import student1.AnimalType;
import student1.AquacultureZone;
import student1.CropZone;
import student1.HealthStatus;
import student1.LivestockType;
import student1.LivestockZone;
import student2.Alert;
import student2.AlertHistory;
import student2.BiometricSensor;
import student2.BiometricSensorType;
import student2.EnvironmentalSensor;
import student2.EnvironmentalSensorType;
import student2.GPSCollar;
import student2.Reading;
import student2.ReadingStatus;
import student2.Sensor;
import student2.Sensors;
import student2.SeverityLevel;
import student2.SoilSensor;
import student2.SoilSensorType;
import student2.WaterSensor;
import student2.WaterSensorType;

public class Main {

    public static void main(String[] args) {

        // Step 1 : Setup 
        System.out.println("// Step 1 : Setup");

        CropZone cropZone = new CropZone("North Fields");
        LivestockZone livestockZone = new LivestockZone("LiveStock Tenes", LivestockType.CATTLE);
        AquacultureZone aquaZone  = new AquacultureZone("Aqua Tenes");

        Farm farm = new Farm("Tenes Farm");
        farm.addZone(cropZone);
        farm.addZone(livestockZone);
        farm.addZone(aquaZone);

        Animal cow = new Animal("Cow", AnimalType.RUMINANT, 4, 520.0, HealthStatus.HEALTHY);
        farm.assignAnimalToZone(livestockZone.getCode(), cow);

        EnvironmentalSensor tempSensor = new EnvironmentalSensor(cropZone, 15.0, 35.0, EnvironmentalSensorType.TEMPERATURE, "°C");
        SoilSensor phSensor = new SoilSensor(cropZone, 6.0, 7.5, SoilSensorType.PH, "pH");
        WaterSensor oxygenSensor = new WaterSensor(aquaZone, 5.0, 12.0, WaterSensorType.DISSOLVED_OXYGEN, "mg/L");
        BiometricSensor bodyTempSensor = new BiometricSensor(livestockZone, 37.5, 39.5, BiometricSensorType.BODY_TEMPERATURE, cow, "°C");

        cropZone.addSensor(tempSensor);
        cropZone.addSensor(phSensor);
        aquaZone.addSensor(oxygenSensor);
        livestockZone.addSensor(bodyTempSensor);

        System.out.println("// Created sensors");
        System.out.println(tempSensor);
        System.out.println(phSensor);
        System.out.println(oxygenSensor);
        System.out.println(bodyTempSensor);
        System.out.println();

        AlertHistory alertHistory = new AlertHistory();

        tempSensor.recordReading(22.0, LocalDateTime.of(2025, 5,  1,  8, 0), alertHistory);
        tempSensor.recordReading(28.0, LocalDateTime.of(2025, 5, 10, 10, 0), alertHistory);
        tempSensor.recordReading(24.0, LocalDateTime.of(2025,  6,  1,  9, 0), alertHistory);
        Reading readingTempSensor = tempSensor.recordReading(37.0, LocalDateTime.of(2025, 5, 20, 14, 0), null);
        tempSensor.recordReading(55.0, LocalDateTime.of(2025, 5, 20, 16, 0), alertHistory); // → ALT-001

        Reading readingpH1 = phSensor.recordReading(6.8, alertHistory);
        phSensor.recordReading(7.0, alertHistory);
        Reading readingpH2 = phSensor.recordReading(4.0, alertHistory); 

        alertHistory.addAlertFromReading(readingTempSensor);

        // Step 2 
        System.out.println("Step 2 : Reading history");
        System.out.println();

        System.out.println("Numerical readings of tempSensor");
        new Reading(25.0, "°C", tempSensor).displayReading();
        new Reading(37.0, "°C", tempSensor).displayReading();
        new Reading(55.0, "°C", tempSensor).displayReading();
        System.out.println();

        System.out.println("pH readings");
        readingpH1.displayReading();
        readingpH2.displayReading();
        System.out.println();

        GPSCollar gpsSensor = new GPSCollar(livestockZone, cow, 36.72, 3.18); // i created a GPS collar to display it in step 2
        System.out.println("GPS reading");
        gpsSensor.displayPosition();
        System.out.println();

        System.out.println("Filter : Filter a sensor's readings by ReadingLevel");
        System.out.println("// Filter : CRITICAL for tempSensor");
        List<Reading> criticalReadings = tempSensor.filterReadingsByLevel(ReadingStatus.CRITICAL); // Getting the list of critical readings using the filter method

        System.out.println("CRITICAL readings : " + criticalReadings.size());
        for (Reading r : criticalReadings) r.displayWithDateTime();
        System.out.println();

        System.out.println("// Filter : NORMAL for tempSensor");
        List<Reading> normalReadings = tempSensor.filterReadingsByLevel(ReadingStatus.NORMAL);

        System.out.println("NORMAL readings : " + normalReadings.size());
        for (Reading r : normalReadings) r.displayWithDateTime();

        System.out.println();

        // Step 3 : Filtered Alert History 
        System.out.println("Step 3 : Filtered Alert History");
        System.out.println("// Actif Alerts with CRITICAL first");
        alertHistory.displayActiveAlerts();

        System.out.println();

        System.out.println("// Filter : CRITICAL only");
        List<Alert> criticalAlerts = alertHistory.filterBySeverity(SeverityLevel.CRITICAL);
        System.out.println("CRITICAL alerts : " + criticalAlerts.size());
        for (Alert a : criticalAlerts) System.out.println(a);

        System.out.println();

        System.out.println("// Filter : zone " + cropZone.getCode() + " only"); // Getting the crop zone code Z001
        List<Alert> cropZoneAlerts = alertHistory.filterByZone(cropZone.getCode()); // Filtering using the zone crop zone to get its alerts
        System.out.println(cropZone.getCode() + " alerts : " + cropZoneAlerts.size());
        for (Alert a : cropZoneAlerts) System.out.println(a);

        System.out.println();

        System.out.println("// Filter : " + cropZone.getCode() + " + CRITICAL");
        //filter using the severity level on the crop zone
        List<Alert> cropZoneCriticalAlerts = alertHistory.filter(cropZone.getCode(), null, SeverityLevel.CRITICAL, null, null);
        
        System.out.println(cropZone.getCode() + " + CRITICAL : " + cropZoneCriticalAlerts.size());
        for (Alert a : cropZoneCriticalAlerts) System.out.println(a);

        System.out.println();

        // Step 4 : Sensor dashboard
        System.out.println("Step 4 : Sensor dashboard");
        System.out.println("// Dashboard Zone " + cropZone.getCode());
        Sensors.displayReadingsDashboardByZone(cropZone);
        System.out.println();

        System.out.println("Sensor summary :");
        List<Sensor> allSensors = new ArrayList<>();

        allSensors.addAll(cropZone.getSensors());
        allSensors.addAll(aquaZone.getSensors());

        Sensors.displaySensorSummaries(allSensors);
    }
}
