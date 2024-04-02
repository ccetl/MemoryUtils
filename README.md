<h1>MemoryUtils</h1>
A java library to modify the ram. Requires at least Java 8 and windows.

<h2>Important Note</h2>
The <code>AppMemory#findAddress</code> method is currently broken, so you can only initialize  
values with pointers right now.

<h2>Basic Usage</h2>
1. Initialize the MemoryEditor which is required by the AppMemory.
    <pre>
    MemoryEditor.init();
    </pre>
2. Create a new AppMemory for your process. For example:
   <pre>
   AppMemory appMemory = new AppMemory("Minecraft.Windows.exe");
   </pre>
3. Use the AppMemory to modify values. The following example will modify the FOV in Minecraft Bedrock.
   <pre>
   MemoryFloatValue fov = new MemoryFloatValue(Managers.PROCESS.getProcess(), "0585D498", "18", "1A0", "1A8", "10", "8", "968", "18", "28");
   System.out.println(fov.readValue());
   fov.writeValue(110f);
   </pre>

<h2>Used libraries</h2>
- JNA <br>
- commons-exec
