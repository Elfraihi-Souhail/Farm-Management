package student2;

import common.SensorStatus;
import common.Suspendable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import student1.GeographicalZone;

public abstract class Sensor implements Suspendable {
    private static int counter = 0;
    private final String code;
    private GeographicalZone zone;
    private SensorStatus status;
    private double minThreshold;
    private double maxThreshold;
    private List<Reading> readings;
    private int batteryLevel = 100;

    public Sensor(GeographicalZone geographicalZone, double d, double d2) {
        // we format the id for better user experience
        this.code = "S" + String.format("%03d", ++Sensor.counter); 
        this.zone = geographicalZone;
        this.status = SensorStatus.ACTIVE;
        this.minThreshold = d;
        this.maxThreshold = d2;
        this.readings = new ArrayList<>();
    }

    public void activate() {
        this.status = SensorStatus.ACTIVE;
    }

    @Override
    public void suspend() {
        this.status = SensorStatus.SUSPENDED;
    }

    @Override
    public void reactivate() {
        this.activate();
    }

    public void markDefective() {
        this.status = SensorStatus.DEFECTIVE;
    }

    public void markFaulty(){
        this.status = SensorStatus.FAULTY;
    }

    public void updateThresholds(double d, double d2) {
        this.minThreshold = d;
        this.maxThreshold = d2;
    }

    // Adding reading while checking if it is valid
    public void addReading(Reading reading) {
        if (reading == null || this.status != SensorStatus.ACTIVE) {
            return;
        }
        reading.setSensor(this);
        reading.evaluateStatus();
        this.readings.add(reading);
    }

    // Generates an alert while adding reading
    public void addReading(Reading reading, AlertHistory alertHistory) {
        this.addReading(reading);
        if (reading != null && alertHistory != null && this.readings.contains(reading)) {
            alertHistory.addAlertFromReading(reading);
        }
    }

    // Builds the reading , adds it then returns it back
    public Reading recordReading(double d, AlertHistory alertHistory) {
        Reading reading = new Reading(LocalDateTime.now(), d, this.getUnit(), this);
        this.addReading(reading, alertHistory);
        return reading;
    }

    public Reading recordReading(double value, LocalDateTime dateTime, AlertHistory alertHistory) {
        if (this.getStatus() == SensorStatus.SUSPENDED) return null;
        Reading reading = new Reading(dateTime, value, this.getUnit(), this);
        this.readings.add(reading);
        if (alertHistory != null && reading.getStatus() != ReadingStatus.NORMAL) {
            alertHistory.addAlertFromReading(reading);
        }
        return reading;
    }

    public Reading sendReading(double value, AlertHistory alertHistory) throws InsufficientEnergyException {
        if (this.status == SensorStatus.FAULTY) {
            throw new InsufficientEnergyException(
                "Sensor " + this.code + " is FAULTY due to insufficient energy.");
        }
        return this.recordReading(value, alertHistory);
    }

    public String getReadingSummary() {
        String lastStr;
        if (this.readings.isEmpty()) {
            lastStr = "none";
        } else {
            Reading last = this.readings.get(this.readings.size() - 1);
            String u = last.getUnit();
            boolean isPrefixUnit = !u.isEmpty() && Character.isLetter(u.charAt(0));
            String valueStr = isPrefixUnit ? "" + last.getValue() : last.getValue() + u;
            lastStr = valueStr + " (" + last.getStatus() + ")";
        }
        return String.format("[%s] %-16s | status: %s | readings: %s | last: %s",
            this.code, this.getSensorTypeName(), this.status, this.readings.size(), lastStr);
    }

    public int getBatteryLevel() { return this.batteryLevel; }

    public void consumeEnergy(int quantity) {
        this.batteryLevel = Math.max(0, this.batteryLevel - quantity);
        if (this.batteryLevel < 5) {
            this.markFaulty();
        }
    }

    public SensorStats computeStats() {
        List<Reading> readings = this.getReadings();
        if (readings.isEmpty()) {
            return new SensorStats(this.getCode(), this.getSensorTypeName(), 0, 0.0, 0.0, 0.0);
        }
        double min = readings.get(0).getValue();
        double max = readings.get(0).getValue();
        double sum = 0.0;
        for (Reading r : readings) {
            double v = r.getValue();
            if (v < min) min = v;
            if (v > max) max = v;
            sum += v;
        }
        double avg = sum / readings.size();
        return new SensorStats(this.getCode(), this.getSensorTypeName(),
                               readings.size(), min, max, avg);
    }

    public List<Reading> filterReadingsByLevel(ReadingStatus level) {
        List<Reading> result = new ArrayList<>();
        for (Reading r : this.getReadings()) {
            if (r.getStatus() == level) result.add(r);
        }
        return result;
    }

    public List<Reading> filterReadings(ReadingStatus level,LocalDateTime from,LocalDateTime to) {
        List<Reading> result = new ArrayList<>();
        for (Reading r : this.getReadings()) {
            boolean levelOk  = (level == null) || (r.getStatus() == level);
            boolean afterFrom = (from  == null) || !r.getDateTime().isBefore(from);
            boolean beforeTo  = (to    == null) || !r.getDateTime().isAfter(to);
            if (levelOk && afterFrom && beforeTo) result.add(r);
        }
        return result;
    }

    public boolean isOutOfThreshold(double d) {
        return d < this.minThreshold || d > this.maxThreshold;
    }

    // Gives the reading in given time interval
    public List<Reading> filterReadingsByPeriod(LocalDateTime localDateTime, LocalDateTime localDateTime2) {
        ArrayList<Reading> arrayList = new ArrayList<>();
        for (Reading reading : this.readings) {
            LocalDateTime localDateTime3 = reading.getDateTime();
            boolean bl2 = localDateTime == null || !localDateTime3.isBefore(localDateTime);
            boolean bl = localDateTime2 == null || !localDateTime3.isAfter(localDateTime2);
            if (!bl2 || !bl) continue;
            arrayList.add(reading);
        }
        return arrayList;
    }

    protected Reading midpointReading(String string) {
        double d = (this.minThreshold + this.maxThreshold) / 2.0;
        return new Reading(LocalDateTime.now(), d, string, this);
    }

    public abstract Reading generateReading();

    public abstract String getSensorTypeName();

    public abstract String getUnit();

    public  abstract  String getName();

    public String getCode() {
        return this.code;
    }

    public GeographicalZone getZone() {
        return this.zone;
    }

    public void setZone(GeographicalZone geographicalZone) {
        this.zone = geographicalZone;
    }

    public SensorStatus getStatus() {
        return this.status;
    }

    public double getMinThreshold() {
        return this.minThreshold;
    }

    public double getMaxThreshold() {
        return this.maxThreshold;
    }

    public List<Reading> getReadings() {
        return this.readings;
    }

    @Override
    public String toString() {
        String zoneCode = this.zone == null ? "no-zone" : this.zone.getCode();
        return String.format("[%s] %s | %-16s | %-19s | status: %s | range: [%s-%s]",
            this.code, zoneCode, this.getSensorTypeName(), this.getName(),
            this.status, this.minThreshold, this.maxThreshold);
    }

   /* public void displayReading(){
        String s;
        if(this.getReadings().getLast() == null){
            s = "last : none";
        }else {
            s = "last : " + this.getReadings().getLast().getValue() + "("+this.getReadings().getLast().getStatus()+")";
        }
        System.out.println(this.getCode()+" | " + this.getStatus() + " | readings: " + this.getReadings().size() + " |  " + s);
    } */
}
