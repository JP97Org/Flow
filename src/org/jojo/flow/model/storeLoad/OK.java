package org.jojo.flow.model.storeLoad;

import java.util.function.Function;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.flowChart.FlowChartElement;

public final class OK {
    public static final String ERR_MSG_WRONG_CAST = "a class cast exc would occur";
    public static final String ERR_MSG_NULL = "a null pointer exc would occur";
    public static final String ERR_MSG_EXC = "this exception would occur";
    public static final String ERR_MSG_DOM_NOT_VALID = "DOM not valid";
    
    private OK() {
        
    }
    
    public static boolean ok(final boolean isOk, final String errMsg) throws ParsingException {
        if (isOk) {
            return isOk;
        }
        throw new ParsingException(new Warning(null, errMsg, true));
    }
    
    public static boolean ok(final boolean isOk, final String errMsg, final FlowChartElement affectedElem) throws ParsingException {
        if (isOk) {
            return isOk;
        }
        throw new ParsingException(new Warning(affectedElem, errMsg, true));
    }
    
    public static <A,B> B ok(final Function<A,B> fct, final A input) throws ParsingException {
        try {
            return fct.apply(input);
        } catch (Exception e) {
            throw new ParsingException(new Warning(null, ERR_MSG_EXC + " : " + e.getClass().getSimpleName() + " : " + e.getMessage(), true));
        }
    }
}
