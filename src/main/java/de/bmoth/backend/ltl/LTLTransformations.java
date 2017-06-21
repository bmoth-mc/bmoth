package de.bmoth.backend.ltl;

import de.bmoth.backend.ltl.transformation.ConvertFinallyGloballyFinallyToGloballyFinally;
import de.bmoth.backend.ltl.transformation.ConvertGloballyFinallyGloballyToFinallyGlobally;
import de.bmoth.backend.ltl.transformation.ConvertNotFinallyToGloballyNot;
import de.bmoth.backend.ltl.transformation.ConvertNotGloballyToFinallyNot;
import de.bmoth.backend.ltl.transformation.ConvertNotNextToNextNot;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.util.ArrayList;
import java.util.List;

public class LTLTransformations {
    private static LTLTransformations instance;

    private final List<AbstractASTTransformation> transformationList;

    private LTLTransformations() {
        this.transformationList = new ArrayList<>();
        transformationList.add(new ConvertNotGloballyToFinallyNot());
        transformationList.add(new ConvertNotFinallyToGloballyNot());
        transformationList.add(new ConvertNotNextToNextNot());
        transformationList.add(new ConvertFinallyGloballyFinallyToGloballyFinally());
        transformationList.add(new ConvertGloballyFinallyGloballyToFinallyGlobally());
    }

    public static LTLTransformations getInstance() {
        if (null == instance) {
            instance = new LTLTransformations();
        }
        return instance;
    }

    public static LTLNode transformLTLNode(LTLNode ltlNode) {
        LTLTransformations astTransformationForZ3 = LTLTransformations.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        return visitor.transformLTLNode(ltlNode);
    }

}
