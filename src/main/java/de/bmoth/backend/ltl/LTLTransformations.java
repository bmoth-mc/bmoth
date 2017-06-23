package de.bmoth.backend.ltl;

<<<<<<< HEAD

import de.bmoth.backend.ltl.transformation.ConvertFinallyGloballyFinallyToGloballyFinally;
import de.bmoth.backend.ltl.transformation.ConvertGloballyFinallyGloballyToFinallyGlobally;
import de.bmoth.backend.ltl.transformation.ConvertNotFinallyToGloballyNot;
import de.bmoth.backend.ltl.transformation.ConvertFinallyFinallyToFinally;
import de.bmoth.backend.ltl.transformation.ConvertGloballyGloballyToGlobally;
import de.bmoth.backend.ltl.transformation.ConvertNotGloballyToFinallyNot;
import de.bmoth.backend.ltl.transformation.ConvertNotNextToNextNot;
import de.bmoth.backend.ltl.transformation.ConvertPhiUntilPhiUntilPsiToPhiUntilPsi;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
=======
import de.bmoth.backend.ltl.transformation.*;
>>>>>>> 0500f2415138555a9f7dcecad560111b3bd4f6cd
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
        transformationList.add(new ConvertGloballyGloballyToGlobally());
        transformationList.add(new ConvertFinallyFinallyToFinally());
        transformationList.add(new ConvertPhiUntilPhiUntilPsiToPhiUntilPsi());
        transformationList.add(new ConvertNextPhiUntilPsiToNextPhiUntilNextPsi());
        transformationList.add(new ConvertFinallyPhiOrPsiToFinallyPhiOrFinallyPsi());
        transformationList.add(new ConvertGloballyPhiAndPsiToGloballyPhiAndGloballyPsi());
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
