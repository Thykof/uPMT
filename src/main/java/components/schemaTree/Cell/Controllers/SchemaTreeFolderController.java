package components.schemaTree.Cell.Controllers;

import application.configuration.Configuration;
import components.schemaTree.Cell.appCommands.SchemaTreeCommandFactory;
import components.schemaTree.Cell.Models.SchemaCategory;
import components.schemaTree.Cell.Models.SchemaFolder;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class SchemaTreeFolderController extends SchemaTreeCellController {

    private SchemaFolder folder;
    private SchemaTreeCommandFactory cmdFactory;

    public SchemaTreeFolderController(SchemaFolder folder, SchemaTreeCommandFactory cmdFactory) {
        super(folder);
        this.folder = folder;
        this.cmdFactory = cmdFactory;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        MenuItem addFolderButton = new MenuItem(Configuration.langBundle.getString("add_folder"));
        addFolderButton.setOnAction(actionEvent -> {
            SchemaFolder f = new SchemaFolder(Configuration.langBundle.getString("folder"));
            cmdFactory.addSchemaTreeChild(f).execute();
        });
        optionsMenu.getItems().add(addFolderButton);

        MenuItem addCategoryButton = new MenuItem(Configuration.langBundle.getString("add_category"));
        addCategoryButton.setOnAction(actionEvent -> {
            SchemaCategory newModel = new SchemaCategory("category");
            cmdFactory.addSchemaTreeChild(newModel).execute();
        });
        optionsMenu.getItems().add(addCategoryButton);

        MenuItem deleteButton = new MenuItem(Configuration.langBundle.getString("delete"));
        deleteButton.setOnAction(actionEvent -> {
            cmdFactory.removeTreeElement(folder).execute();
        });
        optionsMenu.getItems().add(deleteButton);
    }
}