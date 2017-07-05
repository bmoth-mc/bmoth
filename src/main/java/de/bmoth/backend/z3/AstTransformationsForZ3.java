package de.bmoth.backend.z3;

import com.google.common.reflect.ClassPath;

import de.bmoth.parser.ast.TypeChecker;
import de.bmoth.parser.ast.TypeErrorException;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AstTransformationsForZ3 {
    private final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private final Logger logger = Logger.getLogger(getClass().getName());

    private static AstTransformationsForZ3 instance;

    private final List<ASTTransformation> transformationList;

    private AstTransformationsForZ3() {
        this.transformationList = new ArrayList<>();

        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith("de.bmoth.backend.z3.transformation")) {
                    final Class<?> clazz = info.load();
                    transformationList.add((ASTTransformation) clazz.newInstance());
                }
            }
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error loading LTL transformation rules", e);
        }
    }

    private static AstTransformationsForZ3 getInstance() {
        if (null == instance) {
            instance = new AstTransformationsForZ3();
        }
        return instance;
    }

    public static MachineNode transformMachineNode(MachineNode machineNode) {
        AstTransformationsForZ3 astTransformationForZ3 = AstTransformationsForZ3.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        visitor.transformMachine(machineNode);
        try {
            TypeChecker.typecheckMachineNode(machineNode);
        } catch (TypeErrorException e) {
            // a type error should only occur when the AST transformation is
            // invalid
            throw new AssertionError(e);
        }
        return machineNode;
    }

    public static FormulaNode transformFormulaNode(FormulaNode formulaNode) {
        AstTransformationsForZ3 astTransformationForZ3 = AstTransformationsForZ3.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        visitor.transformFormula(formulaNode);
        try {
            TypeChecker.typecheckFormulaNode(formulaNode);
        } catch (TypeErrorException e) {
            // a type error should only occur when the AST transformation is
            // invalid
            throw new AssertionError(e);
        }
        return formulaNode;
    }
}
