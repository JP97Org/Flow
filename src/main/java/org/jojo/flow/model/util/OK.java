package org.jojo.flow.model.util;

import java.util.function.Function;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IFlowChartElement;

/**
 * This utility class can be used for checking correctness of booleans and method executions, 
 * throwing a {@link org.jojo.flow.exc.ParsingException} if incorrectness is detected.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public final class OK {
    public static final String ERR_MSG_WRONG_CAST = "a class cast exc would occur";
    public static final String ERR_MSG_NULL = "a null pointer exc would occur";
    public static final String ERR_MSG_EXC = "this exception would occur";
    public static final String ERR_MSG_DOM_NOT_VALID = "DOM not valid";
    
    private OK() {
        
    }
    
    /**
     * Determines whether a check is ok, i.e. the given {@code isOk} is {@code true}. Throws a
     * {@link org.jojo.flow.exc.ParsingException} if not so.
     * 
     * @param isOk - if the check is ok
     * @param errMsg - the error message if the check is not ok
     * @return {@code isOk} if {@code isOk} is {@code true}
     * @throws ParsingException if {@code isOk} is {@code false}
     */
    public static boolean ok(final boolean isOk, final String errMsg) throws ParsingException {
        if (isOk) {
            return isOk;
        }
        throw new ParsingException(new Warning(null, errMsg, true));
    }
    
    /**
     * Determines whether a check is ok, i.e. the given {@code isOk} is {@code true}. Throws a
     * {@link org.jojo.flow.exc.ParsingException} if not so.
     * 
     * @param isOk - if the check is ok
     * @param errMsg - the error message if the check is not ok
     * @param affectedElem - the affected flow chart element
     * @return {@code isOk} if {@code isOk} is {@code true}
     * @throws ParsingException if {@code isOk} is {@code false}
     */
    public static boolean ok(final boolean isOk, final String errMsg, final IFlowChartElement affectedElem) throws ParsingException {
        if (isOk) {
            return isOk;
        }
        throw new ParsingException(new Warning(affectedElem, errMsg, true));
    }
    
    /**
     * Applies the given function to the given input. Throws a 
     * {@link org.jojo.flow.exc.ParsingException} if an exception occurs during execution of the function.
     * 
     * @param <A> - the generic parameter for the input
     * @param <B> - the generic parameter for the output
     * @param fct - the function
     * @param input - the input
     * @return the result of the function
     * @throws ParsingException if an exception occurs during the execution of the function
     */
    public static <A,B> B ok(final Function<A,B> fct, final A input) throws ParsingException {
        try {
            return fct.apply(input);
        } catch (Exception e) {
            throw new ParsingException(new Warning(null, ERR_MSG_EXC + " : " + e.getClass().getSimpleName() + " : " + e.getMessage(), true));
        }
    }
}
