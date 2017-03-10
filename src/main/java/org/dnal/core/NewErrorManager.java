package org.dnal.core;

public class NewErrorManager {

    public static NewErrorMessage OldErrorMsg(ErrorType errType, String message) {
        NewErrorMessage err = new NewErrorMessage();
        err.setErrorType(NewErrorMessage.Type.IO_ERROR); //!!
        err.setErrorName(errType.name());
        err.setFieldName("?");
        err.setMessage(message);
        err.setSrcFile("?");
        err.setTypeName("?");
        return err;
    }
	
}
