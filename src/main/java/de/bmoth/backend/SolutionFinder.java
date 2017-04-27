package de.bmoth.backend;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_decl_kind;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class SolutionFinder {

    private Set<Expr> vars;
    private Solver solver;
    private Context z3Context;

    /**
     * Solution finder expects the constraint to be already added to the corresponding solver
     *
     * @param constraint the constraint to find solutions for
     * @param solver     corresponding z3 solver
     * @param z3Context  corresponding z3 context
     */
    public SolutionFinder(BoolExpr constraint, Solver solver, Context z3Context) {
        this.solver = solver;
        this.z3Context = z3Context;

        vars = new HashSet<>();
        collectVars(constraint);
    }

    /**
     * Collects all variables from an expression
     * <p>
     * credit goes to Vu Nguyen
     *
     * @param expression the expression to collect variables from
     * @see <a href="http://stackoverflow.com/questions/14080398/z3py-how-to-get-the-list-of-variables-from-a-formula#answer-14089886">Leonardo de Moura's answer on so.com</a>
     */
    private void collectVars(Expr expression) {
        if (expression.isConst()) {
            if (expression.getFuncDecl().getDeclKind() == Z3_decl_kind.Z3_OP_UNINTERPRETED) {
                vars.add(expression);
            }
        } else {
            for (Expr sub : expression.getArgs()) {
                collectVars(sub);
            }
        }
    }

    /**
     * Evaluate a single solution from solver over all variables
     *
     * @return a solution
     */
    private BoolExpr findSolution() {
        BoolExpr result = null;
        for (Expr var : vars) {
            Expr value = solver.getModel().eval(var, true);

            if (result == null) {
                result = z3Context.mkEq(var, value);
            } else {
                result = z3Context.mkAnd(result, z3Context.mkEq(var, value));
            }
        }
        return result;
    }


    /**
     * Evaluates all solutions up to a given maximum of iterations
     * <p>
     * credit goes to Taylor
     *
     * @param maxIterations the maximum nr of iterations
     * @return list of found solution
     * @see <a href="http://stackoverflow.com/questions/13395391/z3-finding-all-satisfying-models#answer-13398853">Taylor's answer on so.com</a>
     */
    public Set<BoolExpr> findSolutions(int maxIterations) {
        Set<BoolExpr> result = new HashSet<>();

        // as long as formula is satisfiable
        for (int i = 0; solver.check() == Status.SATISFIABLE && i < maxIterations; i++) {

            // find a solution ...
            BoolExpr solution = findSolution();

            // ... and add it as an exclusion constraint to solver stack
            solver.add(z3Context.mkNot(solution));

            result.add(solution);
        }

        //TODO it might be a good idea to remove all exclusion constraints from solver stack

        return result;
    }
}
