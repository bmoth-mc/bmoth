package de.bmoth.typechecker;

import org.junit.Test;

import static de.bmoth.TestConstants.MACHINE_NAME;
import static de.bmoth.TestConstants.TWO_CONSTANTS;
import static de.bmoth.typechecker.TestTypechecker.*;

public class ErrorMachinesTest {

    @Test
    public void cannotInferConstantType() {
        String machine = MACHINE_NAME + TWO_CONSTANTS + "PROPERTIES k1 = 1 \n" + "END";
        typeCheckMachineAndGetErrorMessage(machine);
    }

    @Test
    public void cannotInferVariableType() {
        String machine = MACHINE_NAME + "VARIABLES x, y \n" + "INVARIANT x:INTEGER \n"
                + "INITIALISATION \nx := -3 || \n y := 1 \n" + "END";
        typeCheckMachineAndGetErrorMessage(machine);
    }

}
