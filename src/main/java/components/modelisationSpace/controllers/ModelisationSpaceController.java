package components.modelisationSpace.controllers;

import components.modelisationSpace.UI.AutoSuggestionsTextField;
import components.modelisationSpace.moment.controllers.RootMomentController;
import components.modelisationSpace.moment.model.RootMoment;
import utils.autoSuggestion.strategies.SuggestionStrategyCategory;
import utils.scrollOnDragPane.ScrollOnDragPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModelisationSpaceController extends ScrollOnDragPane implements Initializable {

    private  @FXML BorderPane pane;

    public ModelisationSpaceController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/modelisationSpace/ModelisationSpace.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        AutoSuggestionsTextField autoSuggestionsTextField = new AutoSuggestionsTextField(new SuggestionStrategyCategory());
        //autoSuggestionsTextField.setStrategy(new SuggestionStrategyFolder());
        //autoSuggestionsTextField.setStrategy(new SuggestionStrategyNoSense());
        //pane.getChildren().add(autoSuggestionsTextField);
    }


    public void setRootMoment(RootMoment m) {
        RootMomentController controller = new RootMomentController(m);

        pane.setCenter(RootMomentController.createRootMoment(controller));
    }
}
