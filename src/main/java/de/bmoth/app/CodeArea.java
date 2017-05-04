package de.bmoth.app;

import org.fxmisc.undo.UndoManager;

/**
 * Created by Julia on 04.05.2017.
 */
public class CodeArea extends org.fxmisc.richtext.CodeArea {


    public void deletehistory(){
        this.getUndoManager().forgetHistory();
    }
}
