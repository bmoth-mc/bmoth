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
                StringJoiner declarationOutput;
                if (constantDeclaration.getRange().getSortKind() != Z3_sort_kind.Z3_ARRAY_SORT) {
                    // not a set (couple or constant)
                    Expr constantInterpretation = model.getConstInterp(constantDeclaration);
                    Expr[] constInterpretationArgs = constantInterpretation.getArgs();
                    if (constInterpretationArgs.length == 0) {
                        // constant
                        declarationOutput = new StringJoiner(", ", "", "");
                        output.add(declarationOutput.add(constantDeclaration.getName().toString() + "=" +
                            model.getConstInterp(constantDeclaration).toString()).toString());
                    } else {
                        // couple
                        declarationOutput = new StringJoiner(", ", "(", ")");
                        for (Expr constInterpretationArg : constInterpretationArgs) {
                            declarationOutput.add(processDeclaration(constInterpretationArg, model));
                        }
                        output.add(constantDeclaration.getName().toString() + "=" + declarationOutput.toString());
                    }
                } else {
                    // set
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


    public String processDeclaration(Expr interpretation, Model model){
        System.out.println("Interpretation: " + interpretation.toString());
        for (Expr interpretationArgs : interpretation.getArgs()) {
            System.out.println(interpretationArgs);
        }
        interpretation.getSort();
        if (interpretation.getSort().toString().equals("couple")) {
            System.out.println("Couple");
            return formatCouple(interpretation, model);
        } else if (interpretation.getSort().toString().equals("set")) {
            System.out.println("Set");
            return formatSet(interpretation);
        } else {
            // constant
            System.out.println("Constant " + interpretation.toString());
            return interpretation.toString();
        }
    }


    public String formatCouple(Expr interpretation, Model model) {
        StringJoiner coupleJoiner = new StringJoiner(",", "(", ")");
        for (Expr element : interpretation.getArgs()) {
            if (element.isNumeral()) {
                coupleJoiner.add(element.toString());
            } else {
                coupleJoiner.add(processDeclaration(element, model));
            }
        }
        return coupleJoiner.toString();
    }


    public String formatSet(Expr interpretation) {
        StringJoiner setJoiner = new StringJoiner(",", "{", "}");
        for (Expr constInterpretationArg : interpretation.getArgs()) {
            setJoiner.add(constInterpretationArg.toString());
        }
        return setJoiner.toString();
    }


    public String getOutput(){
        return output.toString();
    }


}
