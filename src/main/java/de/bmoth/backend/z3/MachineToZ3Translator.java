package de.bmoth.backend.z3;

import com.microsoft.z3.*;

import de.bmoth.backend.SubstitutionOptions;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.visitors.SubstitutionVisitor;

import java.util.*;

import static de.bmoth.backend.TranslationOptions.PRIMED_0;
import static de.bmoth.backend.TranslationOptions.UNPRIMED;

public class MachineToZ3Translator {
    private final MachineNode machineNode;
    private final Context z3Context;
    private final SubstitutionToZ3TranslatorVisitor visitor;
    private final Z3TypeInference z3TypeInference;
    private final Expr[] originalVariables;
    private TupleSort tuple;

    public MachineToZ3Translator(MachineNode machineNode, Context ctx) {
        this.machineNode = machineNode;
        this.z3Context = ctx;
        this.visitor = new SubstitutionToZ3TranslatorVisitor();
        this.z3TypeInference = new Z3TypeInference();
        this.originalVariables = getVariables().stream().map(this::getVariable).toArray(Expr[]::new);
        z3TypeInference.visitMachineNode(machineNode);
    }

    private List<BoolExpr> visitOperations(SubstitutionOptions ops) {
        List<BoolExpr> results = new ArrayList<>();
        // if there's at least one operation...
        if (!machineNode.getOperations().isEmpty()) {
            // ... then for every operation ...
            for (OperationNode operationNode : this.machineNode.getOperations()) {
                // ... translate it's substitution
                BoolExpr substitution = visitor.visitSubstitutionNode(operationNode.getSubstitution(), ops);

                // for unassigned variables add a dummy assignment, e.g. x' = x
                Set<DeclarationNode> set = new HashSet<>(this.getVariables());
                set.removeAll(operationNode.getSubstitution().getAssignedVariables());
                List<BoolExpr> dummyAssignments = createDummyAssignment(set, ops);
                dummyAssignments.add(0, substitution);
                BoolExpr[] array = dummyAssignments.toArray(new BoolExpr[dummyAssignments.size()]);
                results.add(z3Context.mkAnd(array));
            }
        }
        // if there's no operation...
        else {
            // ... add dummy assignments for all variables
            Set<DeclarationNode> set = new HashSet<>(this.getVariables());
            List<BoolExpr> dummyAssignments = createDummyAssignment(set, ops);
            BoolExpr[] array = dummyAssignments.toArray(new BoolExpr[dummyAssignments.size()]);
            results.add(z3Context.mkAnd(array));
        }
        return results;
    }

    protected List<BoolExpr> createDummyAssignment(Set<DeclarationNode> unassignedVariables, SubstitutionOptions ops) {
        List<BoolExpr> list = new ArrayList<>();
        for (DeclarationNode node : unassignedVariables) {
            BoolExpr mkEq = z3Context.mkEq(getPrimedVariable(node, ops.getLhs()), getPrimedVariable(node, ops.getRhs()));
            list.add(mkEq);
        }
        return list;
    }

    public List<DeclarationNode> getVariables() {
        return machineNode.getVariables();
    }

    public List<DeclarationNode> getConstants() {
        return machineNode.getConstants();
    }

    public Expr getVariableAsZ3Expression(DeclarationNode node) {
        Sort type = z3TypeInference.getZ3Sort(node, z3Context);
        return z3Context.mkConst(node.getName(), type);
    }

    public Expr getVariable(DeclarationNode node) {
        Sort type = z3TypeInference.getZ3Sort(node, z3Context);
        return z3Context.mkConst(node.getName(), type);
    }

    public Expr getPrimedVariable(DeclarationNode node, TranslationOptions ops) {
        String primedName = getPrimedName(node.getName(), ops);
        Sort type = z3TypeInference.getZ3Sort(node, z3Context);
        return z3Context.mkConst(primedName, type);
    }

    public BoolExpr getInitialValueConstraint(TranslationOptions ops) {
        BoolExpr initialization = null;
        BoolExpr properties = null;

        if (machineNode.getInitialisation() != null) {
            initialization = visitor.visitSubstitutionNode(machineNode.getInitialisation(), new SubstitutionOptions(ops, UNPRIMED));
        }
        if (machineNode.getProperties() != null) {
            properties = FormulaToZ3Translator.translatePredicate(machineNode.getProperties(), z3Context, z3TypeInference);
        }

        if (initialization != null && properties != null) {
            return z3Context.mkAnd(initialization, properties);
        } else if (initialization != null) {
            return initialization;
        } else {
            return properties;
        }
    }

    public BoolExpr getInitialValueConstraint() {
        return getInitialValueConstraint(PRIMED_0);
    }

    private BoolExpr translatePredicate(PredicateNode predicateNode, TranslationOptions ops) {
        BoolExpr predicate = FormulaToZ3Translator.translatePredicate(predicateNode, z3Context, ops,
            z3TypeInference);
        return substituteWithPrimedIfNecessary(predicate, ops);
    }

    private BoolExpr substituteWithPrimedIfNecessary(BoolExpr boolExpr, TranslationOptions ops) {
        if (ops.isHasPrimeLevel()) {
            Expr[] primedVariables = getVariables().stream().map(var -> getPrimedVariable(var, ops)).toArray(Expr[]::new);
            return (BoolExpr) boolExpr.substitute(originalVariables, primedVariables);
        } else {
            return boolExpr;
        }
    }

    public BoolExpr getInvariantConstraint(TranslationOptions ops) {
        PredicateNode invariantPredicate = machineNode.getInvariant();
        if (invariantPredicate != null) {
            return translatePredicate(invariantPredicate, ops);
        } else {
            return z3Context.mkTrue();
        }
    }

    public BoolExpr getInvariantConstraint() {
        return getInvariantConstraint(UNPRIMED);
    }

    public List<BoolExpr> getOperationConstraints(SubstitutionOptions ops) {
        return visitOperations(ops);
    }

    public List<BoolExpr> getOperationConstraints() {
        return visitOperations(new SubstitutionOptions(PRIMED_0, UNPRIMED));
    }

    public BoolExpr getCombinedOperationConstraint(SubstitutionOptions ops) {
        BoolExpr[] operations = visitOperations(ops).toArray(new BoolExpr[0]);

        switch (operations.length) {
            case 0:
                return z3Context.mkTrue();
            case 1:
                return operations[0];
            default:
                return z3Context.mkOr(operations);
        }
    }

    public BoolExpr getCombinedOperationConstraint() {
        return getCombinedOperationConstraint(new SubstitutionOptions(PRIMED_0, UNPRIMED));
    }

    public Map<String, Expr> getVarMapFromModel(Model model, TranslationOptions ops) {
        HashMap<String, Expr> map = new HashMap<>();
        for (DeclarationNode declNode : getVariables()) {
            Expr expr = getPrimedVariable(declNode, ops);
            Expr value = model.eval(expr, true);
            map.put(declNode.getName(), value);
        }
        for (DeclarationNode declarationNode : getConstants()) {
            Expr expr = getVariable(declarationNode);
            Expr value = model.eval(expr, true);
            map.put(declarationNode.getName(), value);
        }

        return map;
    }

    public BoolExpr getDistinctVars(int from, int to) {
        if (tuple == null) {
            Expr[] variables = getVariables().stream().map(this::getVariable).toArray(Expr[]::new);
            Symbol[] symbols = Arrays.stream(variables).map(var -> var.getFuncDecl().getName()).toArray(Symbol[]::new);
            Sort[] sorts = Arrays.stream(variables).map(Expr::getSort).toArray(Sort[]::new);

            tuple = z3Context.mkTupleSort(z3Context.mkSymbol("tuple"), symbols, sorts);
        }

        Expr[] distinct = new Expr[to - from + 1];
        for (int v = from, i = 0; v <= to; v++, i++) {
            int finalV = v;
            Expr[] vector = getVariables().stream().map(var -> getPrimedVariable(var, new TranslationOptions(finalV))).toArray(Expr[]::new);
            distinct[i] = tuple.mkDecl().apply(vector);
        }
        return z3Context.mkDistinct(distinct);
    }

    class SubstitutionToZ3TranslatorVisitor implements SubstitutionVisitor<BoolExpr, SubstitutionOptions> {

        private static final String CURRENTLY_NOT_SUPPORTED = "Currently not supported";

        @Override
        public BoolExpr visitAnySubstitution(AnySubstitutionNode node, SubstitutionOptions ops) {
            Expr[] parameters = new Expr[node.getParameters().size()];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = getVariableAsZ3Expression(node.getParameters().get(i));
            }
            BoolExpr parameterConstraints = FormulaToZ3Translator.translatePredicate(node.getWherePredicate(),
                    z3Context, z3TypeInference);
            BoolExpr transition = visitSubstitutionNode(node.getThenSubstitution(), ops);
            BoolExpr existsBody = z3Context.mkAnd(parameterConstraints, transition);
            return z3Context.mkExists(parameters, existsBody, parameters.length, null, null, null, null);
        }

        @Override
        public BoolExpr visitSelectSubstitutionNode(SelectSubstitutionNode node, SubstitutionOptions ops) {
            if (node.getConditions().size() > 1 || node.getElseSubstitution() != null) {
                throw new AssertionError(CURRENTLY_NOT_SUPPORTED);
            }
            BoolExpr condition = translatePredicate(node.getConditions().get(0), ops.getRhs());
            BoolExpr substitution = visitSubstitutionNode(node.getSubstitutions().get(0), ops);
            return z3Context.mkAnd(condition, substitution);
        }

        @Override
        public BoolExpr visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, SubstitutionOptions ops) {
            String name = getPrimedName(node.getIdentifier().getName(), ops.getLhs());
            BoolExpr assignment = FormulaToZ3Translator.translateVariableEqualToExpr(name, node.getValue(), z3Context,
                z3TypeInference);
            return substituteWithPrimedIfNecessary(assignment, ops.getRhs());
        }

        @Override
        public BoolExpr visitParallelSubstitutionNode(ParallelSubstitutionNode node, SubstitutionOptions ops) {
            List<SubstitutionNode> substitutions = node.getSubstitutions();
            BoolExpr boolExpr = null;
            for (SubstitutionNode substitutionNode : substitutions) {
                BoolExpr temp = visitSubstitutionNode(substitutionNode, ops);
                if (boolExpr == null) {
                    boolExpr = temp;
                } else {
                    boolExpr = z3Context.mkAnd(boolExpr, temp);
                }
            }
            return boolExpr;
        }

        @Override
        public BoolExpr visitConditionSubstitutionNode(ConditionSubstitutionNode node, SubstitutionOptions ops) {
            // PRE and ASSERT
            BoolExpr condition = translatePredicate(node.getCondition(), ops.getRhs());
            BoolExpr substitution = visitSubstitutionNode(node.getSubstitution(), ops);
            return z3Context.mkAnd(condition, substitution);
        }

        @Override
        public BoolExpr visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node,
                SubstitutionOptions ops) {
            if (node.getIdentifiers().size() > 1) {
                throw new AssertionError(CURRENTLY_NOT_SUPPORTED);
            }
            IdentifierExprNode identifierExprNode = node.getIdentifiers().get(0);
            String name = getPrimedName(identifierExprNode.getName(), ops.getLhs());
            return FormulaToZ3Translator.translateVariableElementOfSetExpr(name,
                identifierExprNode.getDeclarationNode(), node.getExpression(), z3Context, UNPRIMED,
                z3TypeInference);
        }

        @Override
        public BoolExpr visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node,
                                                             SubstitutionOptions ops) {
            throw new AssertionError(CURRENTLY_NOT_SUPPORTED);
        }

        @Override
        public BoolExpr visitIfSubstitutionNode(IfSubstitutionNode node, SubstitutionOptions ops) {
            if (node.getConditions().size() > 1) {
                // ELSIF THEN ...
                throw new AssertionError(CURRENTLY_NOT_SUPPORTED);
            }
            BoolExpr condition = translatePredicate(node.getConditions().get(0), ops.getRhs());
            BoolExpr substitution = visitSubstitutionNode(node.getSubstitutions().get(0), ops);
            List<BoolExpr> ifThenList = new ArrayList<>();
            ifThenList.add(condition);
            ifThenList.add(substitution);
            Set<DeclarationNode> set = new HashSet<>(node.getAssignedVariables());
            set.removeAll(node.getSubstitutions().get(0).getAssignedVariables());
            ifThenList.addAll(createDummyAssignment(set, ops));
            BoolExpr ifThen = z3Context.mkAnd(ifThenList.toArray(new BoolExpr[ifThenList.size()]));

            BoolExpr elseExpr = null;
            List<BoolExpr> elseList = new ArrayList<>();
            elseList.add(z3Context.mkNot(condition));
            if (null == node.getElseSubstitution()) {
                elseList.addAll(createDummyAssignment(node.getAssignedVariables(), ops));
            } else {
                elseList.add(visitSubstitutionNode(node.getElseSubstitution(), ops));
                Set<DeclarationNode> elseDummies = new HashSet<>(node.getAssignedVariables());
                elseDummies.removeAll(node.getElseSubstitution().getAssignedVariables());
                elseList.addAll(createDummyAssignment(elseDummies, ops));
            }
            elseExpr = z3Context.mkAnd(elseList.toArray(new BoolExpr[elseList.size()]));
            return z3Context.mkOr(ifThen, elseExpr);
        }

        @Override
        public BoolExpr visitSkipSubstitutionNode(SkipSubstitutionNode node, SubstitutionOptions ops) {
            return z3Context.mkBool(true);
        }

    }

    private String getPrimedName(String name, TranslationOptions ops) {
        if (ops.isHasPrimeLevel()) {
            return name + "'" + ops.getPrimeLevel();
        } else {
            return name;
        }
    }
}
