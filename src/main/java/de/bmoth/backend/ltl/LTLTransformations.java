package de.bmoth.backend.ltl;


import com.google.common.reflect.ClassPath;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LTLTransformations {
    private final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private final Logger logger = Logger.getLogger(getClass().getName());

    private static LTLTransformations instance;

    private final List<ASTTransformation> transformationList;

    private LTLTransformations() {
        this.transformationList = new ArrayList<>();

        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith("de.bmoth.backend.ltl.transformation")) {
                    final Class<?> clazz = info.load();
                    transformationList.add((ASTTransformation) clazz.newInstance());
                }
            }
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error loading LTL transformation rules", e);
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
