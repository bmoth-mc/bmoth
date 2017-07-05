package de.bmoth.modelchecker.esmc;

import com.microsoft.z3.Context;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

public class ParallelTest {

    @Test
    public void parallelTest() throws ParserException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst("src/test/resources/machines/lifts/LiftLowerHigher.mch");

        MachineToZ3Translator m1 = new MachineToZ3Translator(simpleMachineWithViolation, new Context());
        m1.getInitialValueConstraint();
        m1.getInvariantConstraint();
        new MachineToZ3Translator(simpleMachineWithViolation, new Context());
    }
}
