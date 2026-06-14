package student1;

public class FeedingProgram {
    private String foodType;
    private double quantityPerMeal;
    private int mealsPerDay;

    public FeedingProgram(String string, double d, int n) {
        this.foodType = string;
        this.quantityPerMeal = d;
        this.mealsPerDay = n;
    }

    public void updateProgram(String string, double d, int n) {
        this.foodType = string;
        this.quantityPerMeal = d;
        this.mealsPerDay = n;
    }

    public void displayProgram() {
        System.out.println("Food: " + this.foodType);
        System.out.println("Quantity per meal: " + this.quantityPerMeal);
        System.out.println("Meals per day: " + this.mealsPerDay);
    }

    public String getFoodType() {
        return this.foodType;
    }

    public double getQuantityPerMeal() {
        return this.quantityPerMeal;
    }

    public int getMealsPerDay() {
        return this.mealsPerDay;
    }
}
