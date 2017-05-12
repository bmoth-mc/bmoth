package de.bmoth.app;

/**
 * Created by Julian on 04.05.2017.
 */
public class CodeArea extends org.fxmisc.richtext.CodeArea {

    public void deletehistory() {
        this.getUndoManager().forgetHistory();
    }
}
