package de.bmoth.exceptions;

import com.google.common.eventbus.EventBus;
import de.bmoth.app.EventBusProvider;
import org.antlr.v4.runtime.CommonToken;

public class ParseErrorException extends RuntimeException {
    private static final long serialVersionUID = 2305560853973886094L;
    private CommonToken token;

    public ParseErrorException(CommonToken token, String message) {
        super(message);
        this.token = token;
        EventBus eventBus = EventBusProvider.getInstance().getEventBus();
        eventBus.post(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parse error: Unexpected input '").append(token.getText()).append("' ");
        sb.append("in line ").append(token.getLine());
        sb.append(" column " + token.getCharPositionInLine()).append(".\n");
        sb.append("Additional information: ").append(super.getMessage());
        System.out.println(sb.toString());
        return sb.toString();
    }
}
