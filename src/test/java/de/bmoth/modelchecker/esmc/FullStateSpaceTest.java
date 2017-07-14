package de.bmoth.modelchecker.esmc;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.StateSpaceNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class FullStateSpaceTest extends TestParser {

    private MachineBuilder builder;
    private MachineNode machine;
    private ModelCheckingResult result;

    @Before
    public void init() {
        builder = new MachineBuilder();
        machine = null;
        result = null;
    }

    @Test
    public void testFullStateSpace1() {
        machine = builder
            .setName("SimpleMachine")
            .setSets("")
            .setVariables("x")
            .setInvariant("x:NATURAL")
            .setInitialization("x := 1")
            .addOperation("incSome = PRE x < 3 THEN x := x+1 END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
        assertEquals(3, result.getSteps());

        Set<StateSpaceNode> stateSpace = result.getStateSpaceRoot();
        assertEquals(1, stateSpace.size());

        // as we have not added an LTL formula, each root node should be in the Buechi default node
        for (StateSpaceNode ssNode : stateSpace) {
            assertEquals(ssNode.getState().getBuechiNodes().size(), 1);
        }
    }

    @Test
    public void testFullStateSpace1WithLTL() {
        machine = builder
            .setName("SimpleMachine")
            .setDefinitions("ASSERT_LTL_1 == \"G {x=2}\"")
            .setSets("")
            .setVariables("x")
            .setInvariant("x:NATURAL")
            .setInitialization("x := 1")
            .addOperation("incSome = PRE x < 3 THEN x := x+1 END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
        assertEquals(3, result.getSteps());

        Set<StateSpaceNode> rootNodes = result.getStateSpaceRoot();
        assertEquals(1, rootNodes.size());

        for (StateSpaceNode ssNode : rootNodes) {
            assertEquals(ssNode.getState().getBuechiNodes().size(), 1);
            Set<StateSpaceNode> successors = ssNode.getSuccessors();
            for (StateSpaceNode successor : successors) {
                assertNotEquals(ssNode.getState().getBuechiNodes().iterator().next(), successor.getState().getBuechiNodes().iterator().next());
            }
        }
    }
}
