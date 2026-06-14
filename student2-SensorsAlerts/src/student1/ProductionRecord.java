package student1;

import java.time.LocalDate;

public class ProductionRecord {
    private LocalDate date;
    private ProductionType type;
    private double value;
    private UnitType unit;

    public ProductionRecord(LocalDate localDate, ProductionType productionType, double d, UnitType unitType) {
        this.date = localDate;
        this.type = productionType;
        this.value = d;
        this.unit = unitType;
    }

    public void displayRecord() {
        System.out.println(String.valueOf(this.date) + " - " + String.valueOf((Object)this.type) + ": " + this.value + " " + String.valueOf((Object)this.unit));
    }

    public LocalDate getDate() {
        return this.date;
    }

    public ProductionType getType() {
        return this.type;
    }

    public double getValue() {
        return this.value;
    }

    public UnitType getUnit() {
        return this.unit;
    }
}
