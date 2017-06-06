package de.bmoth.parser.ast.nodes;

public interface OperatorNode<OpType> extends Node {
    OpType getOperator();

    void setOperator(OpType operator);
}
