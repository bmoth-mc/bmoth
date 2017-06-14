package de.bmoth.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class ErrorListener extends BaseErrorListener {

    class VisitorException extends RuntimeException {
        private static final long serialVersionUID = -3388334148890725470L;
        private final ParseErrorException parseErrorException;

        VisitorException(ParseErrorException parseErrorException) {
            this.parseErrorException = parseErrorException;
        }

        public ParseErrorException getParseErrorException() {
            return this.parseErrorException;
        }
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        CommonToken token = (CommonToken) offendingSymbol;
        throw new VisitorException(new ParseErrorException(token, msg));
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
            java.util.BitSet conflictingAlts, ATNConfigSet configs) {
        // log ambiguity
    }

}
