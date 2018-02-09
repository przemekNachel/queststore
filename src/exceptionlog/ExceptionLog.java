package exceptionlog;

import generic_group.Group;

public class ExceptionLog {

    private static Group<Exception> exceptions;

    public static void add(Exception e) {

        ExceptionLog.exceptions.add(e);
    }

    public static Group<Exception> getExceptions() {

        return ExceptionLog.exceptions;
    }
}
