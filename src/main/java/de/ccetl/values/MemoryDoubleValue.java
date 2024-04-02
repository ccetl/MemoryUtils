package de.ccetl.values;

import com.sun.jna.Pointer;
import de.ccetl.AppMemory;

// TODO
public class MemoryDoubleValue extends MemoryValue<Double> {
    public MemoryDoubleValue(AppMemory memory, String baseAddress, String... offsetStrings) {
        super(memory, baseAddress, offsetStrings);
    }

    public MemoryDoubleValue(AppMemory memory, long baseAddress, int... offsets) {
        super(memory, baseAddress, offsets);
    }

    public MemoryDoubleValue(AppMemory memory, Pointer pointer) {
        super(memory, pointer);
    }

    @Override
    public Double readValue() {
        return null;
    }

    @Override
    public void writeValue(Double value) {

    }
}
