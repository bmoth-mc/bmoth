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
            output.add(constantDeclaration.getName().toString() + "=" + processDeclaration(constantDeclaration, model));
        }
    }

    
    public String processDeclaration(FuncDecl constantDeclaration, Model model) {
        try {
            if (constantDeclaration.getRange().getSortKind() != Z3_sort_kind.Z3_ARRAY_SORT) {
                Expr constantInterpretation = model.getConstInterp(constantDeclaration);
                if (constantInterpretation.getArgs().length == 0) // constant
                    return model.getConstInterp(constantDeclaration).toString();
                else // couple
                    return formatCouple(constantInterpretation, model);
            } else // set
                return (formatSet(model.getFuncInterp(constantDeclaration), model));
        } catch (com.microsoft.z3.Z3Exception e) {
            logger.log(Level.SEVERE, "Z3 exception while solving", e);
            return null;
        }

    }


    public String processInterpretation(Expr interpretation, Model model){
        if (interpretation.getSort().getSortKind() == Z3_sort_kind.Z3_DATATYPE_SORT)
            return formatCouple(interpretation, model);
        else if (interpretation.getSort().getSortKind() == Z3_sort_kind.Z3_ARRAY_SORT)
            return formatSet(model.getFuncInterp(interpretation.getFuncDecl()), model);
        else // constant
            return interpretation.toString();
    }


    public String formatCouple(Expr interpretation, Model model) {
        StringJoiner coupleJoiner = new StringJoiner(",", "(", ")");
        for (Expr element : interpretation.getArgs()) {
            if (element.isNumeral())
                coupleJoiner.add(element.toString());
            else coupleJoiner.add(processInterpretation(element, model));
        }
        return coupleJoiner.toString();
    }


    public String formatSet(FuncInterp interpretation, Model model) {
        StringJoiner setJoiner = new StringJoiner(",", "{", "}");
        if (interpretation != null) {
            for (FuncInterp.Entry interpretationEntry : interpretation.getEntries()) {
                if (interpretationEntry.getArgs()[0].isNumeral())
                    setJoiner.add(interpretationEntry.getArgs()[0].toString());
                else setJoiner.add(processInterpretation(interpretationEntry.getArgs()[0], model));
            }
        }
        return setJoiner.toString();
    }


    public String getOutput(){
        return output.toString();
    }


}
