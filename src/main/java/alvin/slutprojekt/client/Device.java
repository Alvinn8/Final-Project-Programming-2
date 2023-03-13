package alvin.slutprojekt.client;

import java.util.Locale;

/**
 * Util constants about the device running the game.
 */
public class Device {
    /**
     * Whether the operating system of the device is macOS.
     */
    public static final boolean IS_MAC
        = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");

    /**
     * Whether the operating system of the device is Windows.
     */
    public static final boolean IS_WINDOWS
        = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
}
