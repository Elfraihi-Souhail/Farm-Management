# Smart Farm Management System

Object-Oriented Programming project — Higher National School of Computer Science (2025/2026)
Made by **ELFRAIHI Souhail Charaf eddine** and **Hamouda Mohamed Hatem**

The project combines:
- **Farm Entities** — Zones, Animals, Crops, Aquaculture, Production
- **Sensors & Alerts** — Environmental / Soil / Water / Biometric / GPS sensors, readings, alerts

It ships with front-end:
- a **JavaFX GUI** (`gui.FarmApp`) — full graphical interface, auto-seeded with sample data

---

## Prerequisites

Install a **JDK (Java Development Kit), version 17 or newer**, for your operating system.
This gives you the `javac` (compiler) and `java` (runtime) commands used below.

- Download: https://adoptium.net/  (Temurin) — pick the build for your OS:
  - **macOS** → macOS installer (Apple Silicon **aarch64** for M1/M2/M3, or **x64** for Intel Macs)
  - **Windows** → Windows x64 installer
  - **Linux** → your distro package or the Linux x64 archive
- Verify the install in a terminal:
  ```
  java -version
  javac -version
  ```
  Both must print version **17** or higher.

> The **console demo (Option A)** only needs this JDK.
> The **JavaFX GUI (Option B)** additionally needs the JavaFX SDK (see that section).

---

## Project layout

```
Farm-Management/
├── README.md                ← this file
├── student1-FarmEntities/
└── student2-SensorsAlerts/
    └── src/                 ← all source code (packages: gui, farm, common, student1, student2)
```

All commands below are run **from inside** `student2-SensorsAlerts/`.

---
## JavaFX GUI

The GUI needs the **JavaFX SDK** (it is **not** part of the JDK since Java 11).
Download it for your machine, then point `--module-path` at its `lib` folder.

> Download: https://gluonhq.com/products/javafx/  (version 17 or newer)
> - **macOS** Apple Silicon (M1/M2/M3) → macOS **aarch64**
> - **macOS** Intel → macOS **x64**
> - **Windows** → Windows **x64**
> - **Linux** → Linux **x64**
>
> Unzip it; `PATH_TO_FX` below is the path to its `lib` folder
> (e.g. `~/Downloads/javafx-sdk-21/lib`).

### macOS / Linux

```bash
cd student2-SensorsAlerts

# compile everything
javac --module-path PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
      -d out $(find src -name '*.java')

# the GUI loads its stylesheet from the classpath — copy it next to the classes
mkdir -p out/gui && cp src/gui/styles.css out/gui/styles.css

# run
java --module-path PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
     -cp out gui.FarmApp
```

### Windows (cmd)

```bat
cd student2-SensorsAlerts
javac --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -d out src\Main.java src\common\*.java src\farm\*.java src\student1\*.java src\student2\*.java src\gui\*.java
mkdir out\gui 2>nul
copy src\gui\styles.css out\gui\styles.css
java --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp out gui.FarmApp
```

---

## Notes
- The GUI **auto-seeds realistic sample data** on every launch (5 zones, animals, crops, species,
  sensors with readings, alerts, GPS geofences, feeding programs, production history).
  Use **Farm → Reset Farm** to start empty.
- If the stylesheet (`styles.css`) is not copied, the GUI still runs — just without the dark theme.
