package interviewSelector.Controllers;

import interviewSelector.Models.Interview;
import application.Configuration.Configuration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import utils.ResourceLoader;

import java.net.URL;
import java.util.ResourceBundle;

public class InterviewListCellControler implements Initializable {

    @FXML Label name;
    @FXML ImageView pictureView;
    @FXML MenuButton optionsMenu;

    protected Interview interview;
    private boolean shouldRemoveMenuButtonVisibility;

    public InterviewListCellControler(Interview interview) {
        this.interview = interview;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pictureView.setImage(ResourceLoader.loadImage("category.png"));

        name.setText(interview.getTitle());

        MenuItem deleteButton = new MenuItem(Configuration.langBundle.getString("delete"));
        deleteButton.setOnAction(actionEvent -> {
            System.out.println("delete !");
        });

        optionsMenu.setVisible(false);
        optionsMenu.onHiddenProperty().addListener((observableValue, eventEventHandler, t1) -> {
            if(shouldRemoveMenuButtonVisibility) { shouldRemoveMenuButtonVisibility = false; optionsMenu.setVisible(false);}
        });
    }

    public void setOnHover(boolean YoN) {
        if(optionsMenu.isShowing())
            shouldRemoveMenuButtonVisibility = true;
        else
            optionsMenu.setVisible(YoN);
    }

    public boolean getOnHover() { return optionsMenu.isVisible(); }
}
