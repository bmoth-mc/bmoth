package de.bmoth.parser.ast.types;

public class BoolType extends BasicType implements BType {

    private static BoolType instance = new BoolType();

    public static BoolType getInstance() {
        return instance;
    }

    private BoolType() {
        super("BOOL");
    }

    @Override
    public boolean unifiable(BType otherType) {
        return otherType == this || otherType instanceof UntypedType;
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (unifiable(otherType)) {
            if (otherType == instance) {
                return instance;
            } else {
                ((UntypedType) otherType).replaceBy(this);
                return instance;
            }
        } else {
            throw new UnificationException();
        }
    }
}
