package de.bmoth.backend.ltl;


import com.google.common.reflect.ClassPath;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LTLTransformations {
    final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private final List<AbstractASTTransformation> transformationList;

    private static LTLTransformations instance;

    private LTLTransformations() {
        this.transformationList = new ArrayList<>();

        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith("de.bmoth.backend.ltl.transformation")) {
                    final Class<?> clazz = info.load();
                    transformationList.add((AbstractASTTransformation) clazz.newInstance());
                    // do something with your clazz
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static LTLTransformations getInstance() {
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
