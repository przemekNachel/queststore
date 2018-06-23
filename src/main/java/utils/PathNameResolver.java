package utils;

import java.util.HashMap;

public class PathNameResolver {


    public static final String MENTOR = "mentor";
    public static final String STUDENT = "codecooler";
    public static final String ADMIN = "admin";
    public static final String INDEX = "index";
    public static HashMap<String, String> pathNameMapping;

    static {
        pathNameMapping = new HashMap<String, String>(200) {
            private void put1(String key, String value) {
                if (put(key, value) != null) {
                    throw new IllegalArgumentException("Duplicated name: " + key);
                }
            }

            {
                put1("/", INDEX);
                put1("/admin", ADMIN);
                put1("/student", STUDENT);
                put1("/mentor", MENTOR);
            }
        };
    }

}
