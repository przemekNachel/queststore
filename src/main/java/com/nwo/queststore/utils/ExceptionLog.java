package main.java.com.nwo.queststore.utils;

import main.java.com.nwo.queststore.model.GroupModel;

public class ExceptionLog {

    private static GroupModel<Exception> exceptions = new GroupModel<>("exception container");

    public static void add(Exception e) {

        ExceptionLog.exceptions.add(e);
    }

    public static GroupModel<Exception> getExceptions() {

        return ExceptionLog.exceptions;
    }
}
