package student2;

public class SensorStats {
    private String sensorCode;
    private String sensorType;
    private int count;
    private double min;
    private double max;
    private double average;

    public SensorStats(String sensorCode, String sensorType,
                       int count, double min, double max, double average) {
        this.sensorCode = sensorCode;
        this.sensorType = sensorType;
        this.count = count;
        this.min = min;
        this.max = max;
        this.average = average;
    }

    public void display() {
        System.out.println("[" + this.sensorCode + "] " + this.sensorType
            + " | count: " + this.count
            + " | min: " + this.min
            + " | max: " + this.max
            + " | avg: " + this.average);
    }

    public String getSensorCode() { return sensorCode; }
    public String getSensorType() { return sensorType; }
    public int getCount() { return count; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getAverage() { return average; }
}
