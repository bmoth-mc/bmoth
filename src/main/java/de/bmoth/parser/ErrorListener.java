package de.bmoth.parser;

import de.bmoth.exceptions.ParseErrorException;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        CommonToken token = (CommonToken) offendingSymbol;
        if (null == e) {
            return;
        }
        throw new ParseErrorException(token, msg);
    }

}
