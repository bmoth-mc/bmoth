package de.bmoth.parser.ast.types;

import java.util.ArrayList;
import java.util.List;

public class EnumeratedSetElementType extends SetElementType implements BType {
    private final List<String> elements;

    public EnumeratedSetElementType(String name, List<String> list) {
        super(name);
        this.elements = list;
    }

    public List<String> getElements() {
        return new ArrayList<>(this.elements);
    }

}
