package de.ccetl.values;

import com.sun.jna.Pointer;
import de.ccetl.AppMemory;

/**
 * This abstract class provides a foundation for representing and interacting
 * with values stored in memory within a specific application's process space.
 */
public abstract class MemoryValue<T> {
    /**
     * Reference to the AppMemory object associated with the target process.
     */
    private final AppMemory memory;
    /**
     * Pointer object pointing to the specific memory address where the value resides.
     */
    private final Pointer pointer;

    /**
     * Initializes a MemoryValue object from a base address string and optional offset strings.
     * <p>
     * This constructor parses the base address string as a hexadecimal number and converts
     * the provided offset strings (also in hexadecimal) to an integer array.
     * It then uses the AppMemory object to find the final memory address by applying the
     * offsets to the base address.
     *
     * @param memory the process containing the address
     * @param baseAddress the base address string in hexadecimal format
     * @param offsetStrings optional offset strings in hexadecimal format (can be empty)
     */
    public MemoryValue(AppMemory memory, String baseAddress, String... offsetStrings) {
        this(memory, Long.parseLong(baseAddress, 16), getOffsets(offsetStrings));
    }

    /**
     * Initializes a MemoryValue object from a base address long and optional offset integers.
     * <p>
     * This constructor directly uses the provided base address (as a long) and offset integers
     * to find the final memory address using the AppMemory object.
     *
     * @param memory the process containing the address
     * @param baseAddress the base address as a long value
     * @param offsets optional offset integers (can be empty)
     */
    public MemoryValue(AppMemory memory, long baseAddress, int... offsets) {
        this.memory = memory;
        Pointer pointer = memory.findAddress(baseAddress, offsets);
        System.out.println("Found Address " + pointer);
        this.pointer = pointer;
    }

    /**
     * Directly constructs a MemoryValue and won't search for the dynamic address.
     * <p>
     * This constructor is useful when the memory address is already known beforehand.
     * It takes the AppMemory object and the pointer directly.
     *
     * @param memory the process containing the address
     * @param pointer the pointer to the value
     */
    public MemoryValue(AppMemory memory, Pointer pointer) {
        this.memory = memory;
        this.pointer = pointer;
    }

    /**
     * Converts an array of hexadecimal strings representing offsets into an integer array.
     * <p>
     * This helper method parses each string in the offsetStrings array as a hexadecimal number
     * and stores the parsed integer value in the resulting offsets array.
     *
     * @param offsetStrings the array of offset strings in hexadecimal format
     * @return the integer array containing the parsed offsets
     */
    private static int[] getOffsets(String[] offsetStrings) {
        int[] offsets = new int[offsetStrings.length];
        for (int i = 0; i < offsetStrings.length; i++) {
            offsets[i] = Integer.parseInt(offsetStrings[i], 16);
        }
        return offsets;
    }

    /**
     * Reads the value stored at the memory address pointed to by this MemoryValue object.
     *
     * @return the value read from memory
     */
    public abstract T readValue();

    /**
     * Writes a new value to the memory address.
     *
     * @param value the value to write
     */
    public abstract void writeValue(T value);

    /**
     * Returns the AppMemory object associated with this MemoryValue object.
     *
     * @return the AppMemory object
     */
    public AppMemory getMemory() {
        return memory;
    }

    /**
     * Returns the pointer object pointing to the memory address associated with
     * this MemoryValue object.
     *
     * @return the pointer object
     */
    public Pointer getPointer() {
        return pointer;
    }
}
