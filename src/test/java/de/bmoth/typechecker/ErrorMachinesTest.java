package de.bmoth.typechecker;

import de.bmoth.exceptions.TypeErrorException;
import org.junit.Test;

public class ErrorMachinesTest {

    @Test(expected = TypeErrorException.class)
    public void cannotInferConstantType() {
        String machine = "MACHINE test\n" + "CONSTANTS k1,k2 \n" + "PROPERTIES k1 = 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test(expected = TypeErrorException.class)
    public void cannotInferVariableType() {
        String machine =
            "MACHINE test\n" +
                "VARIABLES x, y \n" +
                "INVARIANT x:INTEGER \n" +
                "INITIALISATION \nx := -3 || \n y := 1 \n" +
                "END";
        new TestTypechecker(machine);
    }


}
