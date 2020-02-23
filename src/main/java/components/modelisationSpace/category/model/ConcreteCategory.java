package components.modelisationSpace.category.model;

import components.modelisationSpace.justification.models.Justification;
import components.modelisationSpace.property.appCommands.AddConcretePropertyCommand;
import components.modelisationSpace.property.appCommands.RemoveConcretePropertyCommand;
import components.modelisationSpace.property.model.ConcreteProperty;
import components.schemaTree.Cell.Models.SchemaCategory;
import components.schemaTree.Cell.Models.SchemaProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.input.DataFormat;
import utils.dragAndDrop.IDraggable;

import java.util.LinkedList;
import java.util.function.Supplier;

public class ConcreteCategory implements IDraggable {

    public static final DataFormat format = new DataFormat("ConcreteCategory");
    private SchemaCategory category;
    private Justification justification;

    private ListProperty<ConcreteProperty> properties;
    private ListChangeListener<SchemaProperty> onPropertiesUpdate = change -> {
        while(change.next()){
            for (SchemaProperty rem : change.getRemoved()) {
                new RemoveConcretePropertyCommand(this, properties.get(indexOfConcreteProperty(rem))).execute();
            }
            for (SchemaProperty added : change.getAddedSubList()) {
                if(indexOfConcreteProperty(added) == -1)
                    new AddConcretePropertyCommand(this, new ConcreteProperty(added));
            }
        }
    };

    public ConcreteCategory(SchemaCategory c) {
        this.category = c;
        this.justification = new Justification();

        this.properties = new SimpleListProperty<>(FXCollections.observableList(new LinkedList<>()));
        for(SchemaProperty p: c.propertiesProperty())
            properties.add(new ConcreteProperty(p));
        c.propertiesProperty().addListener(onPropertiesUpdate);
    }

    public String getName() { return category.getName(); }
    public StringProperty nameProperty() { return category.nameProperty(); }

    public Justification getJustification() { return justification; }

    public boolean isSchemaCategory(SchemaCategory sc) { return sc == category; }

    public ObservableBooleanValue existsProperty() { return category.existsProperty(); }

    public ObservableList<ConcreteProperty> propertiesProperty() { return properties; }

    public void addConcreteProperty(ConcreteProperty p) {
        properties.add(p);
    }

    public void addConcreteProperty(int index, ConcreteProperty p) {
        if(index == properties.size())
            addConcreteProperty(p);
        else
            properties.add(index, p);
    }

    public void removeConcreteProperty(ConcreteProperty p) {
        properties.remove(p);
    }

    public int indexOfConcreteProperty(ConcreteProperty property) {
        return properties.indexOf(property);
    }

    private int indexOfConcreteProperty(SchemaProperty sp) {
        for(int i = 0; i < properties.size(); i++)
            if(properties.get(i).isSchemaProperty(sp))
                return i;
        return -1;
    }

    @Override
    public DataFormat getDataFormat() {
        return format;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }
}
