package de.bmoth.backend.ltl;



import java.util.ArrayList;
import java.util.List;

import de.bmoth.backend.ltl.transformation.*;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

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
        transformationList.add(new ConvertGloballyGloballyToGlobally());
        transformationList.add(new ConvertFinallyFinallyToFinally());
        transformationList.add(new ConvertPhiUntilPhiUntilPsiToPhiUntilPsi());
        transformationList.add(new ConvertNextPhiUntilPsiToNextPhiUntilNextPsi());
        transformationList.add(new ConvertFinallyPhiOrPsiToFinallyPhiOrFinallyPsi());
        transformationList.add(new ConvertGloballyPhiAndPsiToGloballyPhiAndGloballyPsi());
        transformationList.add(new ConvertNotUntil());
        transformationList.add(new ConvertNotWeakUntil());
        transformationList.add(new ConvertFinallyPhiToTrueUntilPhi());
        transformationList.add(new ConvertGloballyPhiToPhiWeakUntilFalse());
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
