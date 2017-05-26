package de.bmoth.typechecker;

import static de.bmoth.typechecker.TestTypechecker.getFormulaTypes;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class RelationsTest {

    @Test
    public void testDirectProduct() {
        String formula = "a = {1 |-> TRUE} >< {b |-> 1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*(BOOL*INTEGER))", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    
}
