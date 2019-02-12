package fr.ifremer.octopus.view;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.Callback;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * @author altran
 *
 */
public class LoggerConsoleControllerLight {
    static final Logger LOGGER = LogManager.getLogger(LoggerConsoleControllerLight.class.getName());
 
    @FXML
    private ListView<String> listViewLog;


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
    void handleScrollToEndLog(){
    	listViewLog.scrollTo(listViewLog.getItems().size() - 1);
        listViewLog.getSelectionModel().select(listViewLog.getItems().size() - 1);
    }
    
    @FXML
    void initialize() {
		TextAreaAppender.setListView(listViewLog);
		
		 listViewLog.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
	        Configuration loggerConfiguration = loggerContext.getConfiguration();
	        LoggerConfig loggerConfig = loggerConfiguration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
	        /* ChoiceBox füllen */
	        for (Level level : Level.values()) {
	        	
	        	// OFF is unnecessary
	            if (level != Level.OFF) {
					choiceBoxLogLevel.getItems().add(level);
				}
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
    }
}
