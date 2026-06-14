package student1;

import java.time.LocalDate;

public abstract class Crop {
    private static int counter = 0;
    private final String id;
    private String name;
    private CropFamily family;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private GrowthStage currentStage;
    private double optimalPHMin;
    private double optimalPHMax;
    private double optimalHumidityMin;
    private double optimalHumidityMax;

    public Crop(String string2, CropFamily cropFamily, LocalDate localDate, LocalDate localDate2, GrowthStage growthStage, double d, double d2, double d3, double d4) {
        this.id = "C" + String.format("%03d", ++Crop.counter);
        this.name = string2;
        this.family = cropFamily;
        this.plantingDate = localDate;
        this.expectedHarvestDate = localDate2;
        this.currentStage = growthStage;
        this.optimalPHMin = d;
        this.optimalPHMax = d2;
        this.optimalHumidityMin = d3;
        this.optimalHumidityMax = d4;
    }

    public void updateGrowthStage(GrowthStage growthStage) {
        this.currentStage = growthStage;
    }

    public boolean isSoilConditionOptimal(double d, double d2) {
        return d >= this.optimalPHMin && d <= this.optimalPHMax && d2 >= this.optimalHumidityMin && d2 <= this.optimalHumidityMax;
    }

    public void displayInfo() {
        System.out.println("Crop " + this.id + ": " + this.name);
        System.out.println("Family: " + String.valueOf((Object)this.family));
        System.out.println("Planting date: " + String.valueOf(this.plantingDate));
        System.out.println("Expected harvest date: " + String.valueOf(this.expectedHarvestDate));
        System.out.println("Current stage: " + String.valueOf((Object)this.currentStage));
        System.out.println("Optimal pH: " + this.optimalPHMin + " - " + this.optimalPHMax);
        System.out.println("Optimal humidity: " + this.optimalHumidityMin + " - " + this.optimalHumidityMax);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public CropFamily getFamily() {
        return this.family;
    }

    public LocalDate getPlantingDate() {
        return this.plantingDate;
    }

    public LocalDate getExpectedHarvestDate() {
        return this.expectedHarvestDate;
    }

    public GrowthStage getCurrentStage() {
        return this.currentStage;
    }
}
