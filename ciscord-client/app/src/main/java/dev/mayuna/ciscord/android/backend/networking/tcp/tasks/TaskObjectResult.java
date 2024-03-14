package dev.mayuna.ciscord.android.backend.networking.tcp.tasks;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaskObjectResult<T> extends TaskResult {

    private T result;

    protected TaskObjectResult(boolean success, String errorMessage, Exception exception) {
        super(success, errorMessage, exception);
    }

    public static <T> TaskObjectResult<T> objectSuccess(T result) {
        TaskObjectResult<T> taskResult = new TaskObjectResult<>(true, null, null);
        taskResult.result = result;
        return taskResult;
    }

    public static <T> TaskObjectResult<T> objectFailure(String errorMessage) {
        return new TaskObjectResult<>(false, errorMessage, null);
    }

    public static <T> TaskObjectResult<T> objectFailure(Exception exception) {
        return new TaskObjectResult<>(false, null, exception);
    }

    public static <T> TaskObjectResult<T> objectFailure(String errorMessage, Exception exception) {
        return new TaskObjectResult<>(false, errorMessage, exception);
    }

    public T getResult() {
        return result;
    }
}
