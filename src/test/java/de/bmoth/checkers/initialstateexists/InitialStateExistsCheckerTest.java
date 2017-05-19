package de.bmoth.checkers.initialstateexists;

import com.microsoft.z3.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InitialStateExistsCheckerTest {
    @Test
    public void testWithInitialState() throws Exception {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x \n";
        machine += "INVARIANT x=1 \n";
        machine += "INITIALISATION x:= 1 \n";
        machine += "END";

        InitialStateExistsCheckingResult res = InitialStateExistsChecker.doInitialStateExistsCheck(machine);
        assertEquals(Status.SATISFIABLE, res.getResult());
    }
}
