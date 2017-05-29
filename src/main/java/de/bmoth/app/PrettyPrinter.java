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
        FuncDecl[] functionDeclarations = model.getConstDecls();
        for (FuncDecl decl : functionDeclarations) {
            try {
                output.add(decl.getName().toString() + "=" + processDeclaration(decl, model));
            } catch (com.microsoft.z3.Z3Exception e) {
                logger.log(Level.SEVERE, "Z3 exception while solving", e);
            }
        }
    }


    public String processDeclaration(FuncDecl decl, Model model){
        if (decl.getArity() == 0 && (decl.getRange().getSortKind() == Z3_sort_kind.Z3_ARRAY_SORT
            || decl.getRange().getSortKind() == Z3_sort_kind.Z3_DATATYPE_SORT)) {
            // not a constant
            StringJoiner presentation = new StringJoiner(", ", "", "");
            if (decl.getRange().getSortKind() == Z3_sort_kind.Z3_DATATYPE_SORT) {
                // couple
                presentation.add(formatCouples(model.getConstInterp(decl)));
            } else {
                // set
                presentation.add(formatSets(model.getFuncInterp(decl)));
            }
            return presentation.toString();
        } else {
            // constant
            return model.getConstInterp(decl).toString();
        }
    }


    public String formatCouples(Expr constantArg) {
        StringJoiner coupleJoiner = new StringJoiner(",", "(", ")");
        for (Expr element : constantArg.getArgs()) {
            if (element.isNumeral()) {
                coupleJoiner.add(element.toString());
            } else {
                coupleJoiner.add(formatCouples(element));
            }
        }
        return coupleJoiner.toString();
    }

    public String formatSets(FuncInterp funcInterpretation) {
        StringJoiner setJoiner = new StringJoiner(",", "{", "}");
        for (FuncInterp.Entry entry : funcInterpretation.getEntries()) {
            for (Expr entryArg : entry.getArgs()) {
                setJoiner.add(entryArg.toString());
            }
        }
        return setJoiner.toString();
    }


    public String getOutput(){
        return output.toString();
    }


}
