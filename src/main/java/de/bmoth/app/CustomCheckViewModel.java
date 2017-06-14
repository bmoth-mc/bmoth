package de.bmoth.app;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Created by julian on 14.06.17.
 */
public class CustomCheckViewModel implements ViewModel {

    SimpleBooleanProperty checkInit = new SimpleBooleanProperty(false);
    SimpleBooleanProperty checkInvar = new SimpleBooleanProperty(false);
    SimpleBooleanProperty checkModel = new SimpleBooleanProperty(false);


    public boolean isCheckInit() {
        return checkInit.get();
    }

    public void setCheckInit(boolean checkInit) {
        this.checkInit.set(checkInit);
    }

    public SimpleBooleanProperty checkInitProperty() {
        return checkInit;
    }

    public boolean isCheckInvar() {
        return checkInvar.get();
    }

    public void setCheckInvar(boolean checkInvar) {
        this.checkInvar.set(checkInvar);
    }

    public SimpleBooleanProperty checkInvarProperty() {
        return checkInvar;
    }

    public boolean isCheckModel() {
        return checkModel.get();
    }

    public void setCheckModel(boolean checkModel) {
        this.checkModel.set(checkModel);
    }

    public SimpleBooleanProperty checkModelProperty() {
        return checkModel;
    }


}
