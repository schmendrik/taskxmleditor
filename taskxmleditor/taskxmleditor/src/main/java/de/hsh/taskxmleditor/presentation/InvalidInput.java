package de.hsh.taskxmleditor.presentation;

import java.awt.*;

public class InvalidInput {
    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        Error,
        Warning
    }

    private String id;
    private Component component;
    private String error;
    private ErrorType errorType = ErrorType.Error;

    public InvalidInput(String id, Component component, String error) {
        this(id, component, error, ErrorType.Error);
    }

    public InvalidInput(String id, Component component, String error, ErrorType errorType) {
        this.id = id;
        this.component = component;
        this.error = error;
        this.errorType = errorType;
    }

    public InvalidInput() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}
