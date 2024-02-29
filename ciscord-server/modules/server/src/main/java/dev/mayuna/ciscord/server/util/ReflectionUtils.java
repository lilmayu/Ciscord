package dev.mayuna.ciscord.server.util;

public class ReflectionUtils {

    public static String getMethodNameByIndex(int index) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[index];
        return getLastElementOfClassHierarchy(stackTraceElement.getClassName()) + ": " + stackTraceElement.getMethodName();
    }

    private static String getLastElementOfClassHierarchy(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }
}