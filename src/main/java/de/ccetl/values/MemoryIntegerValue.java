package de.ccetl.values;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import de.ccetl.AppMemory;

import java.nio.ByteBuffer;

public class MemoryIntegerValue extends MemoryValue<Integer> {
    public MemoryIntegerValue(AppMemory memory, String baseAddress, String... offsetStrings) {
        super(memory, baseAddress, offsetStrings);
    }

    public MemoryIntegerValue(AppMemory memory, long baseAddress, int... offsets) {
        super(memory, baseAddress, offsets);
    }

    public MemoryIntegerValue(AppMemory memory, Pointer pointer) {
        super(memory, pointer);
    }

    @Override
    public Integer readValue() {
        Memory memory = getMemory().readMemory(getPointer(), 4);
        Integer i = memory.getInt(0);
        memory.close();
        return i;
    }

    @Override
    public void writeValue(Integer value) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        getMemory().writeMemory(getPointer(), bytes);
    }
}
