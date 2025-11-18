package com.tinusj.mcconfigurator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainWindowController {

    private final ConfigFileService configFileService;

    @Getter
    private final BorderPane root = new BorderPane();

    private final Label currentFolderLabel = new Label();
    private final Button chooseFolderButton = new Button("Choose Folder");
    private final Button refreshButton = new Button("Refresh");

    private final ListView<ParsedConfig> configListView = new ListView<>();
    private final TreeView<ConfigNode> configTreeView = new TreeView<>();

    private Path currentFolderPath;

    public MainWindowController(@Value("${app.config.default-folder:./configs}") String defaultFolder,
                                ConfigFileService configFileService) {
        this.configFileService = configFileService;
        this.currentFolderPath = Paths.get(defaultFolder);
        initializeUi();
        loadConfigs();
    }

    private void initializeUi() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        currentFolderLabel.setText("Folder: " + currentFolderPath.toAbsolutePath());
        topBar.getChildren().addAll(chooseFolderButton, refreshButton, currentFolderLabel);

        chooseFolderButton.setOnAction(event -> handleChooseFolder());
        refreshButton.setOnAction(event -> loadConfigs());

        configListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ParsedConfig item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    StringBuilder text = new StringBuilder(item.getFileName())
                            .append(" (")
                            .append(item.getConfigType())
                            .append(")");
                    if (item.hasError()) {
                        text.append(" - ERROR");
                    }
                    setText(text.toString());
                }
            }
        });

        configListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showConfig(newVal)
        );

        configTreeView.setShowRoot(false);
        configTreeView.setCellFactory(treeView -> new ConfigTreeCell());

        SplitPane centerPane = new SplitPane(configListView, configTreeView);
        centerPane.setDividerPositions(0.3);

        root.setTop(topBar);
        root.setCenter(centerPane);
    }

    private void handleChooseFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Config Folder");
        chooser.setInitialDirectory(currentFolderPath.toFile());

        Window window = root.getScene() != null ? root.getScene().getWindow() : null;
        Path selected = null;
        if (window != null) {
            var file = chooser.showDialog(window);
            if (file != null && file.isDirectory()) {
                selected = file.toPath();
            }
        }
        if (selected != null) {
            currentFolderPath = selected;
            currentFolderLabel.setText("Folder: " + currentFolderPath.toAbsolutePath());
            loadConfigs();
        }
    }

    private void loadConfigs() {
        List<ParsedConfig> configs = configFileService.loadConfigs(currentFolderPath);
        configListView.getItems().setAll(configs);
        configTreeView.setRoot(null);
    }

    private void showConfig(ParsedConfig parsedConfig) {
        if (parsedConfig == null) {
            configTreeView.setRoot(null);
            return;
        }

        if (parsedConfig.hasError()) {
            ConfigNode errorNode = ConfigNode.leaf("Error", parsedConfig.getErrorMessage());
            TreeItem<ConfigNode> rootItem = new TreeItem<>(errorNode);
            configTreeView.setRoot(rootItem);
            configTreeView.setShowRoot(true);
            return;
        }

        ConfigNode rootNode = parsedConfig.getRootNode();
        if (rootNode == null) {
            configTreeView.setRoot(null);
            return;
        }

        TreeItem<ConfigNode> rootItem = createTreeItem(rootNode);
        configTreeView.setRoot(rootItem);
        configTreeView.setShowRoot(false);
        rootItem.setExpanded(true);
    }

    private TreeItem<ConfigNode> createTreeItem(ConfigNode node) {
        TreeItem<ConfigNode> item = new TreeItem<>(node);
        if (!node.isLeaf()) {
            for (ConfigNode child : node.getChildren()) {
                item.getChildren().add(createTreeItem(child));
            }
        }
        return item;
    }

    private static class ConfigTreeCell extends javafx.scene.control.TreeCell<ConfigNode> {

        @Override
        protected void updateItem(ConfigNode item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else if (item.isLeaf()) {
                setText(item.getName() + " = " + String.valueOf(item.getValue()));
            } else {
                setText(item.getName());
            }
        }
    }
}