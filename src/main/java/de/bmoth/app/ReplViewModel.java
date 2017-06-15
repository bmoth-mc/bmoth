package de.bmoth.app;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by julian on 15.06.17.
 */
public class ReplViewModel implements ViewModel {

    SimpleStringProperty code = new SimpleStringProperty();

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public SimpleStringProperty codeProperty() {
        return code;
    }
}
