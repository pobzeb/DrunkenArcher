DrunkenArcher
=============

Minecraft like clone written in Java with LWJGL

Import the project into Eclipse to update the code.

To run:
 - Create a run configuration in Eclipse by right clicking the project and selecting "Run As -> Java Application"
 - The application will fail due to missing native libraries.
 - Edit the run configuration and add one of the following lines to the VM args section:
    -Djava.library.path=lib/native/linux
    -Djava.library.path=lib/native/macosx
    -Djava.library.path=lib/native/solaris
    -Djava.library.path=lib/native/windows
