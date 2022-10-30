package org.elective.tags;

import java.util.List;

public class IsCheckFunction {

    public static String isCheck(String[] list, String value) {
        String result = "";
        if (list != null) {
            for (String s : list) {
                if (s.equals(value)) {
                    result = "checked";
                    break;
                }
            }
        }
        return result;
    }
}
