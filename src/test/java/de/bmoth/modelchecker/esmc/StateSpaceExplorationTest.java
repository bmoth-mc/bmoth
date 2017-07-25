package de.bmoth.modelchecker.esmc;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.State;
import de.bmoth.modelchecker.StateSpace;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static de.bmoth.modelchecker.ModelCheckingResult.Type.UNKNOWN;
import static org.junit.Assert.*;

public class StateSpaceExplorationTest extends TestParser {
    private MachineBuilder machineBuilder;
    private ModelCheckingResult result;
    private MachineNode machine;
    private CycleComparator<State> comparator;

    @Before
    public void init() {
        machineBuilder = new MachineBuilder();
        result = null;
        machine = null;
        comparator = new CycleComparator<>();
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
        List<List<State>> cycles = new TarjanSimpleCycles<>(space).findSimpleCycles();
        assertEquals(0, cycles.size());

        machine = machineBuilder
            .setName("ExitingMachineWithLoop")
            .addOperation("resetToThree = SELECT x = 7 THEN x := 3 END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertEquals(7, result.getSteps());
        assertTrue(result.isCorrect());

        space = result.getStateSpace();
        cycles = new TarjanSimpleCycles<>(space).findSimpleCycles();

        comparator.addExpectedCycle("{x=3}", "{x=4}", "{x=5}", "{x=6}", "{x=7}");
        comparator.addActualCycle(cycles.get(0));

        comparator.compare();
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

        List<List<State>> cycles = new TarjanSimpleCycles<>(result.getStateSpace()).findSimpleCycles();
        assertEquals(8, cycles.size());

        comparator.addExpectedCycle("{x=1}", "{x=2}", "{x=3}", "{x=4}");
        comparator.addExpectedCycle("{x=3}", "{x=4}", "{x=5}");
        comparator.addExpectedCycle("{x=3}", "{x=5}");
        comparator.addExpectedCycle("{x=6}", "{x=7}");
        comparator.addExpectedCycle("{x=6}", "{x=7}", "{x=8}", "{x=10}", "{x=9}");
        comparator.addExpectedCycle("{x=6}", "{x=9}", "{x=7}");
        comparator.addExpectedCycle("{x=6}", "{x=9}");
        comparator.addExpectedCycle("{x=7}", "{x=8}", "{x=10}", "{x=9}");

        cycles.forEach(comparator::addActualCycle);

        comparator.compare();
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
