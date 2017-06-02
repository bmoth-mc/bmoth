package de.bmoth.app;


import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.FuncInterp;
import com.microsoft.z3.Model;
import com.microsoft.z3.enumerations.Z3_sort_kind;

import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrettyPrinter {
    private final Logger logger = Logger.getLogger(PrettyPrinter.class.getName());
    private StringJoiner output = new StringJoiner(", ", "{", "}");

    public PrettyPrinter(Model model) {
        FuncDecl[] constantDeclarations = model.getConstDecls();
        for (FuncDecl constantDeclaration : constantDeclarations) {
            try {
                System.out.println("Declaration arity: " + constantDeclaration.getArity());
                System.out.println("Declaration sort: " + constantDeclaration.getRange().getSortKind());
                output.add(constantDeclaration.getName().toString() + "=");
                if (constantDeclaration.getRange().getSortKind() != Z3_sort_kind.Z3_ARRAY_SORT) {
                    Expr constantInterpretation = model.getConstInterp(constantDeclaration);
                    Expr[] constInterpretationArgs = constantInterpretation.getArgs();
                    if (constInterpretationArgs.length == 0) {
                        output.add(model.getConstInterp(constantDeclaration).toString());
                    } else {
                        for (Expr constInterpretationArg : constInterpretationArgs) {
                            System.out.println(constInterpretationArg);
                            System.out.println(constInterpretationArg.getSort());
                            output.add(processDeclaration(constInterpretationArg, model));
                        }
                    }
                } else {
                    FuncInterp functionInterpretation = model.getFuncInterp(constantDeclaration);
                    System.out.println(functionInterpretation);
                    for (FuncInterp.Entry funcInterpEntry : functionInterpretation.getEntries()) {
                        System.out.println(funcInterpEntry);
                    }
                }

            } catch (com.microsoft.z3.Z3Exception e) {
                logger.log(Level.SEVERE, "Z3 exception while solving", e);
            }
        }
    }


    public String processDeclaration(Expr constantInterpretation, Model model){
        System.out.println("Interpretation: " + constantInterpretation.toString());
        for (Expr interpretationArgs : constantInterpretation.getArgs()) {
            System.out.println(interpretationArgs);
        }
        constantInterpretation.getSort();
        if (constantInterpretation.getSort().toString().equals("couple")
            || constantInterpretation.getSort().toString().equals("set")) {
            // not a constant
            if (constantInterpretation.getSort().toString().equals("couple")) {
                // couple
                System.out.println("Couple");
                return formatCouple(constantInterpretation, model);
            } else {
                // set
                return formatSet(constantInterpretation);
            }
        } else {
            // constant
            System.out.println("Constant " + constantInterpretation.toString());
            return constantInterpretation.toString();
        }


        /*if (constantInterpretation.getRange().getSortKind() == Z3_sort_kind.Z3_ARRAY_SORT
            || constantInterpretation.getRange().getSortKind() == Z3_sort_kind.Z3_DATATYPE_SORT) {
            // not a constant
            if (constantInterpretation.getRange().getSortKind() == Z3_sort_kind.Z3_DATATYPE_SORT) {
                // couple
                System.out.println("Couple");
                if (constantInterpretation.getArity() == 0) {
                    return formatCouple(model.getConstInterp(constantInterpretation), model);
                } else {
                    System.out.println("Arity != 0");
                    FuncInterp interp =  model.getFuncInterp(constantInterpretation);
                    System.out.println("Interp: " + interp.toString());
                    return "1";
                }
            } else {
                // set
                return formatSet(model.getFuncInterp(constantInterpretation));
            }
        } else {
            System.out.println("Constant " + model.getConstInterp(constantInterpretation).toString());
            // constant
            return model.getConstInterp(constantInterpretation).toString();
        }*/
    }


    public String formatCouple(Expr constantInterpretation, Model model) {
        StringJoiner coupleJoiner = new StringJoiner(",", "(", ")");
        for (Expr element : constantInterpretation.getArgs()) {
            if (element.isNumeral()) {
                // System.out.println(element.toString() + " is numeral!");
                coupleJoiner.add(element.toString());
            } else {
                // System.out.println(element.toString() + " is not numeral!");
                coupleJoiner.add(processDeclaration(element, model));
            }
        }
        return coupleJoiner.toString();
    }

    public String formatSet(Expr constantInterpretation) {
        StringJoiner setJoiner = new StringJoiner(",", "{", "}");
        for (Expr constInterpretationArg : constantInterpretation.getArgs()) {
            setJoiner.add(constantInterpretation.toString());
        }
        return setJoiner.toString();
    }


    public String getOutput(){
        return output.toString();
    }


}
