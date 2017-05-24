package de.bmoth.typechecker;

import de.bmoth.exceptions.TypeErrorException;
import org.junit.Test;

public class ErrorMachinesTest {

    @Test(expected = TypeErrorException.class)
    public void testNaturalException() {
        String machine = "MACHINE test\n" + "CONSTANTS k1,k2 \n" + "PROPERTIES k1 = 1 \n" + "END";
        new TestTypechecker(machine);
    }

}
