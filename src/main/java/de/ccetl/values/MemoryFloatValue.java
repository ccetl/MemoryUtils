package de.ccetl.values;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import de.ccetl.AppMemory;

import java.nio.ByteBuffer;

public class MemoryFloatValue extends MemoryValue<Float> {
    public MemoryFloatValue(AppMemory memory, String baseAddress, String... offsetStrings) {
        super(memory, baseAddress, offsetStrings);
    }

    public MemoryFloatValue(AppMemory memory, long baseAddress, int... offsets) {
        super(memory, baseAddress, offsets);
    }

    public MemoryFloatValue(AppMemory memory, Pointer pointer) {
        super(memory, pointer);
    }

    @Override
    public Float readValue() {
        Memory memory = getMemory().readMemory(getPointer(), 4);
        Float f = memory.getFloat(0);
        memory.close();
        return f;
    }

    @Override
    public void writeValue(Float value) {
        byte[] bytes = ByteBuffer.allocate(4).putFloat(value).array();
        getMemory().writeMemory(getPointer(), bytes);
    }
}
