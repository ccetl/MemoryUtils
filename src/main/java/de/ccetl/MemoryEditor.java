package de.ccetl;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;

/**
 * A clas to load the kernel32 dll and initialize it.
 */
public class MemoryEditor {
    public static Kernel32 kernel32;

    /**
     * This shouldn't get called.
     */
    private MemoryEditor() {
        throw new RuntimeException("You can't create an instance of this class!");
    }

    /**
     * Loads the kernel32.dll.
     */
    public static void init() {
        kernel32 = Native.load("kernel32", Kernel32.class);
    }
}
