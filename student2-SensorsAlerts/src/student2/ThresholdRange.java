package student2;

public class ThresholdRange {
    private String label;
    private double min;
    private double max;

    public ThresholdRange(String label, double min, double max) {
        this.label = label;
        this.min = min;
        this.max = max;
    }

    public void display() {
        System.out.printf("%-5s range : [%s - %s]%n", label, min, max);
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
}
