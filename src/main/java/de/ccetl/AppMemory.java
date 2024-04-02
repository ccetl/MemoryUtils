package de.ccetl;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.ptr.IntByReference;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;

import static com.sun.jna.platform.win32.WinNT.*;
import static de.ccetl.MemoryEditor.kernel32;

/**
 * A reference to a process.
 */
public class AppMemory {
    /**
     * The handle for the process.
     */
    protected final HANDLE handle;
    /**
     * The current pointer size.
     */
    protected int pointerSize = 8;

    /**
     * Constructs a new AppMemory object.
     *
     * @param name of the process
     */
    public AppMemory(String name) {
        this(getPid(name));
    }

    /**
     * Constructs a new AppMemory object.
     *
     * @param pid of the process
     */
    public AppMemory(int pid) {
        Pointer process = kernel32.OpenProcess(PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION, true, pid).getPointer();
        this.handle = new HANDLE(process);
    }

    /**
     * Gets the PID for a process.
     *
     * @param name the name of the process
     * @return the PID
     * @throws IllegalStateException when the process wasn't found
     */
    private static int getPid(String name) {
        try {
            CommandLine cmdLine = CommandLine.parse("tasklist /FI \"IMAGENAME eq " + name + "\" /FO CSV");
            DefaultExecutor executor = new DefaultExecutor.Builder<>().get();
            ExecuteWatchdog watchdog = new ExecuteWatchdog.Builder().setTimeout(Duration.ofMillis(ExecuteWatchdog.INFINITE_TIMEOUT)).get();
            executor.setWatchdog(watchdog);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            executor.setStreamHandler(new PumpStreamHandler(outputStream));

            int exitValue = executor.execute(cmdLine);
            if (exitValue == 0) {
                String output = outputStream.toString();
                String[] lines = output.split("\r\n|\r|\n");

                for (String line : lines) {
                    if (!line.contains(name)) {
                        continue;
                    }

                    outputStream.close();
                    String[] tokens = line.split(",");
                    String pid = tokens[1].replaceAll("\"", "");
                    System.out.println("Pid: " + pid);
                    return Integer.parseInt(pid);
                }
            }

            outputStream.close();
        } catch (IOException ignored) {
        }
        throw new RuntimeException(new IllegalStateException("Process " + name + " no found!"));
    }

    /**
     * Attempts to find a dynamic address with a static address and offsets.
     *
     * @param baseAddress the static address
     * @param offsets     offsets that get applied to the address
     * @return a Pointer pointing to the field
     */
    public Pointer findAddress(long baseAddress, int... offsets) { // TODO
        long address = 0L; //Pointer.nativeValue(handle.getPointer());
        Memory temp = new Memory(pointerSize);
        System.out.println(Long.toHexString(address));

        kernel32.ReadProcessMemory(handle, handle.getPointer().share(baseAddress), temp, pointerSize, null);
        if (pointerSize == 4) {
            address = temp.getInt(0);
        } else if (pointerSize == 8) {
            address = temp.getLong(0);
        }

        System.out.println(Long.toHexString(address));
        int start = offsets.length - 1;
        for (int i = start; i >= 0; i--) {
            int offset = offsets[i];
            System.out.print(Long.toHexString(address));
            kernel32.ReadProcessMemory(handle, new Pointer(address).share(offset), temp, pointerSize, null);
            if (pointerSize == 4) {
                address = temp.getInt(0);
            } else if (pointerSize == 8) {
                address = temp.getLong(0);
            }
            System.out.print(" + " + Integer.toHexString(offset) + " = " + Long.toHexString(address) + "\n");
        }
        temp.close();
        return new Pointer(address);
    }

    /**
     * Writes to a field.
     *
     * @param address     to the memory
     * @param bytesToRead number of bytes of space to allocate
     * @return the memory stored the address
     */
    public Memory readMemory(long address, int bytesToRead) {
        return readMemory(new Pointer(address), bytesToRead);
    }

    /**
     * Writes to a field.
     *
     * @param address     the pointer to the memory
     * @param bytesToRead number of bytes of space to allocate
     * @return the memory stored the address
     */
    public Memory readMemory(Pointer address, int bytesToRead) {
        IntByReference read = new IntByReference(0);
        Memory output = new Memory(bytesToRead);

        kernel32.ReadProcessMemory(handle, address, output, bytesToRead, read);
        return output;
    }

    /**
     * Writes to a field.
     *
     * @param address to the memory
     * @param data    the new data
     * @return true if successful
     */
    public boolean writeMemory(long address, byte[] data) {
        return writeMemory(new Pointer(address), data);
    }

    /**
     * Writes to a field.
     *
     * @param address the pointer to the memory
     * @param data    the new data
     * @return true if successful
     */
    public boolean writeMemory(Pointer address, byte[] data) {
        int size = data.length;
        Memory toWrite = new Memory(size);

        for (int i = 0; i < size; i++) {
            toWrite.setByte(i, data[i]);
        }

        boolean b = kernel32.WriteProcessMemory(handle, address, toWrite, size, null);
        toWrite.close();
        return b;
    }

    /**
     * Sets the pointer size. The default value is 8. <br>
     * Use 4 for 32-bit and 8 for 64-bit.
     *
     * @param size the new pointer size
     */
    public void setPointerSize(int size) {
        this.pointerSize = size;
    }

    /**
     * Calls {@link Kernel32#CloseHandle}
     */
    public void dispose() {
        kernel32.CloseHandle(handle);
    }
}
