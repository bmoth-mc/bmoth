package de.bmoth.backend.z3;

import de.bmoth.backend.z3.transformation.ConvertElementOfUnionToMultipleElementOfs;
import de.bmoth.backend.z3.transformation.ConvertMemberOfIntegerSetToLeqGeq;
import de.bmoth.backend.z3.transformation.ConvertMemberOfIntervalToLeqAndGeq;
import de.bmoth.backend.z3.transformation.ConvertNestedUnionsToUnionList;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.util.ArrayList;
import java.util.List;

public class AstTransformationsForZ3 {
    private static AstTransformationsForZ3 instance;

    private final List<AbstractASTTransformation> transformationList;

    private AstTransformationsForZ3() {
        this.transformationList = new ArrayList<>();
        transformationList.add(new ConvertNestedUnionsToUnionList());
        transformationList.add(new ConvertElementOfUnionToMultipleElementOfs());
        transformationList.add(new ConvertMemberOfIntervalToLeqAndGeq());
        transformationList.add(new ConvertMemberOfIntegerSetToLeqGeq());
    }

    public static AstTransformationsForZ3 getInstance() {
        if (null == instance) {
            instance = new AstTransformationsForZ3();
        }
        return instance;
    }

    public static PredicateNode transformPredicate(PredicateNode predNode) {
        AstTransformationsForZ3 astTransformationForZ3 = AstTransformationsForZ3.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        return visitor.transformPredicate(predNode);
    }

    public static ExprNode transformExprNode(ExprNode value) {
        AstTransformationsForZ3 astTransformationForZ3 = AstTransformationsForZ3.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        return visitor.transformExpr(value);
    }

}
