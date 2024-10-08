package fr.ifremer.octopus.view;

import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

public class LogStringCell extends ListCell<String> {

    @Override
    protected void updateItem(String string, boolean empty) {
        super.updateItem(string, empty);
        if (string != null && !isEmpty()) {
            setGraphic(createAssembledFlowPane(string));
        }
        else {
            setGraphic(null);
            setText(null);
        }
    }

    /* Erzeuge ein FlowPane mit gefüllten Textbausteien */
    private FlowPane createAssembledFlowPane(String... messageTokens) {
        FlowPane flow = new FlowPane();
        for (String token : messageTokens) {
            Text text = new Text(token);

//            if (text.toString().contains(" TRACE ")) {
//                text.setStyle("-fx-fill: #0000FF");
//            }
//            if (text.toString().contains(" ALL ")) {
//                text.setStyle("-fx-fill: #FF00FF");
//            }
            if (text.toString().contains(" ERROR ")) {
                text.setStyle("-fx-fill: #FF0000");
            }
            else if (text.toString().contains(" WARN ")) {
                text.setStyle("-fx-fill: #FF8000");
            }
            else if (text.toString().contains(" INFO ")) {
            	 if (text.toString().contains("[OK]")) {
                     text.setStyle("-fx-fill: #00b33c");
                 }else{
                	 text.setStyle("-fx-fill: #000000");
                 }
            }
//            if (text.toString().contains(" FATAL ")) {
//                text.setStyle("-fx-fill: #FF0000");
//            }
//            if (text.toString().contains(" DEBUG ")) {
//                text.setStyle("-fx-fill: #808080");
//            }
//            if (text.toString().contains(" OFF ")) {
//                text.setStyle("-fx-fill: #8040FF");
//            }
          

            flow.getChildren().add(text);
        }
        return flow;
    }
}