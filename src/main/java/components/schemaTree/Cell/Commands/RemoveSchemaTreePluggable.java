package components.schemaTree.Cell.Commands;

import application.history.ModelUserActionCommand;
import components.schemaTree.Cell.SchemaTreePluggable;

public class RemoveSchemaTreePluggable extends ModelUserActionCommand<Void, Void> {

    private SchemaTreePluggable parent;
    private SchemaTreePluggable element;

    public RemoveSchemaTreePluggable(SchemaTreePluggable parent, SchemaTreePluggable element) {
        this.parent = parent;
        this.element = element;
    }

    @Override
    public Void execute() {
        parent.removeChild(element);
        return null;
    }

    @Override
    public Void undo() {
        parent.addChild(element);
        return null;
    }
}
