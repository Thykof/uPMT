package persistency.newSaveSystem;

import models.ConcreteCategory;
import models.Moment;
import persistency.newSaveSystem.serialization.ObjectSerializer;
import persistency.newSaveSystem.serialization.Serializable;

import java.util.ArrayList;

public class SMoment extends Serializable<Moment> {

    //General info
    public static final int version = 1;
    public static final String modelName = "moment";

    //Fields
    public String name;
    public SJustification justification;
    public ArrayList<SConcreteCategory> categories;
    public ArrayList<SMoment> submoments;

    public SMoment(ObjectSerializer serializer) {
        super(serializer);
    }
    public SMoment(Moment objectReference) {
        super(modelName, version, objectReference);

        this.name = objectReference.getName();
        this.justification = new SJustification(objectReference.getJustification());

        this.categories = new ArrayList<>();
        for(ConcreteCategory cc: objectReference.concreteCategoriesProperty())
            categories.add(new SConcreteCategory(cc));

        this.submoments = new ArrayList<>();
        for(Moment m: objectReference.momentsProperty())
            submoments.add(new SMoment(m));
    }

    @Override
    protected void addStrategies() {

    }

    @Override
    protected void read() {
        name = serializer.getString("name");
        justification = serializer.getObject("justification", SJustification::new);
        categories = serializer.getArray(serializer.setListSuffix(SConcreteCategory.modelName), SConcreteCategory::new);
        submoments = serializer.getArray(serializer.setListSuffix(SMoment.modelName), SMoment::new);
    }

    @Override
    protected void write(ObjectSerializer serializer) {
        serializer.writeString("name", name);
        serializer.writeObject("justification", justification);
        serializer.writeArray(serializer.setListSuffix(SConcreteCategory.modelName), categories);
        serializer.writeArray(serializer.setListSuffix(SMoment.modelName), submoments);
    }

    @Override
    protected Moment createModel() {
        Moment m = new Moment(name, justification.createModel());
        for(SMoment sm: submoments)
            m.addMoment(sm.convertToModel());
        for(SConcreteCategory cc: categories)
            m.addCategory(cc.convertToModel());
        return m;
    }
}