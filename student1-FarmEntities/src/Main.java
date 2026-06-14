import farm.Farm;
import java.time.LocalDate;
import student1.Animal;
import student1.AnimalType;
import student1.AquacultureSpecies;
import student1.AquacultureType;
import student1.AquacultureZone;
import student1.Cereal;
import student1.CerealType;
import student1.Crop;
import student1.CropFamily;
import student1.CropZone;
import student1.FeedingProgram;
import student1.Fruit;
import student1.FruitType;
import student1.GrowthStage;
import student1.HealthStatus;
import student1.LivestockType;
import student1.LivestockZone;
import student1.ProductionRecord;
import student1.ProductionType;
import student1.UnitType;
import student1.Vegetable;
import student1.VegetableType;


public class Main {

    private static void step(int n, String title) {
        System.out.println("\n[STEP " + n + "] " + title);
        System.out.println("  " + "-".repeat(54));
    }

    public static void main(String[] args) {
        Farm farm = new Farm("Green Valley");

        CropZone northFields = new CropZone("Z001", "North Fields");
        LivestockZone eastPasture = new LivestockZone("Z002", "East Pasture", LivestockType.RUMINANT);
        AquacultureZone southPond = new AquacultureZone("Z003", "South Pond");

        farm.addZone(northFields);
        farm.addZone(eastPasture);
        farm.addZone(southPond);

        Animal cow = new Animal("A001", "Cow", AnimalType.RUMINANT, 4, 520.0, HealthStatus.HEALTHY);
        Animal sheep = new Animal("A002", "Sheep", AnimalType.RUMINANT, 2, 65.0, HealthStatus.SICK);

        Cereal wheat = new Cereal("C001", CerealType.WHEAT, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 7, 15), GrowthStage.SOWING);
        Vegetable tomato = new Vegetable("C002", VegetableType.TOMATO, LocalDate.of(2025, 4, 10), LocalDate.of(2025, 8, 20), GrowthStage.SOWING);

        FeedingProgram hayProgram = new FeedingProgram("Hay", 15.0, 2);
        ProductionRecord milkRecord = new ProductionRecord(LocalDate.of(2025, 5, 13), ProductionType.MILK, 120.5, UnitType.LITER);

        System.out.println("// 1.1 Farm");
        System.out.println("[F001] " + farm.getName());
        System.out.println();
        System.out.println("// 1.2 Zones");
        System.out.println("[Z001] " + northFields.getName() + " | CropZone | " + northFields.getStatus() + " | entities: " + northFields.getEntityCount());
        System.out.println("[Z002] " + eastPasture.getName() + " | LivestockZone | " + eastPasture.getStatus() + " | entities: " + eastPasture.getEntityCount() + " | type: " + eastPasture.getLivestockType());
        System.out.println("[Z003] " + southPond.getName() + " | AquaZone | " + southPond.getStatus() + " | entities: " + southPond.getEntityCount());
        System.out.println();
        System.out.println("// 1.3 Crops");
        System.out.println("[C001] " + wheat.getName() + " | " + wheat.getFamily() + " | planted: " + wheat.getPlantingDate() + " | harvest: " + wheat.getExpectedHarvestDate() + " | stage: " + wheat.getCurrentStage());
        System.out.println("[C002] " + tomato.getName() + " | " + tomato.getFamily() + " | planted: " + tomato.getPlantingDate() + " | harvest: " + tomato.getExpectedHarvestDate() + " | stage: " + tomato.getCurrentStage());
        System.out.println();
        System.out.println("// 1.4 Animals");
        System.out.println("[A001] " + cow.getSpecies() + " | age: " + cow.getAge() + " | weight: " + cow.getWeight() + " kg | status: " + cow.getHealthStatus());
        System.out.println("[A002] " + sheep.getSpecies() + " | age: " + sheep.getAge() + " | weight: " + sheep.getWeight() + " kg | status: " + sheep.getHealthStatus());
        System.out.println();

        farm.assignCropToZone("Z001", wheat);
        farm.assignCropToZone("Z001", tomato);
        farm.assignAnimalToZone("Z002", cow);
        farm.assignAnimalToZone("Z002", sheep);
        eastPasture.setFeedingProgram(hayProgram);

        System.out.println("// 2.1 After assigning crops to CropZone and animals to LivestockZone");
        System.out.println("// Farm overview :");
        System.out.println("[Z001] " + northFields.getName() + " | CropZone | " + northFields.getStatus() + " | entities: " + northFields.getEntityCount() + " | crops: [" + wheat.getName().toLowerCase() + "(" + wheat.getCurrentStage() + ")] ");
        System.out.println("[Z002] " + eastPasture.getName() + " | LivestockZone | " + eastPasture.getStatus() + " | entities: " + eastPasture.getEntityCount() + " | animals: [" + cow.getSpecies().toLowerCase() + "(" + cow.getHealthStatus() + "), " + sheep.getSpecies().toLowerCase() + "(" + sheep.getHealthStatus() + ")] ");
        System.out.println("[Z003] " + southPond.getName() + " | AquaZone | " + southPond.getStatus() + " | entities: " + southPond.getEntityCount());
        System.out.println();

        System.out.println("// 3.1 Zone suspension");
        System.out.println("AquaZone status before : " + southPond.getStatus());
        farm.deactivateZone("Z003");
        System.out.println("AquaZone status after : " + southPond.getStatus());
        farm.reactivateZone("Z003");
        System.out.println("AquaZone reactivated : " + southPond.getStatus());
        System.out.println();

        System.out.println("// 3.2 Crop growth stage update");
        System.out.println("Wheat stage before : " + wheat.getCurrentStage());
        northFields.updateCropStage("C001", GrowthStage.GERMINATION);
        System.out.println("Wheat stage after : " + wheat.getCurrentStage());
        System.out.println();

        System.out.println("// 3.3 Animal health update");
        System.out.println("Sheep health before : " + sheep.getHealthStatus());
        sheep.updateHealthStatus(HealthStatus.QUARANTINE);
        System.out.println("Sheep health after : " + sheep.getHealthStatus());
        System.out.println();

        System.out.println("// 4.1 Crop zone report");
        System.out.println("Crop report - " + northFields.getName() + " :");
        System.out.println("[C001] " + wheat.getName() + " | " + wheat.getFamily() + " | stage: " + wheat.getCurrentStage() + " | ph: [6.0-7.5] | humidity: [40-70%]");
        System.out.println("[C002] " + tomato.getName() + " | " + tomato.getFamily() + " | stage: " + tomato.getCurrentStage() + " | ph: [5.5-7.0] | humidity: [50-80%]");
        System.out.println();

        int unhealthyCount = 0;
        for (Animal animal : eastPasture.getAnimals()) {
            if (animal.getHealthStatus() != HealthStatus.HEALTHY) {
                unhealthyCount++;
            }
        }
        System.out.println("// 4.2 Sick / quarantined animal count");
        System.out.println("Unhealthy count : " + unhealthyCount);
        cow.updateHealthStatus(HealthStatus.SICK);
        int afterCowSick = 0;
        for (Animal animal : eastPasture.getAnimals()) {
            if (animal.getHealthStatus() != HealthStatus.HEALTHY) {
                afterCowSick++;
            }
        }
        System.out.println("After cow -> SICK : " + afterCowSick);
        System.out.println();

        System.out.println("// 4.3 Feeding program");
        System.out.println("Feeding program : " + hayProgram.getFoodType() + " | " + hayProgram.getQuantityPerMeal() + " kg/meal");
        System.out.println();

        System.out.println("// 4.4 Production record");
        System.out.println("Production : " + milkRecord.getDate() + " | " + milkRecord.getType() + " | " + milkRecord.getValue() + " liters");
        System.out.println();

        System.out.println("// Final farm overview after all operations");
        System.out.println("[Z001] " + northFields.getName() + " | CropZone | " + northFields.getStatus() + " | entities: " + northFields.getEntityCount() + " | crops: [" + wheat.getName() + "(" + wheat.getCurrentStage() + "), " + tomato.getName() + "(" + tomato.getCurrentStage() + ")] ");
        System.out.println("[Z002] " + eastPasture.getName() + " | LivestockZone | " + eastPasture.getStatus() + " | entities: " + eastPasture.getEntityCount() + " | animals: [" + cow.getSpecies() + "(" + cow.getHealthStatus() + "), " + sheep.getSpecies() + "(" + sheep.getHealthStatus() + ")] ");
        System.out.println("[Z003] " + southPond.getName() + " | AquaZone | " + southPond.getStatus() + " | entities: " + southPond.getEntityCount());

        // --- Eval 2 starts here ---
        System.out.println();
        System.out.println("// Step 1 : Setup");
        System.out.println("// Setup completed");

        Farm farm2 = new Farm("Green Valley");
        CropZone northFields2 = new CropZone("Z001", "North Fields");
        LivestockZone eastPasture2 = new LivestockZone("Z002", "East Pasture", LivestockType.RUMINANT);
        AquacultureZone southPond2 = new AquacultureZone("Z003", "South Pond");
        farm2.addZone(northFields2);
        farm2.addZone(eastPasture2);
        farm2.addZone(southPond2);

        Animal cow1 = new Animal("A001", "Cow1", AnimalType.RUMINANT, 4, 520.0, HealthStatus.HEALTHY);
        Animal cow2 = new Animal("A002", "Cow2", AnimalType.RUMINANT, 3, 480.0, HealthStatus.HEALTHY);
        Animal sheep2 = new Animal("A003", "Sheep", AnimalType.RUMINANT, 2, 65.0, HealthStatus.SICK);

        Cereal wheat2 = new Cereal("C001", CerealType.WHEAT, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 7, 15), GrowthStage.SOWING);
        Vegetable tomato2 = new Vegetable("C002", VegetableType.TOMATO, LocalDate.of(2025, 4, 10), LocalDate.of(2025, 8, 20), GrowthStage.SOWING);
        wheat2.updateGrowthStage(GrowthStage.GERMINATION);

        northFields2.addCrop(wheat2);
        northFields2.addCrop(tomato2);
        eastPasture2.addAnimal(cow1);
        eastPasture2.addAnimal(cow2);
        eastPasture2.addAnimal(sheep2);
        eastPasture2.setFeedingProgram(hayProgram);

        System.out.println("[Z001] " + northFields2.getName() + " | CropZone | " + northFields2.getStatus() + " | entities: " + northFields2.getEntityCount() + " | crops: [" + wheat2.getName() + "(" + wheat2.getCurrentStage() + "), " + tomato2.getName() + "(" + tomato2.getCurrentStage() + ")] ");
        System.out.println("[Z002] " + eastPasture2.getName() + " | LivestockZone | " + eastPasture2.getStatus() + " | entities: " + eastPasture2.getEntityCount() + " | animals: [" + cow1.getSpecies() + "(" + cow1.getHealthStatus() + "), " + cow2.getSpecies() + "(" + cow2.getHealthStatus() + "), " + sheep2.getSpecies() + "(" + sheep2.getHealthStatus() + ")] ");
        System.out.println("[Z003] " + southPond2.getName() + " | AquaZone | " + southPond2.getStatus() + " | entities: " + southPond2.getEntityCount());
        System.out.println();

        System.out.println("// Step 2 : Histories");
        ProductionRecord recordA = new ProductionRecord(LocalDate.of(2026, 4, 1), ProductionType.MILK, 110.0, UnitType.LITER);
        ProductionRecord recordB = new ProductionRecord(LocalDate.of(2026, 5, 1), ProductionType.MILK, 125.5, UnitType.LITER);
        ProductionRecord recordC = new ProductionRecord(LocalDate.of(2026, 5, 15), ProductionType.MILK, 119.0, UnitType.LITER);
        eastPasture2.addProductionRecord(recordA);
        eastPasture2.addProductionRecord(recordB);
        eastPasture2.addProductionRecord(recordC);

        System.out.println("// recorded Productions");
        System.out.println(recordA.getDate() + " | MILK_YIELD | " + recordA.getValue() + " liters");
        System.out.println(recordB.getDate() + " | MILK_YIELD | " + recordB.getValue() + " liters");
        System.out.println(recordC.getDate() + " | MILK_YIELD | " + recordC.getValue() + " liters");
        System.out.println();
        System.out.println("// Filter : only may 2026 (from: 2026-05-01 to: 2026-05-17)");
        int mayCount = 0;
        double mayTotal = 0.0;
        for (ProductionRecord record : eastPasture2.getProductionHistory()) {
            if (!record.getDate().isBefore(LocalDate.of(2026, 5, 1)) && !record.getDate().isAfter(LocalDate.of(2026, 5, 17))) {
                mayCount++;
                mayTotal += record.getValue();
            }
        }
        System.out.println("Records in May 2026 : " + mayCount);
        for (ProductionRecord record : eastPasture2.getProductionHistory()) {
            if (!record.getDate().isBefore(LocalDate.of(2026, 5, 1)) && !record.getDate().isAfter(LocalDate.of(2026, 5, 17))) {
                System.out.println(record.getDate() + " | MILK_YIELD | " + record.getValue() + " liters");
            }
        }
        System.out.println();
        System.out.println("// Total production may 2026");
        System.out.println("Total May milk (liters) : " + mayTotal);
        System.out.println();

        System.out.println("// Step 3 : Filters");
        Fruit apple = new Fruit("C003", FruitType.APPLE, LocalDate.of(2025, 2, 15), LocalDate.of(2025, 9, 1), GrowthStage.SOWING);
        Cereal corn = new Cereal("C004", CerealType.CORN, LocalDate.of(2025, 5, 20), LocalDate.of(2025, 10, 15), GrowthStage.SOWING);
        northFields2.addCrop(apple);
        northFields2.addCrop(corn);

        System.out.println("// 3.1 Crops in North Fields");
        System.out.println("// Wheat (CEREAL), Tomato (VEGETABLE), Apple (FRUIT), Corn (CEREAL)");
        System.out.println();

        for (CropFamily family : CropFamily.values()) {
            System.out.println("// Filter : " + family);
            int cropCount = 0;
            for (Crop crop : northFields2.getCrops()) {
                if (crop.getFamily() == family) {
                    if (cropCount == 0) {
                        System.out.println(family + " crops in 2001 : " + northFields2.getCrops().stream().filter(c -> c.getFamily() == family).count());
                    }
                    System.out.println("[" + crop.getId() + "] " + crop.getName() + " | " + crop.getFamily() + " | " + crop.getCurrentStage());
                    cropCount++;
                }
            }
            if (cropCount == 0) {
                System.out.println(family + " crops in 2001 : 0");
            }
            System.out.println();
        }

        System.out.println("// 3.2 Animal Filter : HEALTHY");
        int healthyCount = 0;
        for (Animal animal : eastPasture2.getAnimals()) {
            if (animal.getHealthStatus() == HealthStatus.HEALTHY) {
                healthyCount++;
            }
        }
        System.out.println("HEALTHY animals in 2002 : " + healthyCount);
        for (Animal animal : eastPasture2.getAnimals()) {
            if (animal.getHealthStatus() == HealthStatus.HEALTHY) {
                System.out.println("[" + animal.getId() + "] " + animal.getSpecies() + " | age:" + animal.getAge() + " | " + animal.getWeight() + "kg | " + animal.getHealthStatus());
            }
        }
        System.out.println();

        System.out.println("// Filter : SICK");
        int sickCount = 0;
        for (Animal animal : eastPasture2.getAnimals()) {
            if (animal.getHealthStatus() == HealthStatus.SICK) {
                sickCount++;
            }
        }
        System.out.println("SICK animals in 2002 : " + sickCount);
        for (Animal animal : eastPasture2.getAnimals()) {
            if (animal.getHealthStatus() == HealthStatus.SICK) {
                System.out.println("[" + animal.getId() + "] " + animal.getSpecies() + " | age:" + animal.getAge() + " | " + animal.getWeight() + "kg | " + animal.getHealthStatus());
            }
        }
        System.out.println();

        System.out.println("// After quarantining the sheep");
        sheep2.updateHealthStatus(HealthStatus.QUARANTINE);
        System.out.println("Sheep health : " + sheep2.getHealthStatus());
        int quarantinedCount = 0;
        for (Animal animal : eastPasture2.getAnimals()) {
            if (animal.getHealthStatus() == HealthStatus.QUARANTINE) {
                quarantinedCount++;
            }
        }
        System.out.println("QUARANTINED animals in 2002 : " + quarantinedCount);
        for (Animal animal : eastPasture2.getAnimals()) {
            if (animal.getHealthStatus() == HealthStatus.QUARANTINE) {
                System.out.println("[" + animal.getId() + "] " + animal.getSpecies() + " | age:" + animal.getAge() + " | " + animal.getWeight() + "kg | " + animal.getHealthStatus());
            }
        }
        System.out.println();

        System.out.println("// Step 4 : Complete farm report");
        System.out.println("// Farm report - " + farm2.getName());
        System.out.println("----------------------------------------");
        System.out.println("Zone : [" + northFields2.getCode() + "] " + northFields2.getName() + " | CropZone | " + northFields2.getStatus());
        System.out.println("Crops : " + northFields2.getEntityCount());
        for (Crop crop : northFields2.getCrops()) {
            System.out.println("[" + crop.getId() + "] " + crop.getName() + " | " + crop.getFamily() + " | " + crop.getCurrentStage() + " | ph:[" + (crop.getFamily() == CropFamily.FRUIT ? "6.0-7.0" : crop.getFamily() == CropFamily.VEGETABLE ? "5.5-7.0" : "6.0-7.5") + "] | humidity:[" + (crop.getFamily() == CropFamily.FRUIT ? "45-75%" : crop.getFamily() == CropFamily.VEGETABLE ? "50-80%" : "40-70%") + "]");
        }
        System.out.println();
        System.out.println("Zone : [" + eastPasture2.getCode() + "] " + eastPasture2.getName() + " | LivestockZone | " + eastPasture2.getStatus());
        int healthy = 0;
        int sick = 0;
        int quarantined = 0;
        for (Animal animal : eastPasture2.getAnimals()) {
            if (animal.getHealthStatus() == HealthStatus.HEALTHY) healthy++;
            if (animal.getHealthStatus() == HealthStatus.SICK) sick++;
            if (animal.getHealthStatus() == HealthStatus.QUARANTINE) quarantined++;
        }
        System.out.println("Animals : " + eastPasture2.getEntityCount() + " (healthy:" + healthy + " sick:" + sick + " quarantined:" + quarantined + ")");
        System.out.println("[Above : " + eastPasture2.getEntityCount() + " (healthy:" + healthy + " sick:" + sick + " quarantined:" + quarantined + ")]");
        for (Animal animal : eastPasture2.getAnimals()) {
            System.out.println("[" + animal.getId() + "] " + animal.getSpecies() + " | " + animal.getHealthStatus() + " | " + hayProgram.getFoodType() + " | " + hayProgram.getQuantityPerMeal() + " kg/meal");
        }
        System.out.println();
        System.out.println("Zone : [" + southPond2.getCode() + "] " + southPond2.getName() + " | AquaZone | " + southPond2.getStatus());
        System.out.println("Entities: " + southPond2.getEntityCount());
    }
}
