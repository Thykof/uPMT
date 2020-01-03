package components.schemaTree.Controllers;

import components.schemaTree.Cell.SchemaTreePluggable;
import components.schemaTree.Cell.Models.SchemaTreeRoot;
import components.schemaTree.Cell.Visitors.CreateSchemaTreeItemVisitor;
import components.schemaTree.Cell.SchemaTreeCell;
import application.configuration.Configuration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SchemaTreeController implements Initializable {

    @FXML
    private
    TreeView<SchemaTreePluggable> schemaTree;

    private SchemaTreeRoot root;

    public SchemaTreeController(SchemaTreeRoot root) { this.root = root; }

    public static Node createSchemaTree(SchemaTreeRoot root) {
        SchemaTreeController controller = new SchemaTreeController(root);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(controller.getClass().getResource("/views/SchemaTree/SchemaTree.fxml"));
            loader.setController(controller);
            loader.setResources(Configuration.langBundle);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        schemaTree.setEditable(true);
        schemaTree.setCellFactory(modelTreeElementTreeView -> {
            return new SchemaTreeCell();
        });
        setTreeRoot(root);
    }

    private void setTreeRoot(SchemaTreeRoot root) {
        CreateSchemaTreeItemVisitor visitor = new CreateSchemaTreeItemVisitor();
        root.accept(visitor);
        schemaTree.setRoot(visitor.getSchemaTreeItem());
    }
}