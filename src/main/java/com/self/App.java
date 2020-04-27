package com.self;

import com.self.model.Item;
import com.self.util.ExcelUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * JavaFX App
 */
public class App extends Application {

    private final StringBuilder todayReportPath = new StringBuilder("/home/name/桌面/mama/入库.xlsx");
    private final StringBuilder myReportPath = new StringBuilder("/home/name/桌面/mama/新档案2019.11.27.xlsx");

    private Label countNameLabel = new Label();

    private DatePicker startDatePicker = new DatePicker();
    private DatePicker endDatePicker = new DatePicker();
    private ListView<Item> listView = null;

//    private ObservableList<Item> data = FXCollections.observableArrayList();
//    private HashMap<String, ArrayList<Integer>> compAndPosMap = new HashMap<>();
//    // 若合并过公司，则将它置为true
//    private boolean dataChanged = false;

    private Map<String, List<Item>> data = new HashMap<>();

    @Override
    public void start(Stage stage) {

        final Label docNameLabel = new Label();
        docNameLabel.setFont(new Font(20));
        docNameLabel.setText("我的档案路径：");

        final Label docPathLabel = new Label();
        docPathLabel.setFont(new Font(20));
        docPathLabel.setText(myReportPath.toString());

        Button docButton = new Button("打开我的档案");
        docButton.setFont(new Font(20));
        docButton.setOnAction(arg0 -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);

            if (file == null) {
                return;
            }

            changeDocPath(file.getAbsolutePath());
            docPathLabel.setText(myReportPath.toString());
        });

        final Label reportNameLabel = new Label();
        reportNameLabel.setFont(new Font(20));
        reportNameLabel.setText("报表路径：");

        final Label reportPathLabel = new Label();
        reportPathLabel.setFont(new Font(20));
        reportPathLabel.setText(todayReportPath.toString());

        Button reportButton = new Button("打开报表");
        reportButton.setFont(new Font(20));
        reportButton.setOnAction(arg0 -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);

            if (file == null) {
                return;
            }

            changeReportPath(file.getAbsolutePath());
            reportPathLabel.setText(todayReportPath.toString());
        });

        final Label startLabel = new Label();
        startLabel.setFont(new Font(20));
        startLabel.setText("报表开始日期");

        startDatePicker.setValue(LocalDate.of(2020, 1, 1));
        startDatePicker.getEditor().setFont(new Font(20));

        final Label endLabel = new Label();
        endLabel.setFont(new Font(20));
        endLabel.setText("报表结束日期");

        endDatePicker.setValue(LocalDate.now());
        endDatePicker.getEditor().setFont(new Font(20));


//        Button editListButton = new Button("编辑列表");
//        editListButton.setFont(new Font(20));
//        editListButton.setOnAction(arg0 -> {
//
//            ObservableList<String> companyList = FXCollections.observableArrayList();
//            companyList.addAll(compAndPosMap.keySet());
//
//            Callback<ListView<String>, ListCell<String>> comboStyleCallback = new Callback<>() {
//                @Override
//                public ListCell<String> call(ListView<String> stringListView) {
//                    ListCell<String> cell = new ListCell<>() {
//                        @Override
//                        protected void updateItem(String s, boolean b) {
//                            super.updateItem(s, b);
//                            setText(s);
//                            setEditable(false);
//                        }
//                    };
//                    cell.setFont(new Font(20));
//                    return cell;
//                }
//            };
//
//            ComboBox<String> beforeCombo = new ComboBox<>(companyList);
//            beforeCombo.setCellFactory(comboStyleCallback);
//            beforeCombo.setPadding(new Insets(5));
//
//            Label label = new Label("合并到");
//            label.setFont(new Font(30));
//
//            ComboBox<String> afterCombo = new ComboBox<>(companyList);
//            afterCombo.setCellFactory(comboStyleCallback);
//            afterCombo.setPadding(new Insets(5));
//
//            Button mergeButton = new Button("合并");
//            mergeButton.setFont(new Font(30));
//            mergeButton.setOnAction(actionEvent -> {
//                String beforeComp = beforeCombo.getValue();
//                String afterComp = afterCombo.getValue();
//                if (beforeCombo != null && afterCombo != null) {
//                    showMergeCompany(beforeComp, afterComp, companyList);
//                    showSuccessDialog("合并成功", beforeComp + " 已合并到 " + afterComp);
//                }
//            });
//
//            // 创建新的stage
//            Stage editStage = new Stage();
//            VBox vBox = new VBox();
//            vBox.getChildren().addAll(beforeCombo, label, afterCombo, mergeButton);
//
//            editStage.setScene(new Scene(vBox, 300, 200));
//            editStage.show();
//        });

        listView = new ListView<>();
        listView.setPrefSize(900, 400);
        listView.setEditable(false);
        listView.setCellFactory(itemListView -> {
            return new ListCell<Item>() {
                @Override
                public void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    HBox hBox = new HBox();
                    hBox.setSpacing(20);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    if (!empty) {
                        int prefWidth = 200;
                        Label itemNameLabel = new Label(item.getName());
                        itemNameLabel.setFont(new Font(20));
                        itemNameLabel.setPrefWidth(prefWidth);
                        Label itemSpecLabel = new Label(item.getSpec());
                        itemSpecLabel.setFont(new Font(20));
                        itemSpecLabel.setPrefWidth(150);
                        Label itemToStorageLabel = new Label("" + item.getToStorage());
                        itemToStorageLabel.setFont(new Font(20));
                        itemToStorageLabel.setPrefWidth(100);
                        Label itemInDateLabel = new Label("" + item.getInDate(true));
                        itemInDateLabel.setFont(new Font(20));
                        itemInDateLabel.setPrefWidth(100);
                        Label companyLabel = new Label(item.getCompany());
                        companyLabel.setFont(new Font(20));
                        companyLabel.setPrefWidth(300);

                        hBox.getChildren().addAll(itemNameLabel, itemSpecLabel,
                                itemToStorageLabel, itemInDateLabel, companyLabel);
                        setGraphic(hBox);
                    }
                }
            };
        });


        Button showListButton = new Button("查看所有货物");
        showListButton.setFont(new Font(20));
        showListButton.setOnAction(arg0 -> {
            try {
                importDataFromExcel();
                ObservableList<Item> tempList = generateTempListFromMap();
                listView.setItems(tempList);
                countNameLabel.setText("货物总数:" + tempList.size());
            } catch (Exception e) {
                showErrorDialog("错误", "出现错误" + e.getMessage());
            }
        });

        countNameLabel.setFont(new Font(20));


        Button addButton = new Button();
        addButton.setFont(new Font(20));
        addButton.setText("加入我的档案");
//        final ObservableList<Item> tempList = generateTempList(data);
        addButton.setOnAction(event -> {
            ObservableList<Item> tempList = generateTempListFromMap();
            ArrayList<String> differenceInCompanies = null;
            try {
                differenceInCompanies = ExcelUtil.getDifferenceInCompanies(myReportPath.toString(), data.keySet());
                if (differenceInCompanies.size() != 0) {
                    showMergeCompanyWindow(differenceInCompanies);
                } else {
                    persistDataWithConfirm();
                }

            } catch (IOException | GeneralSecurityException e) {
                showErrorDialog("打开报表失败", "打开报表失败，请尝试重新打开报表");
            }

        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        gridPane.add(docNameLabel, 0, 0);
        gridPane.add(docPathLabel, 1, 0);
        gridPane.add(docButton, 2, 0);

        gridPane.add(reportNameLabel, 0, 1);
        gridPane.add(reportPathLabel, 1, 1);
        gridPane.add(reportButton, 2, 1);

        gridPane.add(startLabel, 0, 2);
        gridPane.add(startDatePicker, 1, 2);

        gridPane.add(endLabel, 0, 3);
        gridPane.add(endDatePicker, 1, 3);

//        gridPane.add(editListButton, 1, 4);
        gridPane.add(showListButton, 2, 4);

        gridPane.add(listView, 0, 5, 3, 1);

        gridPane.add(countNameLabel, 0, 6);
        gridPane.add(addButton, 2, 6);

        // 根容器
        Group group = new Group();

        group.getChildren().addAll(gridPane);

        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(700);

        // 退出时关闭所有窗口
        stage.setOnCloseRequest(event -> Platform.exit());

        stage.show();
    }

    private ObservableList<Item> generateTempListFromMap() {
        ObservableList list = FXCollections.observableArrayList();
        for (Map.Entry<String, List<Item>> e :
                data.entrySet()) {
            list.addAll(e.getValue());
        }
        return list;
    }

    private void showMergeCompanyWindow(ArrayList<String> differenceInCompanies) throws IOException, GeneralSecurityException {
        ObservableList<String> targetCompanyList = FXCollections.observableList(ExcelUtil.getCompanyList(myReportPath.toString()));

        Stage editStage = new Stage();
        editStage.setTitle("发现找不到表的公司，改正一下吧");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);

        ArrayList<String> changeList = new ArrayList<>(differenceInCompanies);

        ListView<String> diffListView = new ListView<>(FXCollections.observableList(differenceInCompanies));
        diffListView.setPrefSize(900, 600);
        diffListView.setEditable(false);
        diffListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {

                ListCell<String> cell = new ListCell<>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        int index = differenceInCompanies.indexOf(item);
                        HBox hBox = new HBox();
                        hBox.setSpacing(20);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        if (!empty) {
                            Label companyLabel = new Label(item);
                            companyLabel.setFont(new Font(20));
                            companyLabel.setPrefWidth(300);

                            ComboBox<String> afterCombo = new ComboBox<>(targetCompanyList);
                            afterCombo.setCellFactory(stringListView -> {
                                ListCell<String> cell = new ListCell<>() {
                                    @Override
                                    protected void updateItem(String s, boolean b) {
                                        super.updateItem(s, b);
                                        setText(s);
                                        setEditable(false);
                                    }
                                };
                                cell.setFont(new Font(20));
                                return cell;
                            });
                            afterCombo.setPadding(new Insets(5));
                            afterCombo.valueProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                                    changeList.set(index, t1);
                                    System.out.println(index);
                                }
                            });
                            hBox.getChildren().addAll(companyLabel, afterCombo);
                            setGraphic(hBox);
                        }
                    }
                };
                cell.setFont(new Font(20));

                return cell;
            }
        });

//        diffListView.setCellFactory(itemListView -> new ListCell<>() {
//
//            @Override
//            public void updateItem(String item, boolean empty) {
//                HBox hBox = new HBox();
//                hBox.setSpacing(20);
//                if (!empty) {
//                    Label companyLabel = new Label(item);
//                    companyLabel.setFont(new Font(20));
//                    companyLabel.setPrefWidth(300);
//                    hBox.getChildren().addAll(companyLabel);
//                    setGraphic(hBox);
//                }
//            }
//
//            @Override
//            public void updateSelected(boolean selected) {
//                super.updateSelected(selected);
//
//            }
//        });

        Button confirmButton = new Button("确定");
        confirmButton.setFont(new Font(20));
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                for (int i = 0; i < changeList.size(); i++) {
                    mergeCompany(differenceInCompanies.get(i), changeList.get(i));
                }
                // 更新主界面上的数据
                listView.setItems(generateTempListFromMap());
                // 将数据添加到excel
                persistDataWithConfirm();
                editStage.close();
            }
        });

        vBox.getChildren().add(0, diffListView);
        vBox.getChildren().add(1, confirmButton);

        editStage.setScene(new Scene(vBox, 900, 600));
        editStage.show();
    }

    private void persistDataWithConfirm(){
        boolean b = showConfirmDialog("确定吗？");
        if(b){
            persistData();
        }
    }


    private void persistData() {
        try {
            ObservableList<Item> items = generateTempListFromMap();
            ExcelUtil.backupFile(myReportPath.toString());
            ExcelUtil.persistDataToExcel(myReportPath.toString(), items);
            showSuccessDialog("写入成功", "写入成功，备份文件未删除");
        } catch (IOException e) {
            showErrorDialog("写入错误", "写入错误，原文件已备份" + e.getLocalizedMessage());
        }
    }

    private boolean showConfirmDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("请确认");
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void mergeCompany(String beforeComp, String afterComp) {
        System.out.println(afterComp);
        List<Item> before = data.get(beforeComp);
        before.forEach(item -> {
            item.setCompany(afterComp);
        });
        List<Item> after = data.get(afterComp);
        if (after == null) {
            data.put(afterComp, before);
        } else {
            after.addAll(before);
        }
        data.remove(beforeComp);
    }

    private void showErrorDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("错误");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    private void showSuccessDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("成功");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    private void importDataFromExcel() throws IOException, GeneralSecurityException {
        data.clear();
        data = ExcelUtil.getReportContentMap(todayReportPath.toString(),
                getDateFromLocalDate(startDatePicker.getValue().minusDays(1)),
                getDateFromLocalDate(endDatePicker.getValue().plusDays(1))
        );

//        data.removeIf(item -> true);
//        data.addAll(
//                ExcelUtil.getReportContent(todayReportPath.toString(),
//                        getDateFromLocalDate(startDatePicker.getValue().minusDays(1)),
//                        getDateFromLocalDate(endDatePicker.getValue().plusDays(1))
//                )
//        );
//
//        // 记录所有公司名和相应记录位置
//        for (int i = data.size() - 1; i >= 0; i--) {
//            ArrayList<Integer> integers = compAndPosMap.computeIfAbsent(data.get(i).getCompany(), k -> new ArrayList<>());
//            integers.add(i);
//        }
//
//        dataChanged = false;
    }

    private void changeReportPath(String absolutePath) {
        todayReportPath.replace(0, todayReportPath.length(), absolutePath);
    }

    private void changeDocPath(String absolutePath) {
        myReportPath.replace(0, myReportPath.length(), absolutePath);
        System.out.println(myReportPath.toString());
    }

    private Date getDateFromLocalDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);

        return Date.from(zdt.toInstant());
    }

    public static void main(String[] args) {
        launch();
    }

}
