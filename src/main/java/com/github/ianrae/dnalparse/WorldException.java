package com.github.ianrae.dnalparse;

import java.util.ArrayList;
import java.util.List;

import org.dval.ErrorMessage;


public class WorldException extends Exception {
    public List<ErrorMessage> errL = new ArrayList<>();
    

    public WorldException(String message) {
        super(message);
    }
    public WorldException(String message, List<ErrorMessage> errL) {
        super(message);
        this.errL = errL;
    }
    
    /**
     */
    private static final long serialVersionUID = 1L;
}