package components.modelisationSpace.justification.justificationCell;

import application.configuration.Configuration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import models.Descripteme;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditJustificationCell implements Initializable {

    private Stage stage;
    private Descripteme descripteme;
    private Descripteme descriptemeCopy;

    private @FXML TextArea textArea;
    private @FXML Button shiftLeft, shiftRight, cancelButton, confirmButton;
    private @FXML RadioButton beginningButton, endButton;

    public EditJustificationCell(Stage stage, Descripteme descripteme) {
        this.stage = stage;
        this.descripteme = descripteme;
        this.descriptemeCopy = new Descripteme(descripteme.getInterviewText(), descripteme.getStartIndex(), descripteme.getEndIndex());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.textProperty().bind(descriptemeCopy.getSelectionProperty());

        shiftLeft.setOnAction(actionEvent -> {
            if(beginningButton.isSelected()){
                descriptemeCopy.modifyIndex(descriptemeCopy.getStartIndex()-1, descriptemeCopy.getEndIndex());
            }
            else if(endButton.isSelected()){
                descriptemeCopy.modifyIndex(descriptemeCopy.getStartIndex(), descriptemeCopy.getEndIndex()-1);
            }
            onDescriptemeUpdate();
        });

        shiftRight.setOnAction(actionEvent -> {
            if(beginningButton.isSelected()){
                descriptemeCopy.modifyIndex(descriptemeCopy.getStartIndex()+1, descriptemeCopy.getEndIndex());
            }
            else if(endButton.isSelected()){
                descriptemeCopy.modifyIndex(descriptemeCopy.getStartIndex(), descriptemeCopy.getEndIndex()+1);
            }
            onDescriptemeUpdate();
        });

        beginningButton.setOnAction(actionEvent -> {
            onDescriptemeUpdate();
        });

        endButton.setOnAction(actionEvent -> {
            onDescriptemeUpdate();
        });

        cancelButton.setOnAction(actionEvent -> {
            stage.close();
        });

        confirmButton.setOnAction(actionEvent -> {
            descripteme.modifyIndex(descriptemeCopy.getStartIndex(), descriptemeCopy.getEndIndex());
            stage.close();
        });
    }

    public static EditJustificationCell edit(Descripteme descripteme, Window primaryStage) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(Configuration.langBundle.getString("descripteme_edit"));
        EditJustificationCell controller = new EditJustificationCell(stage, descripteme);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(controller.getClass().getResource("/views/modelisationSpace/Justification/JustificationCell/EditJustificationCell.fxml"));
            loader.setController(controller);
            loader.setResources(Configuration.langBundle);
            VBox layout = loader.load();
            Scene main = new Scene(layout);
            stage.setScene(main);
            stage.showAndWait();
            return controller;
        } catch (IOException e) {
            // TODO Exit Program
            e.printStackTrace();
        }
        return null;
    }

    private void onDescriptemeUpdate() {
        //TODO disable a button if it is to a border of the entire text.
        shiftLeft.setDisable(false);
        shiftRight.setDisable(false);

        if(beginningButton.isSelected()){
            if(descriptemeCopy.getStartIndex() == 0)
                shiftLeft.setDisable(true);
            else if(descriptemeCopy.getFragmentText().length() == 1)
                shiftRight.setDisable(true);
        }

        else if(endButton.isSelected()) {
            if(descriptemeCopy.getEndIndex() == descriptemeCopy.getInterviewText().getText().length())
                shiftRight.setDisable(true);
            else if(descriptemeCopy.getFragmentText().length() == 1)
                shiftLeft.setDisable(true);
        }
    }

}
