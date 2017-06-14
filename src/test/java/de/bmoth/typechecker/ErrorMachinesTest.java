package de.bmoth.typechecker;

import org.junit.Test;
import static de.bmoth.typechecker.TestTypechecker.*;

public class ErrorMachinesTest {

    @Test
    public void cannotInferConstantType() {
        String machine = "MACHINE test\n" + "CONSTANTS k1,k2 \n" + "PROPERTIES k1 = 1 \n" + "END";
        typeCheckMachineAndGetErrorMessage(machine);
    }

    @Test
    public void cannotInferVariableType() {
        String machine = "MACHINE test\n" + "VARIABLES x, y \n" + "INVARIANT x:INTEGER \n"
                + "INITIALISATION \nx := -3 || \n y := 1 \n" + "END";
        typeCheckMachineAndGetErrorMessage(machine);
    }

}
