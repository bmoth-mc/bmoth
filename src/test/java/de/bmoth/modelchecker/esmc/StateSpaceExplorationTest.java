package de.bmoth.modelchecker.esmc;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.StateSpace;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.modelchecker.ModelCheckingResult.Type.UNKNOWN;
import static org.junit.Assert.*;

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

        result = new ExplicitStateModelChecker(machine).check();
        assertEquals(7, result.getSteps());
        assertTrue(result.isCorrect());

        StateSpace space = result.getStateSpace();
        assertEquals(0, space.getCycles().size());

        machine = machineBuilder
            .setName("ExitingMachineWithLoop")
            .addOperation("resetToThree = SELECT x = 7 THEN x := 3 END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertEquals(7, result.getSteps());
        assertTrue(result.isCorrect());

        space = result.getStateSpace();
        assertEquals(1, space.getCycles().size());
        assertEquals("[{x=3}, {x=4}, {x=5}, {x=6}, {x=7}]", space.getCycles().get(0).toString());
    }

    @Test
    public void testTarjan() {
        // see https://de.wikipedia.org/wiki/Algorithmus_von_Tarjan_zur_Bestimmung_starker_Zusammenhangskomponenten#Visualisierung
        // a..j -> 1..10
        machine = machineBuilder
            .setName("TarjanMachine")
            .setVariables("x")
            .setInvariant("x : 1..10")
            .setInitialization("x := 1")
            .addOperation(" a2b = SELECT x = 1 THEN x := 2 END")
            .addOperation(" b2c = SELECT x = 2 THEN x := 3 END")
            .addOperation(" c2de = ANY p WHERE p = 4 or p = 5 & x = 3 THEN x := p END")
            .addOperation(" d2ae = ANY p WHERE p = 1 or p = 5 & x = 4 THEN x := p END")
            .addOperation(" e2cf = ANY p WHERE p = 3 or p = 6 & x = 5 THEN x := p END")
            .addOperation(" f2gi = ANY p WHERE p = 7 or p = 9 & x = 6 THEN x := p END")
            .addOperation(" g2fh = ANY p WHERE p = 6 or p = 8 & x = 7 THEN x := p END")
            .addOperation(" h2j = SELECT x = 8 THEN x := 10 END")
            .addOperation(" i2gf = ANY p WHERE p = 7 or p = 6 & x = 9 THEN x := p END")
            .addOperation(" j2i = SELECT x = 10 THEN x := 9 END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
        assertEquals(10, result.getSteps());

        StateSpace space = result.getStateSpace();
        assertEquals(8, space.getCycles().size());
        assertEquals("[{x=1}, {x=2}, {x=3}, {x=4}]", space.getCycles().get(0).toString());
        assertEquals("[{x=3}, {x=4}, {x=5}]", space.getCycles().get(1).toString());
        assertEquals("[{x=3}, {x=5}]", space.getCycles().get(2).toString());
        assertEquals("[{x=6}, {x=9}, {x=7}]", space.getCycles().get(3).toString());
        assertEquals("[{x=6}, {x=9}]", space.getCycles().get(4).toString());
        assertEquals("[{x=6}, {x=7}]", space.getCycles().get(5).toString());
        assertEquals("[{x=6}, {x=7}, {x=8}, {x=10}, {x=9}]", space.getCycles().get(6).toString());
        assertEquals("[{x=9}, {x=7}, {x=8}, {x=10}]", space.getCycles().get(7).toString());
    }

    @Test
    @Ignore
    public void testSateSpaceExplorationResultUnknown() {
        machine = machineBuilder
            .setName("ResultUnknownMachine")
            .setVariables("x")
            .setInvariant("x : INTEGER")
            .setInitialization("x := 1")
            .addOperation("failWithUnknown = SELECT (x>0 => 2**(10*x) = 2*(2**(10*x-1))) THEN x:= 2 END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertEquals(false, result.isCorrect());
        assertEquals(UNKNOWN, result.getType());
        assertEquals(1, result.getSteps());
    }
}
