package de.bmoth.issues;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;
import static de.bmoth.TestUsingZ3.*;

public class Issue59Test {
    @Test
    public void testIssue59() {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : INTEGER &\n";
        machine += "\tx**2 = x*x \n";
        machine += "INITIALISATION x := -3\n";
        machine += "OPERATIONS\n";
        machine += "\tIncX = SELECT x < 50 THEN x := x+1 END\n";
        machine += "END";

        ModelCheckingResult result = new ExplicitStateModelChecker(parseMachine(machine)).check();
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testIssue59WithAdditionalInvariant() {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : INTEGER &\n";
        machine += "\tx**2 = x*x &\n";
        machine += "\t#x.(x:INTEGER & {x} \\/ {1,2} = {1,2})\n";
        machine += "INITIALISATION x := -3\n";
        machine += "OPERATIONS\n";
        machine += "\tIncX = SELECT x < 50 THEN x := x+1 END\n";
        machine += "END";

        ModelCheckingResult result = new ExplicitStateModelChecker(parseMachine(machine)).check();
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testIssue59JustInvariant() {
        Context ctx = new Context();
        Solver s = ctx.mkSolver();
        String formula = "x**2 = x*x & #x.({x} \\/ {1,2} = {1,2})";
        BoolExpr combinedConstraint = translatePredicate(formula, ctx);

        s.add(combinedConstraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testIssue59JustInvariant2() {
        Context ctx = new Context();
        Solver s = ctx.mkSolver();
        String formula = "x**2 = x*x";
        BoolExpr combinedConstraint = translatePredicate(formula, ctx);

        s.add(combinedConstraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testArithmeticLawsMachine() {
        MachineNode simpleMachineWithoutViolation = parseMachineFromFile(
                "src/test/resources/machines/OnlyInitNoViolation.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithoutViolation);
        assertEquals(true, result.isCorrect());
    }
}
