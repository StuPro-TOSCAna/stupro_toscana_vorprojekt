package de.toscana.transformator.util;

/**
 * Misc. utility methods for checking multiple variables at once
 */
public class CheckUtils {
    /**
     * Nullchecks multiple objects. if one is null false is returned true otherwise
     * @param objects
     * @return
     */
    public static boolean checkNull(Object... objects) {
        for (Object object : objects) {
            if(object == null) {
                return false;
            }
        }
        return true;
    }
}
