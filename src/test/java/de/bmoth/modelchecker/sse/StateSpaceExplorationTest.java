package de.bmoth.modelchecker.sse;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Before;
import org.junit.Test;

import static de.bmoth.modelchecker.ModelCheckingResult.Type.UNKNOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateSpaceExplorationTest extends TestParser {
    private MachineBuilder machineBuilder;
    private ModelCheckingResult result;
    private MachineNode machine;

    @Before
    public void init() {
        machineBuilder = new MachineBuilder();
        result = null;
        machine = null;
    }


    @Test
    public void testStateSpaceExploration() {
        machine = machineBuilder
            .setName("BoringMachineWithoutLoop")
            .setVariables("x")
            .setInvariant("x : 1..7")
            .setInitialization("ANY p WHERE p : 1..2 THEN x := p END")
            .addOperation("incToThree = SELECT x : 1..2 THEN x := 3 END")
            .addOperation("incFromThree = SELECT x : 3..6 THEN x := x + 1 END")
            .build();

        result = new StateSpaceExplorator(machine).check();
        assertEquals(8, result.getSteps());
        assertTrue(result.isCorrect());

        machine = machineBuilder
            .setName("ExitingMachineWithLoop")
            .addOperation("resetToThree = SELECT x = 7 THEN x := 3 END")
            .build();

        result = new StateSpaceExplorator(machine).check();
        assertEquals(9, result.getSteps());
        assertTrue(result.isCorrect());
    }

    @Test
    public void testSateSpaceExplorationResultUnknown() {
        machine = machineBuilder
            .setName("ResultUnknownMachine")
            .setVariables("x")
            .setInvariant("x : INTEGER")
            .setInitialization("x := 1")
            .addOperation("failWithUnknown = SELECT (x>0 => 2**(10*x) = 2*(2**(10*x-1))) THEN x:= 2 END")
            .build();

        result = new StateSpaceExplorator(machine).check();
        assertEquals(false, result.isCorrect());
        assertEquals(UNKNOWN, result.getType());
    }
}
