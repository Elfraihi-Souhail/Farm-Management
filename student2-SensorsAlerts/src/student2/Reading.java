package student2;

import java.time.LocalDateTime;

public class Reading {
    private LocalDateTime dateTime;
    private double value;
    private String unit;
    private Sensor sensor;
    private ReadingStatus status;

    public Reading(LocalDateTime localDateTime, double d, String string, Sensor sensor) {
        this.dateTime = localDateTime;
        this.value = d;
        this.unit = string;
        this.sensor = sensor;
        if (this.sensor == null || !this.sensor.isOutOfThreshold(this.value)) {
            this.status = ReadingStatus.NORMAL;
        } else {
            double margin = Math.max(1.0, (this.sensor.getMaxThreshold() - this.sensor.getMinThreshold()) * 0.2);
            this.status = this.value < this.sensor.getMinThreshold() - margin || this.value > this.sensor.getMaxThreshold() + margin ? ReadingStatus.CRITICAL : ReadingStatus.WARNING;
        }
    }
    // This constructor is when the actual time is not given so we use the present time to create the reading
    public Reading(double d, String string, Sensor sensor) {
        this(LocalDateTime.now(), d, string, sensor);
    }

    public void evaluateStatus() {
        if (this.sensor == null || !this.sensor.isOutOfThreshold(this.value)) {
            this.status = ReadingStatus.NORMAL;
            return;
        }
        //we calculate the margin to decide which status is more accurate
        double d = Math.max(1.0, (this.sensor.getMaxThreshold() - this.sensor.getMinThreshold()) * 0.2);
        this.status = this.value < this.sensor.getMinThreshold() - d || this.value > this.sensor.getMaxThreshold() + d ? ReadingStatus.CRITICAL : ReadingStatus.WARNING;
    }

    public void displayReading() {
        boolean isPrefixUnit = !this.unit.isEmpty() && Character.isLetter(this.unit.charAt(0));
        String label = isPrefixUnit ? this.unit + " " + this.value : this.value + this.unit;
        System.out.printf("%-8s -> value: %-6s| unit: %-4s| level: %s%n", label, this.value, this.unit, this.status);
    }

    public void displayWithDateTime() {
        String formattedDate = this.dateTime.format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("  " + formattedDate + " | " + this.value + this.unit + " | " + this.status);
    }

    public String getColorIndicator() {
        if (this.status == ReadingStatus.CRITICAL) {
            return "RED";
        }
        if (this.status == ReadingStatus.WARNING) {
            return "YELLOW";
        }
        return "GREEN";
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public double getValue() {
        return this.value;
    }

    public String getUnit() {
        return this.unit;
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public ReadingStatus getStatus() {
        return this.status;
    }
}
