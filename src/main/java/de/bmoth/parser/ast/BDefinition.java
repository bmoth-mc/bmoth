package de.bmoth.parser.ast;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.bmoth.antlr.BMoThParser.OrdinaryDefinitionContext;

public class BDefinition {

    public enum KIND {
        SUBSTITUTION, EXPRESSION, PREDICATE, UNKNOWN
    }

    private KIND kind;
    private OrdinaryDefinitionContext definitionContext;
    private final int arity;
    private final String name;
    private final List<TerminalNode> parameters;

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
        if (null == def.identifier_list()) {
            this.arity = 0;
            this.parameters = new ArrayList<>();
        } else {
            this.arity = def.identifier_list().IDENTIFIER().size();
            this.parameters = def.identifier_list().IDENTIFIER();
        }
    }

    public List<TerminalNode> getParameters() {
        return this.parameters;
    }
}
