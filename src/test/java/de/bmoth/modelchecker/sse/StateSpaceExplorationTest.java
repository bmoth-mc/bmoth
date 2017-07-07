package de.bmoth.modelchecker.sse;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateSpaceExplorationTest extends TestParser {
    @Test
    public void testStateSpaceExploration() {
        MachineBuilder machineBuilder = new MachineBuilder();

        MachineNode boringMachine = machineBuilder
            .setName("StateSpaceExploration")
            .setVariables("x")
            .setInvariant("x : 1..7")
            .setInitialization("ANY p WHERE p : 1..2 THEN x := p END")
            .addOperation("incToThree = SELECT x : 1..2 THEN x := 3 END")
            .addOperation("incFromThree = SELECT x : 3..6 THEN x := x + 1 END")
            .build();

        MachineNode exitingMachine = machineBuilder
            .addOperation("resetToThree = SELECT x = 7 THEN x := 3 END")
            .build();

        ModelCheckingResult result = new StateSpaceExplorator(exitingMachine).check();
        assertEquals(9, result.getSteps());
        assertTrue(result.isCorrect());

        result = new StateSpaceExplorator(boringMachine).check();
        assertEquals(8, result.getSteps());
        assertTrue(result.isCorrect());
    }
}
