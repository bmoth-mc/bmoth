package de.bmoth.parser.ast.nodes;

public interface OperatorNode<T> extends Node {
    T getOperator();

    void setOperator(T operator);
}
