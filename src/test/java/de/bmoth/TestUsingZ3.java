package de.bmoth;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import org.junit.After;
import org.junit.Before;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestUsingZ3 {
    protected Context z3Context;
    protected Solver z3Solver;

    @Before
    public void setup() {
        z3Context = new Context();
        z3Solver = z3Context.mkSolver();
    }

    @After
    public void cleanup() {
        z3Context.close();
    }

    public void check(Status satisfiable, String formula) {
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        // create scope just for current constraint
        z3Solver.push();
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        // clean solver stack
        z3Solver.pop();
        assertEquals(satisfiable, check);
    }

    public void checkLaw(String law) {
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(law, z3Context);
        z3Solver.push();
        z3Solver.add(z3Context.mkNot(constraint));
        Status check = z3Solver.check();
        z3Solver.pop();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    public void checkTruthTable(Map<String, Status> map) {
        for (Map.Entry<String, Status> entry : map.entrySet()) {
            check(entry.getValue(), entry.getKey());
            z3Solver.reset();
        }
    }
}
