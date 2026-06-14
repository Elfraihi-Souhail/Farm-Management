package gui;

import farm.Farm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import student1.AquacultureZone;
import student1.CropZone;
import student1.GeographicalZone;
import student1.LivestockZone;
import student2.Alert;
import student2.AlertHistory;
import student2.GPSCollar;
import student2.Sensor;

public class FarmModel {
    private static FarmModel instance;
    private Farm farm;

    private FarmModel() {
        this.farm = new Farm("Smart Farm");
    }

    public static FarmModel get() {
        if (instance == null) instance = new FarmModel();
        return instance;
    }

    public Farm getFarm() { return farm; }
    public AlertHistory getAlerts() { return farm.getAlertHistory(); }

    public void resetFarm(String name) {
        this.farm = new Farm(name == null || name.isBlank() ? "Smart Farm" : name);
    }

    public ObservableList<GeographicalZone> zones() {
        return FXCollections.observableArrayList(farm.getZones());
    }

    public ObservableList<CropZone> cropZones() {
        ObservableList<CropZone> out = FXCollections.observableArrayList();
        for (GeographicalZone z : farm.getZones())
            if (z instanceof CropZone cz) out.add(cz);
        return out;
    }

    public ObservableList<LivestockZone> livestockZones() {
        ObservableList<LivestockZone> out = FXCollections.observableArrayList();
        for (GeographicalZone z : farm.getZones())
            if (z instanceof LivestockZone lz) out.add(lz);
        return out;
    }

    public ObservableList<AquacultureZone> aquaZones() {
        ObservableList<AquacultureZone> out = FXCollections.observableArrayList();
        for (GeographicalZone z : farm.getZones())
            if (z instanceof AquacultureZone az) out.add(az);
        return out;
    }

    public ObservableList<Sensor> allSensors() {
        ObservableList<Sensor> out = FXCollections.observableArrayList();
        for (GeographicalZone z : farm.getZones()) {
            for (Sensor s : z.getSensors()) out.add(s);
        }
        return out;
    }

    public ObservableList<Sensor> sensorsExcludingGPS() {
        ObservableList<Sensor> out = FXCollections.observableArrayList();
        for (Sensor s : allSensors()) if (!(s instanceof GPSCollar)) out.add(s);
        return out;
    }

    public ObservableList<Alert> allAlerts() {
        return FXCollections.observableArrayList(farm.getAlertHistory().getAlerts());
    }
}
