package com.self;

import com.self.model.Item;
import com.self.util.ExcelUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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

    private ObservableList<Item> data = FXCollections.observableArrayList();

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

        Button showListButton = new Button("查看所有货物");
        showListButton.setFont(new Font(20));
        showListButton.setOnAction(arg0 -> {
            try {
                changeItemList();
                countNameLabel.setText("货物总数:" + data.size());
            } catch (Exception e) {
                showErrorDialog("错误", "出现错误" + e.getMessage());
            }
        });

        Button editListButton = new Button("编辑列表");
        editListButton.setFont(new Font(20));
        editListButton.setOnAction(arg0 -> {
            // 创建新的stage
            Stage editStage = new Stage();
            GridPane editGridPane = new GridPane();
            ListView<Item> companyList = new ListView<>();
            companyList.setPrefSize(700, 600);
            companyList.setEditable(true);
            companyList.setCellFactory(itemListView -> {
                ListCell<Item> cell = new ListCell<Item>() {
                    @Override
                    public void updateItem(Item item, boolean empty) {
                        HBox hBox = new HBox();
                        hBox.setSpacing(20);
                        if (!empty) {
                            Label companyLabel = new Label(item.getCompany());
                            companyLabel.setFont(new Font(20));
                            hBox.getChildren().addAll(companyLabel);
                            setGraphic(hBox);
                        }
                    }
                };
                return cell;
            });
            editGridPane.add(companyList, 0,0);
            editStage.setScene(new Scene(editGridPane, 700, 800));
            editStage.show();
        });

        ListView<Item> listView = new ListView<>(data);
        listView.setPrefSize(700, 600);
        listView.setEditable(false);
        listView.setCellFactory(itemListView -> {
            ListCell<Item> cell = new ListCell<Item>() {
                @Override
                public void updateItem(Item item, boolean empty) {
                    HBox hBox = new HBox();
                    hBox.setSpacing(20);
                    if (!empty) {
                        Label itemNameLabel = new Label(item.getName());
                        itemNameLabel.setFont(new Font(20));
                        Label itemSpecLabel = new Label(item.getSpec());
                        itemSpecLabel.setFont(new Font(20));
                        Label itemToStorageLabel = new Label("" + item.getToStorage());
                        itemToStorageLabel.setFont(new Font(20));
                        Label itemInDateLabel = new Label("" + item.getInDate(true));
                        itemInDateLabel.setFont(new Font(20));
                        Label companyLabel = new Label(item.getCompany());
                        companyLabel.setFont(new Font(20));
                        hBox.getChildren().addAll(itemNameLabel, itemSpecLabel,
                                itemToStorageLabel, itemInDateLabel, companyLabel);
                        setGraphic(hBox);
                    }
                }
            };
            return cell;
        });

        countNameLabel.setFont(new Font(20));
        countNameLabel.setText("货物总数:" + data.size());


        Button addButton = new Button();
        addButton.setFont(new Font(20));
        addButton.setText("加入我的档案");
        final ObservableList<Item> tempList = data;
        addButton.setOnAction(event -> {
            try {
                ExcelUtil.backupFile(myReportPath.toString());
                ExcelUtil.persistDataToExcel(myReportPath.toString(), tempList);
//                boolean b = ExcelUtil.deleteFile(backupFilePath);
                showSuccessDialog("写入成功","写入成功，备份文件未删除");
            } catch (IOException e) {
                showErrorDialog("写入错误", "写入错误，原文件已备份" + e.getLocalizedMessage());
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

        gridPane.add(editListButton, 1, 4);
        gridPane.add(showListButton, 2, 4);

        gridPane.add(listView, 0, 5, 3, 1);

        gridPane.add(countNameLabel, 0, 6);
        gridPane.add(addButton, 2, 6);

        // 根容器
        Group group = new Group();

        group.getChildren().addAll(gridPane);

        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setMinWidth(725);
        stage.setMinHeight(900);

        // 退出时关闭所有窗口
        stage.setOnCloseRequest(event -> Platform.exit());

        stage.show();
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

    private void changeItemList() throws IOException, GeneralSecurityException {
        data.removeIf(item -> true);
        data.addAll(
                ExcelUtil.getReportContent(todayReportPath.toString(),
                        getDateFromLocalDate(startDatePicker.getValue().minusDays(1)),
                        getDateFromLocalDate(endDatePicker.getValue().plusDays(1))
                )
        );
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
