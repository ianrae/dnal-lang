package org.dnal.api;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.NewErrorMessage;


public class WorldException extends Exception {
    public List<NewErrorMessage> errL = new ArrayList<>();
    

    public WorldException(String message) {
        super(message);
    }
    public WorldException(String message, List<NewErrorMessage> errL) {
        super(message);
        this.errL = errL;
    }
    
    /**
     */
    private static final long serialVersionUID = 1L;
}