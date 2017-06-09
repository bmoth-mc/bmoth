package de.bmoth.app;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Julian on 09.06.2017.
 */
public class AppViewModel implements ViewModel {


    private StringProperty warnings = new SimpleStringProperty();
    private StringProperty code = new SimpleStringProperty();

    public String getWarnings() {
        return warnings.get();
    }

    public void setWarnings(String warnings) {
        this.warnings.set(warnings);
    }

    public StringProperty warningsProperty() {
        return warnings;
    }

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public StringProperty codeProperty() {
        return code;
    }


}
