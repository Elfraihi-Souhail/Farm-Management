package gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import farm.Farm;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import student1.*;
import student2.*;
import student2.Alert;

public class FarmApp extends Application {

    private final FarmModel model = FarmModel.get();
    private BorderPane root;
    private StackPane content;
    private HBox toolbar;
    private Label statusBar;
    private Label titleLabel;
    private Stage primaryStage;
    private Button activeNavBtn;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        seedSampleData();

        root = new BorderPane();
        root.setLeft(buildSidebar());

        VBox center = new VBox(10);
        center.setPadding(new Insets(16));
        titleLabel = new Label("Dashboard");
        titleLabel.getStyleClass().add("section-title");
        toolbar = new HBox(8);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar");
        content = new StackPane();
        VBox.setVgrow(content, Priority.ALWAYS);
        center.getChildren().addAll(titleLabel, toolbar, new Separator(), content);
        root.setCenter(center);

        statusBar = new Label("Ready");
        statusBar.getStyleClass().add("status-bar");
        statusBar.setMaxWidth(Double.MAX_VALUE);
        statusBar.setPadding(new Insets(6, 12, 6, 12));
        root.setBottom(statusBar);

        showWelcome();

        Scene scene = new Scene(root, 1280, 800);
        var cssUrl = getClass().getResource("/gui/styles.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
        stage.setTitle("Smart Farm Management — " + model.getFarm().getName());
        stage.setScene(scene);
        stage.show();
    }

    // -------- Sidebar --------
    private VBox buildSidebar() {
        VBox sb = new VBox(2);
        sb.getStyleClass().add("sidebar-inner");
        sb.setPadding(new Insets(0, 6, 16, 6));

        Label logo = new Label("SMART FARM");
        logo.getStyleClass().add("sidebar-logo");
        logo.setMaxWidth(Double.MAX_VALUE);

        sb.getChildren().addAll(
            logo,
            new Separator(),
            navBtn("Dashboard", this::showWelcome),
            navBtn("Farm", this::viewFarm),
            sideLabel("ENTITIES"),
            navBtn("Zones", this::viewZones),
            navBtn("Crops", this::viewCrops),
            navBtn("Animals", this::viewAnimals),
            navBtn("Aquaculture", this::viewAquaculture),
            sideLabel("MONITORING"),
            navBtn("Sensors", this::viewSensors),
            navBtn("Alerts", this::viewAlerts),
            navBtn("Zone Dashboard", this::viewZoneDashboard),
            sideLabel("OTHER"),
            navBtn("Production", this::viewProduction),
            navBtn("About", this::showAbout)
        );

        ScrollPane sp = new ScrollPane(sb);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("sidebar-scroll");
        VBox wrap = new VBox(sp);
        VBox.setVgrow(sp, Priority.ALWAYS);
        wrap.getStyleClass().add("sidebar");
        return wrap;
    }

    private Button navBtn(String label, Runnable action) {
        Button b = new Button(label);
        b.getStyleClass().add("nav-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> { setActiveBtn(b); action.run(); });
        return b;
    }

    private Label sideLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("sidebar-section");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private void setActiveBtn(Button b) {
        if (activeNavBtn != null) activeNavBtn.getStyleClass().remove("active");
        activeNavBtn = b;
        if (b != null && !b.getStyleClass().contains("active")) b.getStyleClass().add("active");
    }

    // -------- Content helpers --------
    private void setContent(String title, javafx.scene.Node node) {
        titleLabel.setText(title);
        toolbar.getChildren().clear();
        toolbar.setVisible(false);
        toolbar.setManaged(false);
        content.getChildren().setAll(node);
    }

    /** Set title + a row of action buttons + content. */
    private void setView(String title, List<Node> actions, javafx.scene.Node node) {
        titleLabel.setText(title);
        toolbar.getChildren().setAll(actions);
        toolbar.setVisible(true);
        toolbar.setManaged(true);
        content.getChildren().setAll(node);
    }

    private Button actBtn(String label, Runnable action) {
        Button b = new Button(label);
        b.getStyleClass().add("act-btn");
        b.setOnAction(e -> action.run());
        return b;
    }

    /** Primary (accent) action button. */
    private Button primBtn(String label, Runnable action) {
        Button b = actBtn(label, action);
        b.getStyleClass().add("act-btn-primary");
        return b;
    }

    private Region grow() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private Node placeholderCard(String msg) {
        VBox v = new VBox();
        v.setPadding(new Insets(24));
        v.setAlignment(Pos.TOP_LEFT);
        Label l = new Label(msg);
        l.getStyleClass().add("muted");
        v.getChildren().add(l);
        return v;
    }

    private void setStatus(String s) { statusBar.setText(s); }

    /** Re-render whatever view is currently active (after a mutation). */
    private Runnable currentRefresh = this::showWelcome;
    private void refresh() { currentRefresh.run(); }

    // -------- Dashboard / Welcome --------
    private void showWelcome() {
        currentRefresh = this::showWelcome;
        Farm f = model.getFarm();
        int totalSensors = 0;
        for (GeographicalZone z : f.getZones()) totalSensors += z.getSensors().size();
        long activeAlerts = f.getAlertHistory().getAlerts().stream()
            .filter(a -> a.getStatus() == AlertStatus.ACTIVE).count();
        long critAlerts = f.getAlertHistory().getAlerts().stream()
            .filter(a -> a.getStatus() == AlertStatus.ACTIVE && a.getSeverityLevel() == SeverityLevel.CRITICAL).count();

        Label heading = new Label("Smart Farm Management System");
        heading.getStyleClass().add("welcome-title");
        Label sub = new Label(f.getName() + "   ·   "
            + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        sub.getStyleClass().add("muted");

        HBox cards = new HBox(14);
        cards.setPadding(new Insets(16, 0, 4, 0));
        HBox zonesCard   = statCard("ZONES",         String.valueOf(f.getZones().size()), "#4ea1f3");
        HBox sensorsCard = statCard("SENSORS",        String.valueOf(totalSensors),        "#8af7a8");
        HBox alertsCard  = statCard("ACTIVE ALERTS",  String.valueOf(activeAlerts),
            activeAlerts > 0 ? "#ffce4d" : "#8af7a8");
        HBox critCard    = statCard("CRITICAL",        String.valueOf(critAlerts),
            critAlerts > 0 ? "#ff6b6b" : "#8af7a8");
        for (HBox c : new HBox[]{zonesCard, sensorsCard, alertsCard, critCard}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
        }
        cards.getChildren().addAll(zonesCard, sensorsCard, alertsCard, critCard);

        VBox zonePanel  = buildZoneStatusPanel(f);
        VBox alertPanel = buildRecentAlertsPanel();

        HBox lower = new HBox(14);
        lower.setPadding(new Insets(14, 0, 0, 0));
        HBox.setHgrow(zonePanel,  Priority.ALWAYS);
        HBox.setHgrow(alertPanel, Priority.ALWAYS);
        zonePanel.setMaxWidth(Double.MAX_VALUE);
        alertPanel.setMaxWidth(Double.MAX_VALUE);
        lower.getChildren().addAll(zonePanel, alertPanel);

        VBox box = new VBox(6);
        box.setPadding(new Insets(24));
        VBox.setVgrow(lower, Priority.ALWAYS);
        box.getChildren().addAll(heading, sub, cards, lower);

        setContent("Dashboard", box);
    }

    private HBox statCard(String label, String value, String color) {
        Region bar = new Region();
        bar.setPrefWidth(5);
        bar.setMinWidth(5);
        bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4 0 0 4;");

        VBox text = new VBox(4);
        text.setPadding(new Insets(14, 18, 14, 14));
        text.setMaxWidth(Double.MAX_VALUE);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: " + color + ";");
        text.getChildren().addAll(lbl, val);

        HBox card = new HBox();
        card.getStyleClass().add("stat-card");
        card.getChildren().addAll(bar, text);
        return card;
    }

    private VBox buildZoneStatusPanel(Farm f) {
        VBox panel = new VBox(0);
        panel.getStyleClass().add("card-section");
        panel.setPadding(new Insets(16));

        Label title = new Label("ZONE STATUS");
        title.getStyleClass().add("card-section-title");
        panel.getChildren().add(title);

        List<GeographicalZone> zones = f.getZones();
        if (zones.isEmpty()) {
            Label empty = new Label("No zones yet. Use Zones menu to add one.");
            empty.getStyleClass().add("muted");
            empty.setPadding(new Insets(8, 0, 0, 0));
            panel.getChildren().add(empty);
        } else {
            for (int i = 0; i < zones.size(); i++) {
                GeographicalZone z = zones.get(i);
                boolean active = z.getStatus() == ZoneStatus.ACTIVE;

                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 0, 8, 0));

                Label dot = new Label(active ? "●" : "○");
                dot.getStyleClass().add(active ? "dot-active" : "dot-inactive");
                dot.setStyle("-fx-font-size: 14px;");

                Label name = new Label(z.getName());
                name.getStyleClass().add("stat-value-sm");

                Label code = new Label("[" + z.getCode() + "]");
                code.getStyleClass().add("muted");

                Label type = new Label(z.getClass().getSimpleName().replace("Zone", ""));
                type.getStyleClass().add("zone-type-badge");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label sensors = new Label(z.getSensors().size() + " sensors  |  " + z.getEntityCount() + " entities");
                sensors.getStyleClass().add("muted");

                row.getChildren().addAll(dot, name, code, type, spacer, sensors);
                panel.getChildren().add(row);
                if (i < zones.size() - 1) panel.getChildren().add(new Separator());
            }
        }
        return panel;
    }

    private VBox buildRecentAlertsPanel() {
        VBox panel = new VBox(0);
        panel.getStyleClass().add("card-section");
        panel.setPadding(new Insets(16));

        Label title = new Label("RECENT ACTIVE ALERTS");
        title.getStyleClass().add("card-section-title");
        panel.getChildren().add(title);

        List<Alert> active = new ArrayList<>();
        for (Alert a : model.getAlerts().getAlerts())
            if (a.getStatus() == AlertStatus.ACTIVE) active.add(a);
        active.sort(Comparator.comparing(Alert::getSeverityLevel).reversed());

        if (active.isEmpty()) {
            Label ok = new Label("✓  No active alerts");
            ok.getStyleClass().add("dot-active");
            ok.setPadding(new Insets(8, 0, 0, 0));
            panel.getChildren().add(ok);
        } else {
            List<Alert> shown = active.subList(0, Math.min(6, active.size()));
            for (int i = 0; i < shown.size(); i++) {
                Alert a = shown.get(i);
                boolean critical = a.getSeverityLevel() == SeverityLevel.CRITICAL;

                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 0, 8, 0));

                Label icon = new Label(critical ? "▲" : "△");
                icon.getStyleClass().add(critical ? "dash-critical" : "dash-warning");
                icon.setStyle("-fx-font-size: 12px;");

                Label id = new Label(a.getId());
                id.getStyleClass().add("muted");

                String sensorCode = (a.getReading() != null && a.getReading().getSensor() != null)
                    ? a.getReading().getSensor().getCode() : "—";
                Label sensor = new Label(sensorCode);
                sensor.getStyleClass().add("stat-value-sm");

                String zoneCode = (a.getReading() != null && a.getReading().getSensor() != null
                    && a.getReading().getSensor().getZone() != null)
                    ? a.getReading().getSensor().getZone().getCode() : "";
                Label zone = new Label(zoneCode);
                zone.getStyleClass().add("muted");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label sev = new Label(a.getSeverityLevel().name());
                sev.getStyleClass().add(critical ? "dash-critical" : "dash-warning");
                sev.getStyleClass().add("severity-badge");

                row.getChildren().addAll(icon, id, sensor, zone, spacer, sev);
                panel.getChildren().add(row);
                if (i < shown.size() - 1) panel.getChildren().add(new Separator());
            }
            if (active.size() > 6) {
                Label more = new Label("+ " + (active.size() - 6) + " more — see Alerts view");
                more.getStyleClass().add("muted");
                more.setPadding(new Insets(8, 0, 0, 0));
                panel.getChildren().add(more);
            }
        }
        return panel;
    }

    // -------- Farm actions --------
    private void doResetFarm() {
        TextInputDialog d = new TextInputDialog("Smart Farm");
        d.setTitle("Reset Farm");
        d.setHeaderText("This will erase all zones, sensors, alerts.");
        d.setContentText("New farm name:");
        d.initOwner(primaryStage);
        d.showAndWait().ifPresent(name -> {
            model.resetFarm(name);
            primaryStage.setTitle("Smart Farm Management — " + model.getFarm().getName());
            showWelcome();
            setStatus("Farm reset.");
        });
    }

    private void doRenameFarm() {
        TextInputDialog d = new TextInputDialog(model.getFarm().getName());
        d.setTitle("Rename Farm");
        d.setHeaderText("This rebuilds the farm container (zones/sensors/alerts preserved).");
        d.setContentText("New name:");
        d.initOwner(primaryStage);
        d.showAndWait().ifPresent(name -> {
            Farm old = model.getFarm();
            Farm n = new Farm(name);
            for (GeographicalZone z : old.getZones()) n.addZone(z);
            for (Alert a : old.getAlertHistory().getAlerts()) n.getAlertHistory().addAlert(a);
            try {
                java.lang.reflect.Field f = FarmModel.class.getDeclaredField("farm");
                f.setAccessible(true);
                f.set(model, n);
            } catch (Exception ex) { setStatus("Rename failed: " + ex.getMessage()); return; }
            primaryStage.setTitle("Smart Farm Management — " + model.getFarm().getName());
            setStatus("Renamed to " + name);
            showWelcome();
        });
    }

    // -------- Farm view --------
    private void viewFarm() {
        currentRefresh = this::viewFarm;
        Farm f = model.getFarm();
        VBox box = new VBox(10);
        box.setPadding(new Insets(8));

        for (GeographicalZone z : f.getZones()) {
            VBox card = new VBox(6);
            card.getStyleClass().add("card-section");
            card.setPadding(new Insets(14));

            boolean active = z.getStatus() == ZoneStatus.ACTIVE;
            Label header = new Label(z.getCode() + "  " + z.getName()
                + "  [" + z.getClass().getSimpleName().replace("Zone","") + "]");
            header.getStyleClass().add("stat-value-sm");

            Label status = new Label(String.valueOf(z.getStatus()));
            status.getStyleClass().add(active ? "dot-active" : "dot-inactive");

            HBox top = new HBox(10, header, status);
            top.setAlignment(Pos.CENTER_LEFT);

            Label detail = new Label("Entities: " + z.getEntityCount()
                + "   Sensors: " + z.getSensors().size()
                + "   Bounds: lat [" + z.getMinLatitude() + ", " + z.getMaxLatitude() + "]"
                + "  lon [" + z.getMinLongitude() + ", " + z.getMaxLongitude() + "]");
            detail.getStyleClass().add("muted");

            card.getChildren().addAll(top, detail);
            box.getChildren().add(card);
        }

        if (f.getZones().isEmpty()) box.getChildren().add(new Label("No zones."));
        setView("Farm Overview — " + f.getName(),
            List.of(primBtn("Rename Farm", this::doRenameFarm),
                    actBtn("Reset Farm", this::doResetFarm),
                    actBtn("Reload Sample Data", () -> { seedSampleData(); viewFarm(); setStatus("Sample data reloaded"); })),
            new ScrollPane(box));
    }

    // -------- Zones view --------
    private void viewZones() {
        currentRefresh = this::viewZones;
        TableView<GeographicalZone> table = new TableView<>(model.zones());
        table.getColumns().addAll(
            col("Code",     z -> z.getCode(),                            80),
            col("Name",     z -> z.getName(),                            160),
            col("Type",     z -> z.getClass().getSimpleName(),           130),
            col("Status",   z -> String.valueOf(z.getStatus()),          100),
            col("Entities", z -> String.valueOf(z.getEntityCount()),      80),
            col("Sensors",  z -> String.valueOf(z.getSensors().size()),   80)
        );
        table.setRowFactory(tv -> new TableRow<GeographicalZone>() {
            @Override protected void updateItem(GeographicalZone z, boolean empty) {
                super.updateItem(z, empty);
                getStyleClass().removeAll("row-suspended");
                if (!empty && z != null && z.getStatus() == ZoneStatus.SUSPENDED)
                    getStyleClass().add("row-suspended");
            }
        });
        table.setPlaceholder(new Label("No zones."));

        java.util.function.Supplier<GeographicalZone> sel = () -> {
            GeographicalZone z = table.getSelectionModel().getSelectedItem();
            if (z == null) warn("Select a zone in the table first.");
            return z;
        };

        setView("All Zones",
            List.of(primBtn("+ Crop Zone", this::dlgAddCropZone),
                    primBtn("+ Livestock Zone", this::dlgAddLivestockZone),
                    primBtn("+ Aquaculture Zone", this::dlgAddAquaZone),
                    grow(),
                    actBtn("Rename", () -> { GeographicalZone z = sel.get(); if (z != null) dlgRenameZone(z); }),
                    actBtn("Suspend", () -> { GeographicalZone z = sel.get(); if (z != null) doSuspendZone(z, true); }),
                    actBtn("Reactivate", () -> { GeographicalZone z = sel.get(); if (z != null) doSuspendZone(z, false); })),
            table);
    }

    private void dlgRenameZone(GeographicalZone z) {
        textPrompt("Rename Zone — " + z.getCode(), "New name:", z.getName()).ifPresent(name -> {
            if (name.isBlank()) { warn("Name cannot be empty."); return; }
            z.setName(name);
            setStatus(z.getCode() + " renamed to " + name);
            refresh();
        });
    }

    private <T> TableColumn<T,String> col(String name, java.util.function.Function<T,String> getter) {
        return col(name, getter, 110);
    }

    private <T> TableColumn<T,String> col(String name, java.util.function.Function<T,String> getter, double minW) {
        TableColumn<T,String> c = new TableColumn<>(name);
        c.setCellValueFactory(cd -> new SimpleStringProperty(getter.apply(cd.getValue())));
        c.setMinWidth(minW);
        return c;
    }

    // -------- Zone dialogs --------
    private void dlgAddCropZone() {
        textPrompt("Add Crop Zone", "Zone name:", "North Fields").ifPresent(name -> {
            CropZone z = new CropZone(name);
            model.getFarm().addZone(z);
            setStatus("Created " + z.getCode() + " (" + name + ")");
            refresh();
        });
    }

    private void dlgAddLivestockZone() {
        Dialog<ButtonType> d = newDialog("Add Livestock Zone");
        GridPane g = newGrid();
        TextField name = new TextField("East Pasture");
        ComboBox<LivestockType> tp = new ComboBox<>(FXCollections.observableArrayList(LivestockType.values()));
        tp.setValue(LivestockType.CATTLE);
        g.add(new Label("Name:"), 0, 0); g.add(name, 1, 0);
        g.add(new Label("Type:"), 0, 1); g.add(tp, 1, 1);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            LivestockZone z = new LivestockZone(name.getText(), tp.getValue());
            model.getFarm().addZone(z);
            setStatus("Created " + z.getCode());
            refresh();
        });
    }

    private void dlgAddAquaZone() {
        textPrompt("Add Aquaculture Zone", "Zone name:", "South Pond").ifPresent(name -> {
            AquacultureZone z = new AquacultureZone(name);
            model.getFarm().addZone(z);
            setStatus("Created " + z.getCode());
            refresh();
        });
    }

    private void doSuspendZone(GeographicalZone z, boolean suspend) {
        if (suspend) { model.getFarm().deactivateZone(z.getCode()); setStatus("Suspended " + z.getCode()); }
        else { model.getFarm().reactivateZone(z.getCode()); setStatus("Reactivated " + z.getCode()); }
        refresh();
    }

    // -------- Crop dialogs --------
    private void dlgAddCrop(String family, CropZone preselect) {
        ObservableList<CropZone> zones = model.cropZones();
        if (zones.isEmpty()) { warn("No CropZone exists. Add one first."); return; }
        Dialog<ButtonType> d = newDialog("Add " + family);
        GridPane g = newGrid();
        ComboBox<CropZone> zb = new ComboBox<>(zones);
        zb.setValue(preselect != null && zones.contains(preselect) ? preselect : zones.get(0));
        zb.setConverter(toStringConv(z -> z.getCode() + " - " + z.getName()));

        ComboBox<String> typeBox = new ComboBox<>();
        switch (family) {
            case "CEREAL"    -> { for (CerealType    t : CerealType.values())    typeBox.getItems().add(t.name()); }
            case "VEGETABLE" -> { for (VegetableType t : VegetableType.values()) typeBox.getItems().add(t.name()); }
            case "FRUIT"     -> { for (FruitType     t : FruitType.values())     typeBox.getItems().add(t.name()); }
        }
        typeBox.setValue(typeBox.getItems().get(0));

        DatePicker dp1 = new DatePicker(LocalDate.now());
        DatePicker dp2 = new DatePicker(LocalDate.now().plusMonths(4));
        ComboBox<GrowthStage> gs = new ComboBox<>(FXCollections.observableArrayList(GrowthStage.values()));
        gs.setValue(GrowthStage.SEEDING);

        g.add(new Label("Zone:"),     0, 0); g.add(zb,      1, 0);
        g.add(new Label("Type:"),     0, 1); g.add(typeBox, 1, 1);
        g.add(new Label("Planting:"), 0, 2); g.add(dp1,     1, 2);
        g.add(new Label("Harvest:"),  0, 3); g.add(dp2,     1, 3);
        g.add(new Label("Stage:"),    0, 4); g.add(gs,      1, 4);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            Crop c = null;
            String t = typeBox.getValue();
            switch (family) {
                case "CEREAL"    -> c = new Cereal(   CerealType.valueOf(t),    dp1.getValue(), dp2.getValue(), gs.getValue());
                case "VEGETABLE" -> c = new Vegetable(VegetableType.valueOf(t), dp1.getValue(), dp2.getValue(), gs.getValue());
                case "FRUIT"     -> c = new Fruit(    FruitType.valueOf(t),     dp1.getValue(), dp2.getValue(), gs.getValue());
            }
            zb.getValue().addCrop(c);
            curCropZone = zb.getValue();
            setStatus("Crop " + c.getId() + " added to " + zb.getValue().getCode());
            refresh();
        });
    }

    private void dlgUpdateGrowthStage(CropZone z, Crop c) {
        if (c == null) { warn("Select a crop in the table first."); return; }
        GrowthStage st = pick("New Growth Stage",
            FXCollections.observableArrayList(GrowthStage.values()), GrowthStage::name);
        if (st == null) return;
        z.updateCropStage(c.getId(), st);
        setStatus(c.getId() + " stage -> " + st);
        refresh();
    }

    // -------- Crops view --------
    private GeographicalZone curCropZone;
    private void viewCrops() {
        currentRefresh = this::viewCrops;
        ObservableList<CropZone> zones = model.cropZones();
        if (zones.isEmpty()) {
            curCropZone = null;
            setView("Crops", List.of(), placeholderCard("No crop zone yet. Add a Crop Zone from the Zones view first."));
            return;
        }
        if (!(curCropZone instanceof CropZone) || !zones.contains(curCropZone)) curCropZone = zones.get(0);
        CropZone z = (CropZone) curCropZone;

        TableView<Crop> table = new TableView<>(FXCollections.observableArrayList(z.getCrops()));
        table.getColumns().addAll(
            col("ID",      Crop::getId,                                        80),
            col("Name",    Crop::getName,                                     140),
            col("Family",  c -> String.valueOf(c.getFamily()),                 100),
            col("Stage",   c -> String.valueOf(c.getCurrentStage()),           120),
            col("Planted", c -> String.valueOf(c.getPlantingDate()),           120),
            col("Harvest", c -> String.valueOf(c.getExpectedHarvestDate()),    120)
        );
        table.setPlaceholder(new Label("No crops in this zone."));

        ComboBox<CropZone> zb = new ComboBox<>(zones);
        zb.setConverter(toStringConv(x -> x.getCode() + " - " + x.getName()));
        zb.setValue(z);
        zb.valueProperty().addListener((o, a, b) -> { if (b != null) { curCropZone = b; viewCrops(); } });

        setView("Crops",
            List.of(new Label("Zone:"), zb, grow(),
                    primBtn("+ Cereal", () -> dlgAddCrop("CEREAL", z)),
                    primBtn("+ Vegetable", () -> dlgAddCrop("VEGETABLE", z)),
                    primBtn("+ Fruit", () -> dlgAddCrop("FRUIT", z)),
                    actBtn("Update Stage", () -> dlgUpdateGrowthStage(z, table.getSelectionModel().getSelectedItem()))),
            table);
    }

    // -------- Animal dialogs --------
    private void dlgAddAnimal(LivestockZone preselect) {
        ObservableList<LivestockZone> zones = model.livestockZones();
        if (zones.isEmpty()) { warn("No LivestockZone exists."); return; }
        Dialog<ButtonType> d = newDialog("Add Animal");
        GridPane g = newGrid();
        ComboBox<LivestockZone> zb = new ComboBox<>(zones);
        zb.setValue(preselect != null && zones.contains(preselect) ? preselect : zones.get(0));
        zb.setConverter(toStringConv(z -> z.getCode() + " - " + z.getName()));
        TextField species = new TextField("cow");
        ComboBox<AnimalType> tp = new ComboBox<>(FXCollections.observableArrayList(AnimalType.values()));
        tp.setValue(AnimalType.RUMINANT);
        TextField age = new TextField("3");
        TextField weight = new TextField("450");
        ComboBox<HealthStatus> hs = new ComboBox<>(FXCollections.observableArrayList(HealthStatus.values()));
        hs.setValue(HealthStatus.HEALTHY);
        g.add(new Label("Zone:"),       0, 0); g.add(zb,      1, 0);
        g.add(new Label("Species:"),    0, 1); g.add(species, 1, 1);
        g.add(new Label("Type:"),       0, 2); g.add(tp,      1, 2);
        g.add(new Label("Age:"),        0, 3); g.add(age,     1, 3);
        g.add(new Label("Weight (kg):"),0, 4); g.add(weight,  1, 4);
        g.add(new Label("Health:"),     0, 5); g.add(hs,      1, 5);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                Animal a = new Animal(species.getText(), tp.getValue(),
                    Integer.parseInt(age.getText()), Double.parseDouble(weight.getText()), hs.getValue());
                zb.getValue().addAnimal(a);
                curAnimalZone = zb.getValue();
                setStatus("Animal " + a.getId() + " added to " + zb.getValue().getCode());
                refresh();
            } catch (Exception ex) { warn("Invalid input: " + ex.getMessage()); }
        });
    }

    private void dlgUpdateAnimalHealth(Animal a) {
        if (a == null) { warn("Select an animal in the table first."); return; }
        HealthStatus h = pick("New Health Status",
            FXCollections.observableArrayList(HealthStatus.values()), HealthStatus::name);
        if (h == null) return;
        a.updateHealthStatus(h);
        setStatus(a.getId() + " -> " + h);
        refresh();
    }

    private void dlgUpdateAnimalWeight(Animal a) {
        if (a == null) { warn("Select an animal in the table first."); return; }
        textPrompt("Record Weight", "New weight (kg):", String.valueOf(a.getWeight())).ifPresent(s -> {
            try { a.recordWeightEvolution(Double.parseDouble(s)); setStatus(a.getId() + " weight -> " + s); refresh(); }
            catch (NumberFormatException ex) { warn("Invalid number."); }
        });
    }

    // -------- Animals view --------
    private GeographicalZone curAnimalZone;
    private void viewAnimals() {
        currentRefresh = this::viewAnimals;
        ObservableList<LivestockZone> zones = model.livestockZones();
        if (zones.isEmpty()) {
            curAnimalZone = null;
            setView("Animals", List.of(), placeholderCard("No livestock zone yet. Add a Livestock Zone from the Zones view first."));
            return;
        }
        if (!(curAnimalZone instanceof LivestockZone) || !zones.contains(curAnimalZone)) curAnimalZone = zones.get(0);
        LivestockZone z = (LivestockZone) curAnimalZone;

        TableView<Animal> table = new TableView<>(FXCollections.observableArrayList(z.getAnimals()));
        table.getColumns().addAll(
            col("ID",      Animal::getId,                                    80),
            col("Species", Animal::getSpecies,                              130),
            col("Type",    a -> String.valueOf(a.getType()),                100),
            col("Age",     a -> String.valueOf(a.getAge()),                  60),
            col("Weight",  a -> String.valueOf(a.getWeight()),               80),
            col("Health",  a -> String.valueOf(a.getHealthStatus()),        110),
            col("GPS",     a -> a.getGpsCollar() == null ? "—" : a.getGpsCollar().getCode(), 100),
            col("Feeding", a -> feedingSummary(animalFeeding.get(a)),       180)
        );
        table.setRowFactory(tv -> new TableRow<Animal>() {
            @Override protected void updateItem(Animal a, boolean empty) {
                super.updateItem(a, empty);
                getStyleClass().removeAll("row-warning", "row-critical");
                if (!empty && a != null) {
                    String h = String.valueOf(a.getHealthStatus());
                    if ("SICK".equals(h) || "CRITICAL".equals(h)) getStyleClass().add("row-critical");
                    else if ("INJURED".equals(h) || "UNDER_TREATMENT".equals(h)) getStyleClass().add("row-warning");
                }
            }
        });
        table.setPlaceholder(new Label("No animals in this zone."));

        ComboBox<LivestockZone> zb = new ComboBox<>(zones);
        zb.setConverter(toStringConv(x -> x.getCode() + " - " + x.getName()));
        zb.setValue(z);
        zb.valueProperty().addListener((o, a, b) -> { if (b != null) { curAnimalZone = b; viewAnimals(); } });

        setView("Animals",
            List.of(new Label("Zone:"), zb, grow(),
                    primBtn("+ Animal", () -> dlgAddAnimal(z)),
                    actBtn("Update Health", () -> dlgUpdateAnimalHealth(table.getSelectionModel().getSelectedItem())),
                    actBtn("Record Weight", () -> dlgUpdateAnimalWeight(table.getSelectionModel().getSelectedItem())),
                    actBtn("Feeding Program", () -> dlgFeedingAnimal(table.getSelectionModel().getSelectedItem()))),
            table);
    }

    // -------- Feeding programs --------
    private final java.util.Map<Animal, FeedingProgram> animalFeeding = new java.util.HashMap<>();

    private String feedingSummary(FeedingProgram fp) {
        if (fp == null) return "—";
        return String.format("%s · %.1f ×%d/day", fp.getFoodType(), fp.getQuantityPerMeal(), fp.getMealsPerDay());
    }

    /** Dialog to set/modify a feeding program. Returns new values applied via callback. */
    private void dlgFeedingProgram(String title, FeedingProgram existing,
                                   TriConsumer<String, Double, Integer> apply) {
        Dialog<ButtonType> d = newDialog(title);
        GridPane g = newGrid();
        TextField food = new TextField(existing == null ? "Hay" : existing.getFoodType());
        TextField qty  = new TextField(existing == null ? "5.0" : String.valueOf(existing.getQuantityPerMeal()));
        TextField mpd  = new TextField(existing == null ? "2"   : String.valueOf(existing.getMealsPerDay()));
        g.add(new Label("Food type:"),        0, 0); g.add(food, 1, 0);
        g.add(new Label("Quantity/meal:"),    0, 1); g.add(qty,  1, 1);
        g.add(new Label("Meals per day:"),    0, 2); g.add(mpd,  1, 2);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                String f = food.getText().isBlank() ? "Feed" : food.getText();
                double q = Double.parseDouble(qty.getText());
                int m = Integer.parseInt(mpd.getText());
                if (q <= 0 || m <= 0) { warn("Quantity and meals must be > 0."); return; }
                apply.accept(f, q, m);
                refresh();
            } catch (NumberFormatException ex) { warn("Invalid number."); }
        });
    }

    private void dlgFeedingAnimal(Animal a) {
        if (a == null) { warn("Select an animal in the table first."); return; }
        FeedingProgram fp = animalFeeding.get(a);
        dlgFeedingProgram("Feeding Program — " + a.getId() + " (" + a.getSpecies() + ")", fp, (food, q, m) -> {
            FeedingProgram cur = animalFeeding.get(a);
            if (cur == null) animalFeeding.put(a, new FeedingProgram(food, q, m));
            else cur.updateProgram(food, q, m);
            setStatus("Feeding program set for " + a.getId());
        });
    }

    private void dlgFeedingSpecies(AquacultureSpecies sp) {
        if (sp == null) { warn("Select a species in the table first."); return; }
        dlgFeedingProgram("Feeding Program — " + sp.getSpeciesName(), sp.getFeedingProgram(), (food, q, m) -> {
            FeedingProgram cur = sp.getFeedingProgram();
            if (cur == null) sp.setFeedingProgram(new FeedingProgram(food, q, m));
            else cur.updateProgram(food, q, m);
            setStatus("Feeding program set for " + sp.getSpeciesName());
        });
    }

    @FunctionalInterface
    private interface TriConsumer<A, B, C> { void accept(A a, B b, C c); }

    // -------- Aquaculture dialogs --------
    private void dlgAddSpecies(AquacultureZone preselect) {
        ObservableList<AquacultureZone> zones = model.aquaZones();
        if (zones.isEmpty()) { warn("No AquacultureZone exists."); return; }
        Dialog<ButtonType> d = newDialog("Add Aquaculture Species");
        GridPane g = newGrid();
        ComboBox<AquacultureZone> zb = new ComboBox<>(zones);
        zb.setValue(preselect != null && zones.contains(preselect) ? preselect : zones.get(0));
        zb.setConverter(toStringConv(z -> z.getCode() + " - " + z.getName()));
        TextField nm    = new TextField("Tilapia");
        ComboBox<AquacultureType> tp = new ComboBox<>(FXCollections.observableArrayList(AquacultureType.values()));
        tp.setValue(AquacultureType.FISH);
        TextField count = new TextField("200");
        g.add(new Label("Zone:"),    0, 0); g.add(zb,    1, 0);
        g.add(new Label("Species:"), 0, 1); g.add(nm,    1, 1);
        g.add(new Label("Type:"),    0, 2); g.add(tp,    1, 2);
        g.add(new Label("Count:"),   0, 3); g.add(count, 1, 3);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                AquacultureSpecies sp = new AquacultureSpecies(nm.getText(), tp.getValue(), Integer.parseInt(count.getText()));
                zb.getValue().addSpecies(sp);
                curAquaZone = zb.getValue();
                setStatus("Species " + nm.getText() + " added to " + zb.getValue().getCode());
                refresh();
            } catch (Exception ex) { warn("Invalid input."); }
        });
    }

    // -------- Aquaculture view --------
    private GeographicalZone curAquaZone;
    private void viewAquaculture() {
        currentRefresh = this::viewAquaculture;
        ObservableList<AquacultureZone> zones = model.aquaZones();
        if (zones.isEmpty()) {
            curAquaZone = null;
            setView("Aquaculture", List.of(), placeholderCard("No aquaculture zone yet. Add an Aquaculture Zone from the Zones view first."));
            return;
        }
        if (!(curAquaZone instanceof AquacultureZone) || !zones.contains(curAquaZone)) curAquaZone = zones.get(0);
        AquacultureZone z = (AquacultureZone) curAquaZone;

        TableView<AquacultureSpecies> table = new TableView<>(FXCollections.observableArrayList(z.getSpecies()));
        table.getColumns().addAll(
            col("Species", AquacultureSpecies::getSpeciesName,              160),
            col("Type",    s -> String.valueOf(s.getType()),                120),
            col("Count",   s -> String.valueOf(s.getNumberOfAnimals()),      80),
            col("Feeding", s -> feedingSummary(s.getFeedingProgram()),      180)
        );
        table.setPlaceholder(new Label("No species in this zone."));

        ComboBox<AquacultureZone> zb = new ComboBox<>(zones);
        zb.setConverter(toStringConv(x -> x.getCode() + " - " + x.getName()));
        zb.setValue(z);
        zb.valueProperty().addListener((o, a, b) -> { if (b != null) { curAquaZone = b; viewAquaculture(); } });

        setView("Aquaculture",
            List.of(new Label("Zone:"), zb, grow(),
                    primBtn("+ Species", () -> dlgAddSpecies(z)),
                    actBtn("Feeding Program", () -> dlgFeedingSpecies(table.getSelectionModel().getSelectedItem()))),
            table);
    }

    // -------- Sensors view --------
    private void viewSensors() {
        currentRefresh = this::viewSensors;
        TableView<Sensor> table = new TableView<>(model.allSensors());
        table.getColumns().addAll(
            col("Code",     Sensor::getCode,                                        80),
            col("Zone",     s -> s.getZone() == null ? "—" : s.getZone().getCode(), 80),
            col("Type",     Sensor::getSensorTypeName,                             150),
            col("Name",     Sensor::getName,                                       160),
            col("Status",   s -> String.valueOf(s.getStatus()),                    100),
            col("Range",    s -> s instanceof GPSCollar gps ? gpsRangeLabel(gps)
                                  : "[" + s.getMinThreshold() + " – " + s.getMaxThreshold() + "]", 230),
            col("Battery",  s -> s.getBatteryLevel() + "%",                         70),
            col("Readings", s -> String.valueOf(s.getReadings().size()),             70)
        );
        table.setRowFactory(tv -> new TableRow<Sensor>() {
            @Override protected void updateItem(Sensor s, boolean empty) {
                super.updateItem(s, empty);
                getStyleClass().removeAll("row-warning", "row-dismissed", "row-critical");
                if (!empty && s != null) {
                    String st = String.valueOf(s.getStatus());
                    if ("DEFECTIVE".equals(st) || "FAULTY".equals(st)) getStyleClass().add("row-critical");
                    else if ("SUSPENDED".equals(st)) getStyleClass().add("row-dismissed");
                }
            }
        });
        table.setPlaceholder(new Label("No sensors."));

        java.util.function.Supplier<Sensor> sel = () -> {
            Sensor s = table.getSelectionModel().getSelectedItem();
            if (s == null) warn("Select a sensor in the table first.");
            return s;
        };

        MenuButton addBtn = new MenuButton("+ Add Sensor");
        addBtn.getStyleClass().add("act-btn-primary");
        addBtn.getItems().addAll(
            menuItem("Environmental", this::dlgAddEnvSensor),
            menuItem("Soil", this::dlgAddSoilSensor),
            menuItem("Water", this::dlgAddWaterSensor),
            menuItem("Biometric", this::dlgAddBiometricSensor),
            menuItem("GPS Collar", this::dlgAddGPSCollar));

        MenuButton statusBtn = new MenuButton("Change Status");
        statusBtn.getStyleClass().add("act-btn");
        statusBtn.getItems().addAll(
            menuItem("Suspend", () -> { Sensor s = sel.get(); if (s != null) doSensorStatus("suspend", s); }),
            menuItem("Activate", () -> { Sensor s = sel.get(); if (s != null) doSensorStatus("activate", s); }),
            menuItem("Mark Defective", () -> { Sensor s = sel.get(); if (s != null) doSensorStatus("defective", s); }),
            menuItem("Mark Faulty", () -> { Sensor s = sel.get(); if (s != null) doSensorStatus("faulty", s); }));

        setView("All Sensors",
            List.of(addBtn,
                    primBtn("Record Reading", () -> {
                        Sensor s = table.getSelectionModel().getSelectedItem();
                        if (s instanceof GPSCollar gps) dlgRecordGpsReading(gps);
                        else dlgRecordReading();
                    }),
                    actBtn("Edit Thresholds", () -> { Sensor s = sel.get(); if (s != null) dlgEditThresholds(s); }),
                    grow(),
                    actBtn("View Readings", () -> { Sensor s = sel.get(); if (s != null) dlgViewReadings(s); }),
                    actBtn("Statistics", () -> { Sensor s = sel.get(); if (s != null) dlgViewStats(s); }),
                    actBtn("Filter Readings", () -> { Sensor s = sel.get(); if (s != null) dlgFilterReadings(s); }),
                    statusBtn),
            table);
    }

    private void dlgEditThresholds(Sensor s) {
        if (s instanceof GPSCollar gps) { dlgEditGpsRange(gps); return; }
        Dialog<ButtonType> d = newDialog("Edit Thresholds — " + s.getCode());
        GridPane g = newGrid();
        TextField mn = new TextField(String.valueOf(s.getMinThreshold()));
        TextField mx = new TextField(String.valueOf(s.getMaxThreshold()));
        g.add(new Label("Sensor:"), 0, 0); g.add(new Label(s.getCode() + " " + s.getSensorTypeName()), 1, 0);
        g.add(new Label("Min:"),    0, 1); g.add(mn, 1, 1);
        g.add(new Label("Max:"),    0, 2); g.add(mx, 1, 2);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                double min = Double.parseDouble(mn.getText());
                double max = Double.parseDouble(mx.getText());
                if (min > max) { warn("Min must be ≤ Max."); return; }
                s.updateThresholds(min, max);
                setStatus(s.getCode() + " thresholds → [" + min + " – " + max + "]");
                refresh();
            } catch (NumberFormatException ex) { warn("Invalid number."); }
        });
    }

    /** Per-collar geofence box: {minLat, maxLat, minLon, maxLon}. */
    private final java.util.Map<GPSCollar, double[]> gpsRanges = new java.util.HashMap<>();

    /** GPS range = an allowed 2D box [(minLat,minLon), (maxLat,maxLon)]. */
    private void dlgEditGpsRange(GPSCollar gps) {
        double[] box = gpsRanges.get(gps);
        GeographicalZone z = gps.getZone();
        if (box == null && z != null)  // default to the zone bounds
            box = new double[]{ z.getMinLatitude(), z.getMaxLatitude(), z.getMinLongitude(), z.getMaxLongitude() };
        if (box == null) box = new double[]{ 0, 0, 0, 0 };

        Dialog<ButtonType> d = newDialog("Set GPS Range — " + gps.getCode());
        GridPane g = newGrid();
        TextField minLat = new TextField(String.valueOf(box[0]));
        TextField maxLat = new TextField(String.valueOf(box[1]));
        TextField minLon = new TextField(String.valueOf(box[2]));
        TextField maxLon = new TextField(String.valueOf(box[3]));
        g.add(new Label("Min latitude (x):"),  0, 0); g.add(minLat, 1, 0);
        g.add(new Label("Max latitude (x):"),  0, 1); g.add(maxLat, 1, 1);
        g.add(new Label("Min longitude (y):"), 0, 2); g.add(minLon, 1, 2);
        g.add(new Label("Max longitude (y):"), 0, 3); g.add(maxLon, 1, 3);
        Label hint = new Label("Allowed box [(min_x,min_y) , (max_x,max_y)].\nAlert fires when a recorded position falls outside it.");
        hint.getStyleClass().add("muted");
        g.add(hint, 0, 4, 2, 1);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                double mnLa = Double.parseDouble(minLat.getText());
                double mxLa = Double.parseDouble(maxLat.getText());
                double mnLo = Double.parseDouble(minLon.getText());
                double mxLo = Double.parseDouble(maxLon.getText());
                if (mnLa > mxLa || mnLo > mxLo) { warn("Min must be ≤ Max on each axis."); return; }
                gpsRanges.put(gps, new double[]{ mnLa, mxLa, mnLo, mxLo });
                setStatus(gps.getCode() + String.format(" range → [(%.4f,%.4f) , (%.4f,%.4f)]", mnLa, mnLo, mxLa, mxLo));
                refresh();
            } catch (NumberFormatException ex) { warn("Invalid number."); }
        });
    }

    private void dlgRecordGpsReading(GPSCollar gps) {
        if (gps.getAnimal() == null) {
            warn("This GPS collar is not linked to any animal.\nAssign it to an animal (Animals → Assign GPS) before recording a position.");
            return;
        }
        double[] box = gpsRanges.get(gps);
        if (box == null) { warn("Set a GPS range first (Edit Thresholds)."); return; }

        Animal animal = gps.getAnimal();
        Dialog<ButtonType> d = newDialog("Record GPS Position — " + gps.getCode());
        GridPane g = newGrid();
        TextField lat = new TextField(String.valueOf(gps.getLatitude()));
        TextField lon = new TextField(String.valueOf(gps.getLongitude()));
        g.add(new Label("Animal:"),        0, 0); g.add(new Label(animal.getId() + " - " + animal.getSpecies()), 1, 0);
        g.add(new Label("Latitude (x):"),  0, 1); g.add(lat, 1, 1);
        g.add(new Label("Longitude (y):"), 0, 2); g.add(lon, 1, 2);
        g.add(new Label("Allowed box:"),   0, 3);
        g.add(new Label(String.format("[(%.4f,%.4f) , (%.4f,%.4f)]", box[0], box[2], box[1], box[3])), 1, 3);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            if (gps.getStatus() != common.SensorStatus.ACTIVE) { warn("GPS collar not active; position not recorded."); return; }
            try {
                double la = Double.parseDouble(lat.getText());
                double lo = Double.parseDouble(lon.getText());
                gps.updatePosition(la, lo);
                // overshoot outside the box on each axis (degrees); 0 if inside
                double dLat = Math.max(0, Math.max(box[0] - la, la - box[1]));
                double dLon = Math.max(0, Math.max(box[2] - lo, lo - box[3]));
                double overshoot = Math.max(dLat, dLon);
                // GPS thresholds stay [0,0] → recordReading classifies: 0=NORMAL, ≤1°=WARNING, >1°=CRITICAL
                Reading r = gps.recordReading(Math.round(overshoot * 10000.0) / 10000.0, LocalDateTime.now(), model.getAlerts());
                if (r == null) { warn("Reading not recorded."); return; }
                if (overshoot == 0)
                    setStatus(gps.getCode() + " (" + animal.getId() + ") inside range → NORMAL");
                else
                    setStatus(gps.getCode() + " (" + animal.getId() + ")" + String.format(" OUTSIDE range by %.4f° → %s (alert raised)", overshoot, r.getStatus()));
                refresh();
                if (r.getStatus() == ReadingStatus.CRITICAL)
                    criticalPopup(gps.getCode() + " — GPS geofence breach",
                        String.format("%s (%s) at (%.2f, %.2f) is %.4f° OUTSIDE the allowed box.%nA critical alert has been raised.",
                            animal.getId(), animal.getSpecies(), la, lo, overshoot));
            } catch (NumberFormatException ex) { warn("Invalid number."); }
        });
    }

    /** Human-readable GPS geofence box, or "—" if unset. */
    private String gpsRangeLabel(GPSCollar gps) {
        double[] b = gpsRanges.get(gps);
        if (b == null) return "—";
        return String.format("[(%.2f,%.2f)-(%.2f,%.2f)]", b[0], b[2], b[1], b[3]);
    }

    private MenuItem menuItem(String label, Runnable action) {
        MenuItem m = new MenuItem(label);
        m.setOnAction(e -> action.run());
        return m;
    }

    private Button backToSensorsBtn() {
        return actBtn("← Back to Sensors", this::viewSensors);
    }

    // -------- Sensor dialogs --------
    private GeographicalZone pickZoneForSensor() {
        ObservableList<GeographicalZone> zs = model.zones();
        if (zs.isEmpty()) { warn("No zone exists. Add one first."); return null; }
        return pick("Select Zone", zs, x -> x.getCode() + " - " + x.getName());
    }

    private void dlgAddEnvSensor() {
        GeographicalZone z = pickZoneForSensor(); if (z == null) return;
        Dialog<ButtonType> d = newDialog("Add Environmental Sensor");
        GridPane g = newGrid();
        ComboBox<EnvironmentalSensorType> tp = new ComboBox<>(FXCollections.observableArrayList(EnvironmentalSensorType.values()));
        tp.setValue(EnvironmentalSensorType.TEMPERATURE);
        TextField unit = new TextField("°C");
        TextField mn = new TextField("10"); TextField mx = new TextField("35");
        g.add(new Label("Type:"), 0, 0); g.add(tp,   1, 0);
        g.add(new Label("Unit:"), 0, 1); g.add(unit, 1, 1);
        g.add(new Label("Min:"),  0, 2); g.add(mn,   1, 2);
        g.add(new Label("Max:"),  0, 3); g.add(mx,   1, 3);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                EnvironmentalSensor s = new EnvironmentalSensor(z,
                    Double.parseDouble(mn.getText()), Double.parseDouble(mx.getText()), tp.getValue(), unit.getText());
                z.addSensor(s); setStatus(s.getCode() + " added to " + z.getCode()); refresh();
            } catch (Exception ex) { warn("Invalid input."); }
        });
    }

    private void dlgAddSoilSensor() {
        GeographicalZone z = pickZoneForSensor(); if (z == null) return;
        Dialog<ButtonType> d = newDialog("Add Soil Sensor");
        GridPane g = newGrid();
        ComboBox<SoilSensorType> tp = new ComboBox<>(FXCollections.observableArrayList(SoilSensorType.values()));
        tp.setValue(SoilSensorType.PH);
        TextField unit = new TextField("pH");
        TextField mn = new TextField("5.5"); TextField mx = new TextField("7.5");
        g.add(new Label("Type:"), 0, 0); g.add(tp,   1, 0);
        g.add(new Label("Unit:"), 0, 1); g.add(unit, 1, 1);
        g.add(new Label("Min:"),  0, 2); g.add(mn,   1, 2);
        g.add(new Label("Max:"),  0, 3); g.add(mx,   1, 3);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                SoilSensor s = new SoilSensor(z,
                    Double.parseDouble(mn.getText()), Double.parseDouble(mx.getText()), tp.getValue(), unit.getText());
                z.addSensor(s); setStatus(s.getCode() + " added to " + z.getCode()); refresh();
            } catch (Exception ex) { warn("Invalid input."); }
        });
    }

    private void dlgAddWaterSensor() {
        GeographicalZone z = pickZoneForSensor(); if (z == null) return;
        Dialog<ButtonType> d = newDialog("Add Water Sensor");
        GridPane g = newGrid();
        ComboBox<WaterSensorType> tp = new ComboBox<>(FXCollections.observableArrayList(WaterSensorType.values()));
        tp.setValue(WaterSensorType.TEMPERATURE);
        TextField unit = new TextField("°C");
        TextField mn = new TextField("18"); TextField mx = new TextField("28");
        g.add(new Label("Type:"), 0, 0); g.add(tp,   1, 0);
        g.add(new Label("Unit:"), 0, 1); g.add(unit, 1, 1);
        g.add(new Label("Min:"),  0, 2); g.add(mn,   1, 2);
        g.add(new Label("Max:"),  0, 3); g.add(mx,   1, 3);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                WaterSensor s = new WaterSensor(z,
                    Double.parseDouble(mn.getText()), Double.parseDouble(mx.getText()), tp.getValue(), unit.getText());
                z.addSensor(s); setStatus(s.getCode() + " added to " + z.getCode()); refresh();
            } catch (Exception ex) { warn("Invalid input."); }
        });
    }

    private void dlgAddBiometricSensor() {
        GeographicalZone z = pickZoneForSensor(); if (z == null) return;
        Animal a = null;
        if (z instanceof LivestockZone lz && !lz.getAnimals().isEmpty()) {
            a = pick("Select Animal (optional)", FXCollections.observableArrayList(lz.getAnimals()),
                an -> an.getId() + " - " + an.getSpecies());
        }
        Dialog<ButtonType> d = newDialog("Add Biometric Sensor");
        GridPane g = newGrid();
        ComboBox<BiometricSensorType> tp = new ComboBox<>(FXCollections.observableArrayList(BiometricSensorType.values()));
        tp.setValue(BiometricSensorType.BODY_TEMPERATURE);
        TextField unit = new TextField("°C");
        TextField mn = new TextField("37"); TextField mx = new TextField("39");
        g.add(new Label("Type:"), 0, 0); g.add(tp,   1, 0);
        g.add(new Label("Unit:"), 0, 1); g.add(unit, 1, 1);
        g.add(new Label("Min:"),  0, 2); g.add(mn,   1, 2);
        g.add(new Label("Max:"),  0, 3); g.add(mx,   1, 3);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Animal aFinal = a;
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                BiometricSensor s = new BiometricSensor(z,
                    Double.parseDouble(mn.getText()), Double.parseDouble(mx.getText()),
                    tp.getValue(), aFinal, unit.getText());
                z.addSensor(s); setStatus(s.getCode() + " added to " + z.getCode()); refresh();
            } catch (Exception ex) { warn("Invalid input."); }
        });
    }

    private void dlgAddGPSCollar() {
        // GPS collars only belong to livestock zones, tracking a specific animal.
        ObservableList<LivestockZone> zones = model.livestockZones();
        if (zones.isEmpty()) { warn("GPS collars are only for livestock zones. Add a Livestock Zone first."); return; }

        Dialog<ButtonType> d = newDialog("Add GPS Collar");
        GridPane g = newGrid();

        ComboBox<LivestockZone> zb = new ComboBox<>(zones);
        zb.setConverter(toStringConv(x -> x.getCode() + " - " + x.getName()));
        zb.setValue(zones.get(0));
        ComboBox<Animal> ab = new ComboBox<>();
        ab.setConverter(toStringConv(a -> a.getId() + " - " + a.getSpecies()));
        // populate animals for the chosen zone, skip ones that already have a collar
        java.util.function.Consumer<LivestockZone> fillAnimals = z -> {
            ObservableList<Animal> free = FXCollections.observableArrayList();
            for (Animal a : z.getAnimals()) if (a.getGpsCollar() == null) free.add(a);
            ab.setItems(free);
            ab.setValue(free.isEmpty() ? null : free.get(0));
        };
        fillAnimals.accept(zb.getValue());
        zb.valueProperty().addListener((o, a, b) -> { if (b != null) fillAnimals.accept(b); });

        TextField minLat = new TextField("36.70");
        TextField maxLat = new TextField("36.75");
        TextField minLon = new TextField("3.15");
        TextField maxLon = new TextField("3.20");

        g.add(new Label("Zone:"),               0, 0); g.add(zb,     1, 0);
        g.add(new Label("Animal:"),             0, 1); g.add(ab,     1, 1);
        g.add(new Separator(),                  0, 2, 2, 1);
        g.add(new Label("Range min lat (x):"),  0, 3); g.add(minLat, 1, 3);
        g.add(new Label("Range max lat (x):"),  0, 4); g.add(maxLat, 1, 4);
        g.add(new Label("Range min lon (y):"),  0, 5); g.add(minLon, 1, 5);
        g.add(new Label("Range max lon (y):"),  0, 6); g.add(maxLon, 1, 6);
        Label hint = new Label("Position is set later via Record Reading.");
        hint.getStyleClass().add("muted");
        g.add(hint, 0, 7, 2, 1);

        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            LivestockZone z = zb.getValue();
            Animal animal = ab.getValue();
            if (animal == null) { warn("No free animal in this zone. Add an animal (without a collar) first."); return; }
            try {
                double mnLa = Double.parseDouble(minLat.getText());
                double mxLa = Double.parseDouble(maxLat.getText());
                double mnLo = Double.parseDouble(minLon.getText());
                double mxLo = Double.parseDouble(maxLon.getText());
                if (mnLa > mxLa || mnLo > mxLo) { warn("Range min must be ≤ max on each axis."); return; }

                // start the collar at the range centre; real position set when recording a reading
                GPSCollar s = new GPSCollar(z, animal, (mnLa + mxLa) / 2.0, (mnLo + mxLo) / 2.0);
                animal.assignGPSCollar(s);
                z.addSensor(s);
                gpsRanges.put(s, new double[]{ mnLa, mxLa, mnLo, mxLo });
                setStatus(s.getCode() + " added to " + z.getCode() + " for " + animal.getId());
                refresh();
            } catch (NumberFormatException ex) { warn("Invalid number."); }
        });
    }

    private void dlgRecordReading() {
        ObservableList<Sensor> ss = model.sensorsExcludingGPS();
        if (ss.isEmpty()) { warn("No sensor available."); return; }
        Dialog<ButtonType> d = newDialog("Record Reading");
        GridPane g = newGrid();
        ComboBox<Sensor> sb = new ComboBox<>(ss); sb.setValue(ss.get(0));
        sb.setConverter(toStringConv(s -> s.getCode() + "  " + s.getSensorTypeName()));
        sb.setPrefWidth(260);
        TextField v = new TextField("0.0");
        DatePicker dp = new DatePicker(LocalDate.now());
        TextField tm = new TextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        g.add(new Label("Sensor:"),    0, 0); g.add(sb, 1, 0);
        g.add(new Label("Value:"),     0, 1); g.add(v,  1, 1);
        g.add(new Label("Date:"),      0, 2); g.add(dp, 1, 2);
        g.add(new Label("Time HH:mm:"),0, 3); g.add(tm, 1, 3);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                double val = Double.parseDouble(v.getText());
                String[] hm = tm.getText().split(":");
                LocalDateTime dt = LocalDateTime.of(dp.getValue(),
                    java.time.LocalTime.of(Integer.parseInt(hm[0]), Integer.parseInt(hm[1])));
                Reading r = sb.getValue().recordReading(val, dt, model.getAlerts());
                if (r == null) warn("Sensor is suspended; reading not recorded.");
                else {
                    setStatus("Reading " + val + " → " + r.getStatus() + " on " + sb.getValue().getCode());
                    refresh();
                    if (r.getStatus() == ReadingStatus.CRITICAL)
                        criticalPopup(sb.getValue().getCode() + " — " + sb.getValue().getSensorTypeName(),
                            String.format("CRITICAL reading: %.2f%s%nThreshold range [%s – %s].%nA critical alert has been raised.",
                                val, sb.getValue().getUnit(), sb.getValue().getMinThreshold(), sb.getValue().getMaxThreshold()));
                }
            } catch (Exception ex) { warn("Invalid input."); }
        });
    }

    private void dlgViewReadings(Sensor s) {
        TableView<Reading> table = new TableView<>(FXCollections.observableArrayList(s.getReadings()));
        table.getColumns().addAll(
            col("DateTime", r -> r.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 160),
            col("Value",    r -> String.valueOf(r.getValue()),                                              90),
            col("Unit",     Reading::getUnit,                                                               70),
            col("Status",   r -> String.valueOf(r.getStatus()),                                            100)
        );
        table.setRowFactory(tv -> new TableRow<Reading>() {
            @Override protected void updateItem(Reading r, boolean empty) {
                super.updateItem(r, empty);
                getStyleClass().removeAll("row-critical", "row-warning");
                if (!empty && r != null) {
                    String st = String.valueOf(r.getStatus());
                    if ("CRITICAL".equals(st)) getStyleClass().add("row-critical");
                    else if ("WARNING".equals(st)) getStyleClass().add("row-warning");
                }
            }
        });
        table.setPlaceholder(new Label("No readings."));
        setView("Readings — " + s.getCode() + " (" + s.getSensorTypeName() + ")",
            List.of(backToSensorsBtn()), table);
    }

    private void dlgViewStats(Sensor s) {
        SensorStats st = s.computeStats();

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.getStyleClass().add("card-section");
        box.setMaxWidth(420);

        Label header = new Label(st.getSensorCode() + "  —  " + st.getSensorType());
        header.getStyleClass().add("stat-value-sm");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: 800;");
        box.getChildren().add(header);
        box.getChildren().add(new Separator());

        box.getChildren().addAll(
            statRow("Readings", String.valueOf(st.getCount())),
            statRow("Minimum",  String.valueOf(st.getMin())),
            statRow("Maximum",  String.valueOf(st.getMax())),
            statRow("Average",  String.format("%.3f", st.getAverage()))
        );

        StackPane wrapper = new StackPane(box);
        wrapper.setAlignment(Pos.TOP_LEFT);
        setView("Sensor Statistics", List.of(backToSensorsBtn()), wrapper);
    }

    private HBox statRow(String l, String v) {
        HBox h = new HBox(10);
        h.setPadding(new Insets(4, 0, 4, 0));
        Label a = new Label(l + ":"); a.getStyleClass().add("stat-label"); a.setMinWidth(120);
        Label b = new Label(v); b.getStyleClass().add("stat-value-sm");
        h.getChildren().addAll(a, b);
        return h;
    }

    private void dlgFilterReadings(Sensor s) {
        Dialog<ButtonType> d = newDialog("Filter Readings — " + s.getCode());
        GridPane g = newGrid();
        ComboBox<String> lvl = new ComboBox<>(FXCollections.observableArrayList("ANY","NORMAL","WARNING","CRITICAL"));
        lvl.setValue("ANY");
        DatePicker from = new DatePicker(); DatePicker to = new DatePicker();
        g.add(new Label("Level:"),  0, 0); g.add(lvl,  1, 0);
        g.add(new Label("From:"),   0, 1); g.add(from, 1, 1);
        g.add(new Label("To:"),     0, 2); g.add(to,   1, 2);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            ReadingStatus level = "ANY".equals(lvl.getValue()) ? null : ReadingStatus.valueOf(lvl.getValue());
            LocalDateTime f = from.getValue() == null ? null : from.getValue().atStartOfDay();
            LocalDateTime t = to.getValue()   == null ? null : to.getValue().atTime(23, 59);
            List<Reading> rs = s.filterReadings(level, f, t);
            TableView<Reading> table = new TableView<>(FXCollections.observableArrayList(rs));
            table.getColumns().addAll(
                col("DateTime", r -> r.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 160),
                col("Value",    r -> String.valueOf(r.getValue()), 90),
                col("Unit",     Reading::getUnit, 70),
                col("Status",   r -> String.valueOf(r.getStatus()), 100)
            );
            table.setPlaceholder(new Label("No matching readings."));
            setView("Filtered Readings — " + s.getCode() + " (" + rs.size() + ")",
                List.of(backToSensorsBtn()), table);
        });
    }

    private void doSensorStatus(String action, Sensor s) {
        switch (action) {
            case "suspend"   -> s.suspend();
            case "activate"  -> s.activate();
            case "defective" -> s.markDefective();
            case "faulty"    -> s.markFaulty();
        }
        setStatus(s.getCode() + " → " + s.getStatus());
        refresh();
    }

    // -------- Alerts views/dialogs --------
    private TableView<Alert> alertTable(List<Alert> alerts) {
        TableView<Alert> t = new TableView<>(FXCollections.observableArrayList(alerts));
        t.getColumns().addAll(
            col("ID",       Alert::getId,                                                         80),
            col("Sensor",   a -> a.getReading() == null || a.getReading().getSensor() == null
                                 ? "—" : a.getReading().getSensor().getCode(),               80),
            col("Zone",     a -> { Sensor s = a.getReading() == null ? null : a.getReading().getSensor();
                                   return s == null || s.getZone() == null ? "—" : s.getZone().getCode(); }, 80),
            col("Type",     a -> a.getReading() == null || a.getReading().getSensor() == null
                                 ? "—" : a.getReading().getSensor().getSensorTypeName(),    140),
            col("Value",    a -> {
                if (a.getReading() == null) return "—";
                Sensor s = a.getReading().getSensor();
                if (s instanceof GPSCollar gps)
                    return String.format("(%.2f, %.2f)", gps.getLatitude(), gps.getLongitude());
                return String.format("%.2f%s", a.getReading().getValue(), a.getReading().getUnit());
            }, 150),
            col("Severity", a -> String.valueOf(a.getSeverityLevel()),                           100),
            col("Status",   a -> String.valueOf(a.getStatus()),                                  110),
            col("Created",  a -> a.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 150)
        );
        t.setPlaceholder(new Label("No alerts."));
        t.setRowFactory(tv -> new TableRow<Alert>() {
            @Override protected void updateItem(Alert a, boolean empty) {
                super.updateItem(a, empty);
                getStyleClass().removeAll("row-critical", "row-warning", "row-dismissed");
                if (!empty && a != null) {
                    if (a.getStatus() == AlertStatus.DELETED) {
                        getStyleClass().add("row-dismissed");
                    } else if (a.getSeverityLevel() == SeverityLevel.CRITICAL) {
                        getStyleClass().add("row-critical");
                    } else {
                        getStyleClass().add("row-warning");
                    }
                }
            }
        });
        return t;
    }

    // -------- Alerts view --------
    private boolean alertsShowAll = false;
    private void viewAlerts() {
        currentRefresh = this::viewAlerts;
        List<Alert> data;
        if (alertsShowAll) {
            data = model.getAlerts().getAlerts();
        } else {
            data = new ArrayList<>();
            for (Alert a : model.getAlerts().getAlerts())
                if (a.getStatus() == AlertStatus.ACTIVE) data.add(a);
            data.sort(Comparator.comparing(Alert::getSeverityLevel).reversed());
        }
        TableView<Alert> table = alertTable(data);

        java.util.function.Supplier<Alert> sel = () -> {
            Alert a = table.getSelectionModel().getSelectedItem();
            if (a == null) warn("Select an alert in the table first.");
            return a;
        };

        ToggleButton active = new ToggleButton("Active");
        ToggleButton history = new ToggleButton("History");
        ToggleGroup tg = new ToggleGroup();
        active.setToggleGroup(tg); history.setToggleGroup(tg);
        active.getStyleClass().add("act-btn"); history.getStyleClass().add("act-btn");
        (alertsShowAll ? history : active).setSelected(true);
        active.setOnAction(e -> { alertsShowAll = false; viewAlerts(); });
        history.setOnAction(e -> { alertsShowAll = true; viewAlerts(); });

        MenuButton filterBtn = new MenuButton("Filter");
        filterBtn.getStyleClass().add("act-btn");
        filterBtn.getItems().addAll(
            menuItem("By Zone", this::dlgFilterAlertsByZone),
            menuItem("By Severity", this::dlgFilterAlertsBySeverity),
            menuItem("By Sensor Type", this::dlgFilterAlertsBySensorType),
            menuItem("By Period", this::dlgFilterAlertsByPeriod),
            menuItem("Combined", this::dlgFilterAlertsCombined));

        String title = (alertsShowAll ? "Alert History" : "Active Alerts") + " (" + data.size() + ")";
        setView(title,
            List.of(active, history, filterBtn, grow(),
                    actBtn("Acknowledge", () -> { Alert a = sel.get(); if (a != null) doAlertAction("ack", a); }),
                    actBtn("Dismiss", () -> { Alert a = sel.get(); if (a != null) doAlertAction("dismiss", a); })),
            table);
    }

    private void showAlertResults(String title, List<Alert> r) {
        setView(title, List.of(actBtn("← Back to Alerts", this::viewAlerts)), alertTable(r));
    }

    private void dlgFilterAlertsByZone() {
        GeographicalZone z = pick("Select Zone", model.zones(), x -> x.getCode() + " - " + x.getName());
        if (z == null) return;
        List<Alert> r = model.getAlerts().filterByZone(z.getCode());
        showAlertResults("Alerts in " + z.getCode() + " (" + r.size() + ")", r);
    }

    private void dlgFilterAlertsBySeverity() {
        SeverityLevel sv = pick("Severity", FXCollections.observableArrayList(SeverityLevel.values()), SeverityLevel::name);
        if (sv == null) return;
        List<Alert> r = model.getAlerts().filterBySeverity(sv);
        showAlertResults("Alerts " + sv + " (" + r.size() + ")", r);
    }

    private void dlgFilterAlertsBySensorType() {
        textPrompt("Sensor Type", "e.g. TEMPERATURE", "TEMPERATURE").ifPresent(t -> {
            List<Alert> r = model.getAlerts().filterBySensorType(t);
            showAlertResults("Alerts type=" + t + " (" + r.size() + ")", r);
        });
    }

    private void dlgFilterAlertsByPeriod() {
        Dialog<ButtonType> d = newDialog("Filter Alerts by Period");
        GridPane g = newGrid();
        DatePicker from = new DatePicker(); DatePicker to = new DatePicker();
        g.add(new Label("From:"), 0, 0); g.add(from, 1, 0);
        g.add(new Label("To:"),   0, 1); g.add(to,   1, 1);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            LocalDateTime f = from.getValue() == null ? null : from.getValue().atStartOfDay();
            LocalDateTime t = to.getValue()   == null ? null : to.getValue().atTime(23, 59);
            List<Alert> r = model.getAlerts().filterByPeriod(f, t);
            showAlertResults("Alerts in period (" + r.size() + ")", r);
        });
    }

    private void dlgFilterAlertsCombined() {
        Dialog<ButtonType> d = newDialog("Combined Alert Filter");
        GridPane g = newGrid();
        ComboBox<GeographicalZone> zb = new ComboBox<>(model.zones());
        zb.setConverter(toStringConv(z -> z.getCode() + " - " + z.getName()));
        TextField st = new TextField();
        ComboBox<SeverityLevel> sv = new ComboBox<>(FXCollections.observableArrayList(SeverityLevel.values()));
        DatePicker from = new DatePicker(); DatePicker to = new DatePicker();
        g.add(new Label("Zone (opt):"),        0, 0); g.add(zb,   1, 0);
        g.add(new Label("Sensor type (opt):"), 0, 1); g.add(st,   1, 1);
        g.add(new Label("Severity (opt):"),    0, 2); g.add(sv,   1, 2);
        g.add(new Label("From:"),              0, 3); g.add(from, 1, 3);
        g.add(new Label("To:"),                0, 4); g.add(to,   1, 4);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            String zc    = zb.getValue() == null     ? null : zb.getValue().getCode();
            String sType = st.getText().isBlank()    ? null : st.getText();
            SeverityLevel sev = sv.getValue();
            LocalDateTime f = from.getValue() == null ? null : from.getValue().atStartOfDay();
            LocalDateTime t = to.getValue()   == null ? null : to.getValue().atTime(23, 59);
            List<Alert> r = model.getAlerts().filter(zc, sType, sev, f, t);
            showAlertResults("Filtered Alerts (" + r.size() + ")", r);
        });
    }

    private void doAlertAction(String action, Alert a) {
        if ("ack".equals(action)) { a.acknowledge(); setStatus(a.getId() + " acknowledged"); }
        else { a.delete(); setStatus(a.getId() + " dismissed"); }
        refresh();
    }

    // -------- Zone dashboard view --------
    private GeographicalZone curDashZone;
    private void viewZoneDashboard() {
        currentRefresh = this::viewZoneDashboard;
        ObservableList<GeographicalZone> zones = model.zones();
        if (zones.isEmpty()) {
            setView("Zone Dashboard", List.of(), placeholderCard("No zones yet. Add a zone from the Zones view first."));
            return;
        }
        if (curDashZone == null || !zones.contains(curDashZone)) curDashZone = zones.get(0);
        GeographicalZone z = curDashZone;

        VBox box = new VBox(10);
        box.setPadding(new Insets(16));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label hdr = new Label(z.getName());
        hdr.getStyleClass().add("dash-header");
        Label codeL = new Label("[" + z.getCode() + "]");
        codeL.getStyleClass().add("muted");
        Label statusL = new Label(String.valueOf(z.getStatus()));
        statusL.getStyleClass().add(z.getStatus() == ZoneStatus.ACTIVE ? "dot-active" : "dot-inactive");
        header.getChildren().addAll(hdr, codeL, statusL);
        box.getChildren().add(header);
        box.getChildren().add(new Separator());

        boolean hasSensors = false;
        for (Sensor s : z.getSensors()) {
            if (s.getReadings().isEmpty()) continue;
            hasSensors = true;
            box.getChildren().addAll(sensorDashRow(s), new Separator());
        }

        if (!hasSensors) {
            Label none = new Label("No sensor readings in this zone.");
            none.getStyleClass().add("muted");
            box.getChildren().add(none);
        }

        ComboBox<GeographicalZone> zb = new ComboBox<>(zones);
        zb.setConverter(toStringConv(x -> x.getCode() + " - " + x.getName()));
        zb.setValue(z);
        zb.valueProperty().addListener((o, a, b) -> { if (b != null) { curDashZone = b; viewZoneDashboard(); } });

        setView("Zone Dashboard — " + z.getCode(),
            List.of(new Label("Zone:"), zb, grow(),
                    actBtn("Sensor Summaries", this::viewSensorSummaries)),
            new ScrollPane(box));
    }

    /** One sensor row in the dashboard style: code · type · last value · status badge. */
    private HBox sensorDashRow(Sensor s) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));

        Label code = new Label(s.getCode());
        code.getStyleClass().add("muted");
        code.setMinWidth(70);
        code.setFont(Font.font("Monospaced", 12));

        Label type = new Label(s.getSensorTypeName());
        type.getStyleClass().add("stat-value-sm");
        type.setMinWidth(180);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (s.getReadings().isEmpty()) {
            Label value = new Label("no readings");
            value.getStyleClass().add("muted");
            value.setMinWidth(80);
            Label status = new Label("  N/A");
            status.getStyleClass().addAll("muted", "severity-badge");
            row.getChildren().addAll(code, type, value, spacer, status);
            return row;
        }

        Reading last = s.getReadings().get(s.getReadings().size() - 1);
        String css = switch (last.getStatus()) {
            case CRITICAL -> "dash-critical";
            case WARNING  -> "dash-warning";
            default       -> "dash-normal";
        };
        String badge = switch (last.getStatus()) {
            case CRITICAL -> "  CRITICAL";
            case WARNING  -> "  WARNING";
            default       -> "  NORMAL";
        };

        Label value = new Label(last.getValue() + last.getUnit());
        value.setMinWidth(80);

        Label status = new Label(badge);
        status.getStyleClass().addAll(css, "severity-badge");

        row.getChildren().addAll(code, type, value, spacer, status);
        return row;
    }

    private void viewSensorSummaries() {
        currentRefresh = this::viewSensorSummaries;
        VBox box = new VBox(10);
        box.setPadding(new Insets(16));

        boolean any = false;
        for (GeographicalZone z : model.getFarm().getZones()) {
            if (z.getSensors().isEmpty()) continue;
            any = true;

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label hdr = new Label(z.getName());
            hdr.getStyleClass().add("dash-header");
            Label codeL = new Label("[" + z.getCode() + "]");
            codeL.getStyleClass().add("muted");
            Label statusL = new Label(String.valueOf(z.getStatus()));
            statusL.getStyleClass().add(z.getStatus() == ZoneStatus.ACTIVE ? "dot-active" : "dot-inactive");
            header.getChildren().addAll(hdr, codeL, statusL);
            box.getChildren().addAll(header, new Separator());

            for (Sensor s : z.getSensors())
                box.getChildren().addAll(sensorDashRow(s), new Separator());
        }

        if (!any) {
            Label none = new Label("No sensors.");
            none.getStyleClass().add("muted");
            box.getChildren().add(none);
        }

        setView("Sensor Summaries",
            List.of(actBtn("← Zone Dashboard", this::viewZoneDashboard)),
            new ScrollPane(box));
    }

    // -------- Production --------
    /** Production types that make sense for a given zone type. */
    private List<ProductionType> allowedProductionTypes(GeographicalZone z) {
        if (z instanceof CropZone)
            return List.of(ProductionType.CROP_YIELD, ProductionType.HARVEST_WEIGHT);
        if (z instanceof LivestockZone)
            return List.of(ProductionType.MILK, ProductionType.EGGS, ProductionType.HARVEST_WEIGHT);
        if (z instanceof AquacultureZone)
            return List.of(ProductionType.HARVEST_WEIGHT);
        return List.of(ProductionType.values());
    }

    /** Units that make sense for a given production type. */
    private List<UnitType> allowedUnits(ProductionType t) {
        return switch (t) {
            case MILK           -> List.of(UnitType.LITER);
            case EGGS           -> List.of(UnitType.UNIT);
            case HARVEST_WEIGHT -> List.of(UnitType.KG, UnitType.TON);
            case CROP_YIELD     -> List.of(UnitType.KG_PER_HECTARE, UnitType.TON, UnitType.KG);
        };
    }

    private void dlgAddProduction(GeographicalZone z) {
        List<ProductionType> types = allowedProductionTypes(z);
        Dialog<ButtonType> d = newDialog("Register Production — " + z.getCode());
        GridPane g = newGrid();
        DatePicker dp = new DatePicker(LocalDate.now());
        ComboBox<ProductionType> tp = new ComboBox<>(FXCollections.observableArrayList(types));
        tp.setValue(types.get(0));
        TextField v = new TextField("100");
        ComboBox<UnitType> u = new ComboBox<>(FXCollections.observableArrayList(allowedUnits(tp.getValue())));
        u.setValue(u.getItems().get(0));
        // keep units consistent with selected production type
        tp.valueProperty().addListener((o, a, b) -> {
            if (b == null) return;
            u.setItems(FXCollections.observableArrayList(allowedUnits(b)));
            u.setValue(u.getItems().get(0));
        });
        g.add(new Label("Zone type:"), 0, 0); g.add(new Label(z.getClass().getSimpleName().replace("Zone","")), 1, 0);
        g.add(new Label("Date:"),  0, 1); g.add(dp, 1, 1);
        g.add(new Label("Type:"),  0, 2); g.add(tp, 1, 2);
        g.add(new Label("Value:"), 0, 3); g.add(v,  1, 3);
        g.add(new Label("Unit:"),  0, 4); g.add(u,  1, 4);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                ProductionRecord r = new ProductionRecord(dp.getValue(), tp.getValue(),
                    Double.parseDouble(v.getText()), u.getValue());
                model.getFarm().registerProduction(z.getCode(), r);
                curProdZone = z;
                setStatus("Production registered for " + z.getCode());
                refresh();
            } catch (Exception ex) { warn("Invalid input."); }
        });
    }

    // -------- Production view --------
    private GeographicalZone curProdZone;
    private void viewProduction() {
        currentRefresh = this::viewProduction;
        ObservableList<GeographicalZone> zones = model.zones();
        if (zones.isEmpty()) {
            setView("Production", List.of(), placeholderCard("No zones yet. Add a zone from the Zones view first."));
            return;
        }
        if (curProdZone == null || !zones.contains(curProdZone)) curProdZone = zones.get(0);
        GeographicalZone z = curProdZone;

        TableView<ProductionRecord> table = new TableView<>(FXCollections.observableArrayList(z.getProductionHistory()));
        table.getColumns().addAll(
            col("Date",  r -> String.valueOf(r.getDate()),  120),
            col("Type",  r -> String.valueOf(r.getType()),  130),
            col("Value", r -> String.valueOf(r.getValue()),  90),
            col("Unit",  r -> String.valueOf(r.getUnit()),  100)
        );
        table.setPlaceholder(new Label("No production records in this zone."));

        ComboBox<GeographicalZone> zb = new ComboBox<>(zones);
        zb.setConverter(toStringConv(x -> x.getCode() + " - " + x.getName()));
        zb.setValue(z);
        zb.valueProperty().addListener((o, a, b) -> { if (b != null) { curProdZone = b; viewProduction(); } });

        setView("Production — " + z.getCode(),
            List.of(new Label("Zone:"), zb, grow(),
                    primBtn("+ Register Production", () -> dlgAddProduction(z))),
            table);
    }

    // -------- Help --------
    private void showAbout() {
        currentRefresh = this::showAbout;
        VBox v = new VBox(10);
        v.setPadding(new Insets(24));
        v.getStyleClass().add("card-section");
        v.setMaxWidth(560);

        Label name = new Label("Smart Farm Management System");
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #5ed47f;");

        Label sep = new Label("—");
        sep.getStyleClass().add("muted");

        for (String line : new String[]{
            "Object-Oriented Programming Project · 2025/2026",
            "Farm Entities (Zones, Animals, Crops, Aquaculture)",
            "Sensors & Alerts (Environmental, Soil, Water, Biometric, GPS)",
            "JavaFX GUI — no domain logic modified"
        }) {
            Label l = new Label(line);
            l.getStyleClass().add("muted");
            v.getChildren().add(l);
        }

        Separator sp2 = new Separator();
        VBox.setMargin(sp2, new Insets(8, 0, 8, 0));

        Label copyright = new Label("© Higher National School of Computer Science");
        copyright.setStyle("-fx-font-weight: 700; -fx-text-fill: #e6edf6;");
        Label madeBy = new Label("Made by");
        madeBy.getStyleClass().add("muted");
        Label a1 = new Label("ELFRAIHI Souhail Charaf eddine");
        a1.setStyle("-fx-font-weight: 700; -fx-text-fill: #5ed47f;");
        Label a2 = new Label("Hamouda Mohamed Hatem");
        a2.setStyle("-fx-font-weight: 700; -fx-text-fill: #5ed47f;");

        v.getChildren().addAll(0, List.of(name, sep));
        v.getChildren().addAll(sp2, copyright, madeBy, a1, a2);

        StackPane wrapper = new StackPane(v);
        wrapper.setAlignment(Pos.TOP_LEFT);
        setContent("About", wrapper);
    }

    private void showGuide() {
        TextArea ta = new TextArea("""
            QUICK GUIDE
            ─────────────────────────────────────────────────────
            Sidebar          Quick nav to any view
            File menu        Load demo data / Reset farm / Exit
            Farm menu        Overview card view / Rename farm
            Zones menu       Add / suspend / reactivate / set bounds
            Entities menu    Crops (Cereals/Vegetables/Fruits)
                             Animals (health, weight, GPS collar)
                             Aquaculture species
            Sensors menu     Add 5 sensor types, record readings,
                             view stats, filter readings, change status
            Alerts menu      Active list, full history, 5 filter modes,
                             acknowledge or dismiss
            Dashboard menu   Per-zone live sensor dashboard + summaries
            Production menu  Register and view production history

            Color coding in tables
            ─────────────────────────────────────────────────────
            Red row     = CRITICAL alert / defective sensor / sick animal
            Yellow row  = WARNING alert / faulty sensor
            Faded row   = dismissed alert / suspended sensor/zone

            All data is live against the real domain model.
            No domain logic was modified.
            """);
        ta.setEditable(false);
        ta.setFont(Font.font("Monospaced", 12));
        setContent("Quick Guide", ta);
    }

    // -------- Generic dialog helpers --------
    private Dialog<ButtonType> newDialog(String title) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(title); d.setHeaderText(null);
        d.initOwner(primaryStage);
        return d;
    }

    private GridPane newGrid() {
        GridPane g = new GridPane();
        g.setHgap(12); g.setVgap(10); g.setPadding(new Insets(16));
        return g;
    }

    private Optional<String> textPrompt(String title, String prompt, String defaultValue) {
        TextInputDialog d = new TextInputDialog(defaultValue);
        d.setTitle(title); d.setHeaderText(null); d.setContentText(prompt);
        d.initOwner(primaryStage);
        return d.showAndWait();
    }

    private <T> T pick(String title, ObservableList<T> options, java.util.function.Function<T,String> labeler) {
        if (options.isEmpty()) { warn("Nothing to select."); return null; }
        Dialog<T> d = new Dialog<>();
        d.setTitle(title); d.setHeaderText(null);
        d.initOwner(primaryStage);
        ComboBox<T> cb = new ComboBox<>(options);
        cb.setConverter(toStringConv(labeler));
        cb.setValue(options.get(0));
        cb.setPrefWidth(340);
        GridPane g = newGrid();
        g.add(new Label("Select:"), 0, 0);
        g.add(cb, 1, 0);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.setResultConverter(b -> b == ButtonType.OK ? cb.getValue() : null);
        return d.showAndWait().orElse(null);
    }

    private <T> StringConverter<T> toStringConv(java.util.function.Function<T,String> f) {
        return new StringConverter<T>() {
            @Override public String toString(T x) { return x == null ? "" : f.apply(x); }
            @Override public T fromString(String s) { return null; }
        };
    }

    private void warn(String msg) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.WARNING, msg);
        a.initOwner(primaryStage);
        a.showAndWait();
    }

    /** Red, attention-grabbing popup for CRITICAL readings. */
    private void criticalPopup(String header, String msg) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR, msg);
        a.setTitle("CRITICAL ALERT");
        a.setHeaderText("⚠  " + header);
        a.initOwner(primaryStage);
        var pane = a.getDialogPane();
        var cssUrl = getClass().getResource("/gui/styles.css");
        if (cssUrl != null) pane.getStylesheets().add(cssUrl.toExternalForm());
        pane.getStyleClass().add("critical-popup");
        a.showAndWait();
    }

    // -------- Seed sample data --------
    private void seedSampleData() {
        if (!model.getFarm().getZones().isEmpty()) return;
        Farm f = model.getFarm();
        AlertHistory ah = f.getAlertHistory();

        // ---- Zones ----
        CropZone        north   = new CropZone("North Fields");
        CropZone        orchard = new CropZone("West Orchard");
        LivestockZone   pasture = new LivestockZone("East Pasture", LivestockType.CATTLE);
        LivestockZone   barn    = new LivestockZone("Poultry Barn", LivestockType.CHICKEN);
        AquacultureZone pond    = new AquacultureZone("South Pond");
        f.addZone(north); f.addZone(orchard); f.addZone(pasture); f.addZone(barn); f.addZone(pond);

        north.setGeographicalLimits(36.70, 36.78, 3.10, 3.20);
        orchard.setGeographicalLimits(36.60, 36.68, 3.00, 3.10);
        pasture.setGeographicalLimits(36.80, 36.90, 3.20, 3.35);
        barn.setGeographicalLimits(36.75, 36.80, 3.18, 3.24);
        pond.setGeographicalLimits(36.50, 36.55, 2.95, 3.02);

        // ---- Crops ----
        north.addCrop(new Cereal(CerealType.WHEAT,  LocalDate.of(2025,11,1), LocalDate.of(2026,6,1),  GrowthStage.GROWTH));
        north.addCrop(new Cereal(CerealType.BARLEY, LocalDate.of(2025,11,15),LocalDate.of(2026,5,20), GrowthStage.GERMINATION));
        north.addCrop(new Cereal(CerealType.CORN,   LocalDate.of(2026,3,1),  LocalDate.of(2026,9,1),  GrowthStage.SEEDING));
        north.addCrop(new Vegetable(VegetableType.POTATO, LocalDate.of(2026,2,1), LocalDate.of(2026,6,15), GrowthStage.GROWTH));
        north.addCrop(new Vegetable(VegetableType.TOMATO, LocalDate.of(2026,3,10),LocalDate.of(2026,7,1),  GrowthStage.SEEDING));
        orchard.addCrop(new Fruit(FruitType.APPLE,  LocalDate.of(2023,3,1), LocalDate.of(2026,9,1),  GrowthStage.MATURITY));
        orchard.addCrop(new Fruit(FruitType.ORANGE, LocalDate.of(2022,2,1), LocalDate.of(2026,12,1), GrowthStage.MATURITY));
        orchard.addCrop(new Fruit(FruitType.GRAPE,  LocalDate.of(2024,4,1), LocalDate.of(2026,8,15), GrowthStage.GROWTH));

        // ---- Animals (cattle) ----
        String[] cowNames = {"Bella","Daisy","Luna","Maggie","Rosie"};
        double[] cowKg    = {520, 540, 498, 610, 475};
        int[]    cowAge   = {4, 6, 3, 7, 2};
        Animal[] cattle = new Animal[cowNames.length];
        for (int i = 0; i < cowNames.length; i++) {
            HealthStatus hs = i == 3 ? HealthStatus.SICK : i == 1 ? HealthStatus.QUARANTINE : HealthStatus.HEALTHY;
            cattle[i] = new Animal(cowNames[i], AnimalType.RUMINANT, cowAge[i], cowKg[i], hs);
            pasture.addAnimal(cattle[i]);
        }
        FeedingProgram cattleFeed = new FeedingProgram("Alfalfa hay", 12.5, 3);
        animalFeeding.put(cattle[0], cattleFeed);
        animalFeeding.put(cattle[2], new FeedingProgram("Silage + grain", 9.0, 2));

        // ---- Animals (poultry) ----
        for (int i = 1; i <= 6; i++) {
            HealthStatus hs = i == 5 ? HealthStatus.SICK : HealthStatus.HEALTHY;
            Animal hen = new Animal("Hen-" + i, AnimalType.POULTRY, 1, 2.4 + i * 0.1, hs);
            barn.addAnimal(hen);
            if (i == 1) animalFeeding.put(hen, new FeedingProgram("Layer mash", 0.13, 2));
        }

        // ---- Aquaculture species ----
        AquacultureSpecies tilapia = new AquacultureSpecies("Tilapia", AquacultureType.FISH, 500);
        AquacultureSpecies shrimp  = new AquacultureSpecies("Whiteleg Shrimp", AquacultureType.CRUSTACEAN, 350);
        tilapia.setFeedingProgram(new FeedingProgram("Floating pellets 32%", 4.0, 3));
        shrimp.setFeedingProgram(new FeedingProgram("Sinking pellets", 2.5, 4));
        pond.addSpecies(tilapia);
        pond.addSpecies(shrimp);

        // ---- Sensors: North Fields ----
        EnvironmentalSensor nTemp = new EnvironmentalSensor(north, 5, 35, EnvironmentalSensorType.TEMPERATURE, "°C");
        EnvironmentalSensor nHum  = new EnvironmentalSensor(north, 30, 80, EnvironmentalSensorType.HUMIDITY, "%");
        SoilSensor          nPh   = new SoilSensor(north, 5.5, 7.5, SoilSensorType.PH, "pH");
        SoilSensor          nN    = new SoilSensor(north, 20, 60, SoilSensorType.NITROGEN, "mg/kg");
        north.addSensor(nTemp); north.addSensor(nHum); north.addSensor(nPh); north.addSensor(nN);

        // ---- Sensors: Orchard ----
        EnvironmentalSensor oTemp = new EnvironmentalSensor(orchard, 5, 38, EnvironmentalSensorType.TEMPERATURE, "°C");
        SoilSensor          oHum  = new SoilSensor(orchard, 25, 70, SoilSensorType.HUMIDITY, "%");
        orchard.addSensor(oTemp); orchard.addSensor(oHum);

        // ---- Sensors: Pasture (biometric + GPS) ----
        BiometricSensor bTemp = new BiometricSensor(pasture, 38.0, 39.5, BiometricSensorType.BODY_TEMPERATURE, cattle[0], "°C");
        BiometricSensor bAct  = new BiometricSensor(pasture, 20, 80, BiometricSensorType.ACTIVITY_LEVEL, cattle[3], "%");
        pasture.addSensor(bTemp); pasture.addSensor(bAct);

        GPSCollar gps0 = new GPSCollar(pasture, cattle[0], 36.85, 3.27);
        GPSCollar gps1 = new GPSCollar(pasture, cattle[2], 36.84, 3.26);
        cattle[0].assignGPSCollar(gps0); cattle[2].assignGPSCollar(gps1);
        pasture.addSensor(gps0); pasture.addSensor(gps1);
        gpsRanges.put(gps0, new double[]{ 36.80, 36.90, 3.20, 3.35 });
        gpsRanges.put(gps1, new double[]{ 36.80, 36.90, 3.20, 3.35 });

        // ---- Sensors: Barn ----
        EnvironmentalSensor barnTemp = new EnvironmentalSensor(barn, 18, 28, EnvironmentalSensorType.TEMPERATURE, "°C");
        barn.addSensor(barnTemp);

        // ---- Sensors: Pond ----
        WaterSensor wTemp = new WaterSensor(pond, 18, 30, WaterSensorType.TEMPERATURE, "°C");
        WaterSensor wO2   = new WaterSensor(pond, 5, 12, WaterSensorType.DISSOLVED_OXYGEN, "mg/L");
        WaterSensor wPh   = new WaterSensor(pond, 6.5, 8.5, WaterSensorType.PH, "pH");
        pond.addSensor(wTemp); pond.addSensor(wO2); pond.addSensor(wPh);

        // ---- Readings (mix of normal + a few out-of-range → alerts) ----
        LocalDateTime t = LocalDateTime.of(2026, 6, 1, 8, 0);
        seedReadings(nTemp, ah, t, 18, 21, 24, 33, 41);          // 41 critical
        seedReadings(nHum,  ah, t, 55, 60, 48, 72, 64);
        seedReadings(nPh,   ah, t, 6.6, 6.9, 7.1, 5.2, 6.8);     // 5.2 warn/critical
        seedReadings(nN,    ah, t, 35, 42, 38, 15, 50);          // 15 low
        seedReadings(oTemp, ah, t, 20, 23, 27, 31, 36);
        seedReadings(oHum,  ah, t, 40, 38, 35, 28, 33);          // 28 low
        seedReadings(bTemp, ah, t, 38.4, 38.7, 39.1, 40.6, 38.9);// 40.6 critical
        seedReadings(bAct,  ah, t, 55, 60, 45, 12, 50);          // 12 low activity
        seedReadings(barnTemp, ah, t, 22, 24, 26, 30, 23);       // 30 high
        seedReadings(wTemp, ah, t, 24, 25, 27, 31, 26);          // 31 high
        seedReadings(wO2,   ah, t, 8, 7.5, 6.8, 3.9, 7.2);       // 3.9 critical low
        seedReadings(wPh,   ah, t, 7.2, 7.5, 7.8, 8.9, 7.4);     // 8.9 high

        // ---- GPS readings (one inside, one geofence breach) ----
        seedGpsReading(gps0, ah, 36.86, 3.28);  // inside → normal
        seedGpsReading(gps1, ah, 36.95, 3.40);  // outside → alert

        // ---- Production history ----
        f.registerProduction(pasture.getCode(), new ProductionRecord(LocalDate.of(2026,5,10), ProductionType.MILK, 320, UnitType.LITER));
        f.registerProduction(pasture.getCode(), new ProductionRecord(LocalDate.of(2026,5,20), ProductionType.MILK, 298, UnitType.LITER));
        f.registerProduction(pasture.getCode(), new ProductionRecord(LocalDate.of(2026,5,30), ProductionType.MILK, 341, UnitType.LITER));
        f.registerProduction(barn.getCode(),    new ProductionRecord(LocalDate.of(2026,5,28), ProductionType.EGGS, 540, UnitType.UNIT));
        f.registerProduction(barn.getCode(),    new ProductionRecord(LocalDate.of(2026,6,4),  ProductionType.EGGS, 562, UnitType.UNIT));
        f.registerProduction(north.getCode(),   new ProductionRecord(LocalDate.of(2025,8,1),  ProductionType.CROP_YIELD, 4.2, UnitType.TON));
        f.registerProduction(orchard.getCode(), new ProductionRecord(LocalDate.of(2025,9,15), ProductionType.HARVEST_WEIGHT, 1.8, UnitType.TON));
        f.registerProduction(pond.getCode(),    new ProductionRecord(LocalDate.of(2026,4,1),  ProductionType.HARVEST_WEIGHT, 220, UnitType.KG));
    }

    /** Record a series of readings on consecutive days for a sensor. */
    private void seedReadings(Sensor s, AlertHistory ah, LocalDateTime start, double... values) {
        for (int i = 0; i < values.length; i++)
            s.recordReading(values[i], start.plusDays(i), ah);
    }

    /** Record a GPS position reading through the geofence pipeline (same logic as the dialog). */
    private void seedGpsReading(GPSCollar gps, AlertHistory ah, double lat, double lon) {
        double[] box = gpsRanges.get(gps);
        if (box == null) return;
        gps.updatePosition(lat, lon);
        double dLat = Math.max(0, Math.max(box[0] - lat, lat - box[1]));
        double dLon = Math.max(0, Math.max(box[2] - lon, lon - box[3]));
        double overshoot = Math.max(dLat, dLon);
        gps.recordReading(Math.round(overshoot * 10000.0) / 10000.0, LocalDateTime.now(), ah);
    }
}
