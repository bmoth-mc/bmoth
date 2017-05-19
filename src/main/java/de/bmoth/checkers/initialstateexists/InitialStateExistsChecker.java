package de.bmoth.checkers.initialstateexists;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;

public interface InitialStateExistsChecker {
    static InitialStateExistsCheckingResult doInitialStateExistsCheck(String machineAsString) {
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machineAsString);
        return doInitialStateExistsCheck(machineAsSemanticAst);
    }

    static InitialStateExistsCheckingResult doInitialStateExistsCheck(MachineNode machine) {
        Context ctx = new Context();
        Solver solver = Z3SolverFactory.getZ3Solver(ctx);
        MachineToZ3Translator machineTranslator = new MachineToZ3Translator(machine, ctx);

        final BoolExpr invariant = machineTranslator.getInitialValueConstraint();
        solver.add(invariant);
        Status check = solver.check();
        return new InitialStateExistsCheckingResult(check);
    }
}
