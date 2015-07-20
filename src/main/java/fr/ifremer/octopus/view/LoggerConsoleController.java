package fr.ifremer.octopus.view;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleButton;
import javafx.util.Callback;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * From "append log4j2 output to TextArea", Stackoverflow:
 * http://stackoverflow.com/questions/21475576/append-log4j2-output-to-textarea
 * @author altran
 *
 */
public class LoggerConsoleController {
    static final Logger logger = LogManager.getLogger(LoggerConsoleController.class.getName());

    @FXML
    private ListView<String> listViewLog;

    @FXML
    private ToggleButton toggleButtonAutoScroll;

    @FXML
    private ChoiceBox<Level> choiceBoxLogLevel;

    @FXML
    void handleRemoveSelected() {
        listViewLog.getItems().removeAll(listViewLog.getSelectionModel().getSelectedItems());
    }

    @FXML
    void handleClearLog() {
        listViewLog.getItems().clear();
    }

    @FXML
    void initialize() {
        listViewLog.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration loggerConfiguration = loggerContext.getConfiguration();
        LoggerConfig loggerConfig = loggerConfiguration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        /* ChoiceBox füllen */
        for (Level level : Level.values()) {
            choiceBoxLogLevel.getItems().add(level);
        }
        /* Aktuellen LogLevel in der ChoiceBox als Auswahl setzen */
        choiceBoxLogLevel.getSelectionModel().select(loggerConfig.getLevel());
        choiceBoxLogLevel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Level>() {
            @Override
            public void changed(ObservableValue<? extends Level> arg0, Level oldLevel, Level newLevel) {
                loggerConfig.setLevel(newLevel);
                loggerContext.updateLoggers(); // übernehme aktuellen LogLevel
            }
        });

        listViewLog.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new LogStringCell();
            }
        });

        /* den Origial System.out Stream in die ListView umleiten */
        PipedOutputStream pOut = new PipedOutputStream();
        System.setOut(new PrintStream(pOut));
        PipedInputStream pIn = null;
        try {
            pIn = new PipedInputStream(pOut);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(pIn));

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    try {
                        String line = reader.readLine();
                        if (line != null) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    listViewLog.getItems().add(line);

                                    /* Auto-Scroll + Select */
                                    if (toggleButtonAutoScroll.selectedProperty().get()) {
                                        listViewLog.scrollTo(listViewLog.getItems().size() - 1);
                                        listViewLog.getSelectionModel().select(listViewLog.getItems().size() - 1);
                                    }
                                }
                            });
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
