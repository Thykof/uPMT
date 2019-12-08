package utils.UX;

import java.util.HashMap;
import java.util.UUID;

public class DragStore {

    private static IDraggable draggable;

    public static void setDraggable(IDraggable draggable) { DragStore.draggable = draggable; }
    public static <T> T getDraggable() { return (T)draggable; }
    public static void clearStore() { draggable = null; }

}