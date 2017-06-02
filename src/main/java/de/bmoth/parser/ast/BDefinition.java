package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser.OrdinaryDefinitionContext;

public class BDefinition {

    public enum KIND {
        SUBSTITUTION, EXPRESSION, PREDICATE, UNKNOWN
    }

    private KIND kind;
    private OrdinaryDefinitionContext definitionContext;
    private final int arity;
    private final String name;

    public KIND getKind() {
        return kind;
    }

    public void setKind(KIND kind) {
        this.kind = kind;
    }

    public OrdinaryDefinitionContext getDefinitionContext() {
        return definitionContext;
    }

    public void setDefinitionContext(OrdinaryDefinitionContext definitionContext) {
        this.definitionContext = definitionContext;
    }

    public int getArity() {
        return arity;
    }

    public String getName() {
        return name;
    }

    public BDefinition(String name, OrdinaryDefinitionContext def, KIND kind) {
        this.name = name;
        this.definitionContext = def;
        this.kind = kind;
        this.arity = def.parameters.size();
    }
}
