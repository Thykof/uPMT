package application.history;

import utils.command.Undoable;
import java.util.UUID;

public abstract class ModelUserActionCommand<ExecuteResult, UndoResult> implements Undoable<ExecuteResult, UndoResult> {
    UUID userActionIdentifier;
    void setUserActionIdentifier(UUID id) { userActionIdentifier = id;}
    UUID getUserActionIdentifier() { return userActionIdentifier; }
}
