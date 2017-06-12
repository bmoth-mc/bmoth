package de.bmoth.parser.ast.types;

public class IntegerType extends BasicType implements BType {
    private static IntegerType instance = new IntegerType();

    public static IntegerType getInstance() {
        return instance;
    }

    private IntegerType() {
        super("INTEGER");
    }

    @Override
    public boolean unifiable(BType otherType) {
        return otherType == this || otherType instanceof UntypedType || otherType instanceof SetOrIntegerType
            || otherType instanceof IntegerOrSetOfPairs;
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (unifiable(otherType)) {
            if (otherType == instance) {
                return instance;
            } else if (otherType instanceof SetOrIntegerType) {
                return otherType.unify(this);
            } else if (otherType instanceof IntegerOrSetOfPairs) {
                return otherType.unify(this);
            } else {
                ((UntypedType) otherType).replaceBy(this);
                return instance;
            }
        } else {
            throw new UnificationException();
        }
    }
}
