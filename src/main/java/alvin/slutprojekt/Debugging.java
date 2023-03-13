package alvin.slutprojekt;

import java.util.Calendar;

/**
 * Utilities and settings for debugging the application.
 */
public class Debugging {
    public static final boolean DEBUGGING_PACKETS = false;
    public static final boolean DEBUG_MESSAGES = true;

    public static String time() {
        return Calendar.getInstance().get(Calendar.SECOND) + "." + Calendar.getInstance().get(Calendar.MILLISECOND) + " ";
    }

    /**
     * Print the string if debugging is enabled.
     *
     * @param text The text to print.
     */
    public static void println(String text) {
        if (DEBUG_MESSAGES) {
            System.out.println(text);
        }
    }
}
