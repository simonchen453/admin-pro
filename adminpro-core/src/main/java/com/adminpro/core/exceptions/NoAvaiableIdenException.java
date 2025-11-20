package com.adminpro.core.exceptions;

public class NoAvaiableIdenException extends BaseRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -5238838644466271832L;

    public NoAvaiableIdenException() {
        super("Invalid appIden in header.");
    }

}
