package models;

import components.interviewPanel.utils.WordStyle;
import javafx.scene.input.DataFormat;
import javafx.scene.paint.Color;

public class Annotation extends Fragment {
    public static final DataFormat format = new DataFormat("Annotation");
    Color color;

    public Annotation(InterviewText interviewText, int startIndex, int endIndex, Color c) {
        super(interviewText, startIndex, endIndex);
        color = c;
    }

    @Override
    public DataFormat getDataFormat() {
        return format;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Descripteme toDescripteme() {
        return new Descripteme(interviewText, startIndex, endIndex);
    }

    public WordStyle getStyle() {
        return new WordStyle(false, true, color);
    }
}