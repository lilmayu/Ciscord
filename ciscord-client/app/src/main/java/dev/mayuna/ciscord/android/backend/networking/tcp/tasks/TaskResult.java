package dev.mayuna.ciscord.android.backend.networking.tcp.tasks;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaskResult {

    private boolean success;
    private String errorMessage;
    private Exception exception;

    private TaskResult(boolean success, String errorMessage, Exception exception) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.exception = exception;
    }

    public static TaskResult success() {
        return new TaskResult(true, null, null);
    }

    public static TaskResult failure(String errorMessage) {
        return new TaskResult(false, errorMessage, null);
    }

    public static TaskResult failure(Exception exception) {
        return new TaskResult(false, null, exception);
    }

    public static TaskResult failure(String errorMessage, Exception exception) {
        return new TaskResult(false, errorMessage, exception);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        if (errorMessage == null) {
            return "Exception: " + exception.getMessage();
        }

        return errorMessage;
    }

    public Exception getException() {
        return exception;
    }
}
