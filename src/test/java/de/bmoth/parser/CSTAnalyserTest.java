package de.bmoth.parser;

import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;
public class CSTAnalyserTest {

    @Test
    public void testWarningInPropertiesClause() {
        String machine = "MACHINE test\n";
        machine += "PROPERTIES TRUE or FALSE & FALSE \n";
        machine += "END";
        MachineNode machineNode = parseMachine(machine);
        List<String> warnings = machineNode.getWarnings();
        assertEquals(1, warnings.size());
        assertEquals("Ambiguous combination of operators 'or' (line 2, pos 16) and '&' (line 2, pos 25)."
            + " Use parentheses to avoid this.", warnings.get(0));
    }

    @Test
    public void testWarningInFormula() {
        String formula = "TRUE or FALSE & FALSE";
        FormulaNode formulaNode = parseFormula(formula);
        List<String> warnings = formulaNode.getWarnings();
        assertEquals(1, warnings.size());
        assertEquals("Ambiguous combination of operators 'or' (line 1, pos 5) and '&' (line 1, pos 14)."
            + " Use parentheses to avoid this.", warnings.get(0));
    }

    @Test
    public void testNoWarningInFormula() {
        String formula = "(TRUE or FALSE) & FALSE";
        FormulaNode formulaNode = parseFormula(formula);
        List<String> warnings = formulaNode.getWarnings();
        assertEquals(0, warnings.size());
    }
}
