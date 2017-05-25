package de.bmoth.issues;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Status;
import de.bmoth.TestUsingZ3;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Issue76Test extends TestUsingZ3 {
    @Test
    @Ignore
    public void testOperatorPrecedence() {
        String satFormula1 = "TRUE or (FALSE & FALSE)";
        String satFormula2 = "TRUE or FALSE & FALSE";
        Status check;

        BoolExpr satExpr1 = FormulaToZ3Translator.translatePredicate(satFormula1, z3Context);
        BoolExpr satExpr2 = FormulaToZ3Translator.translatePredicate(satFormula2, z3Context);

        z3Solver.add(satExpr1);
        check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);

        z3Solver.add(satExpr2);
        check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }
}
