package de.bmoth.modelchecker;

import com.microsoft.z3.Expr;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ModelCheckerTest {
    @Test
    public void testSimpleModelsWithoutOperations() throws Exception {
        String dir = "src/test/resources/machines/";

        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyInitViolation.mch");
        boolean result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result);

        MachineNode simpleMachineWithoutViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyInitNoViolation.mch");
        result = ModelChecker.doModelCheck(simpleMachineWithoutViolation);
        assertEquals(true, result);
    }
}
