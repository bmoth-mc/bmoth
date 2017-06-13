package de.bmoth.checkers.initialstateexists;

import com.microsoft.z3.Status;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;

public class InitialStateExistsCheckerTest {
    @Test
    public void testWithInitialState() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x \n";
        machine += "INVARIANT x=1 \n";
        machine += "INITIALISATION x:= 1 \n";
        machine += "END";

        InitialStateExistsCheckingResult res = InitialStateExistsChecker
                .doInitialStateExistsCheck(parseMachine(machine));
        assertEquals(Status.SATISFIABLE, res.getResult());
    }
}
