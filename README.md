DrunkenArcher
=============

Minecraft like clone written in Java with LWJGL

Import the project into Eclipse to update the code.


----------
To run:
----------
 - Create a run configuration in Eclipse by right clicking the project and selecting "Run As -> Java Application"
 - The application will fail due to missing native libraries.
 - Edit the run configuration and add one of the following lines to the VM args section:
<pre>
    -Djava.library.path=lib/native/linux
    -Djava.library.path=lib/native/macosx
    -Djava.library.path=lib/native/solaris
    -Djava.library.path=lib/native/windows
</pre>


----------
Keys:
----------
 - A,W,S,D to move around
 - Arrow Keys or mouse to look around
 - Shift to run
 - Ctrl to crouch/fly down
 - Space to jump/fly up
 - f to toggle flying mode
 - P to toggle fullscreen
 - Esc to release mouse capture
 - Tab to toggle Wireframe/Polygon File modes


----------
TODO:
----------
1. Player should start above ground, preferably standing on it.
2. Introduce gravity and jumping/crouching.
3. Stop rendering faces that are not visible.
4. Fix the dark line along cube edges.
5. Add ability to place and destry blocks.
6. Add water (transparency, boyancy, fog, etc).
7. Allow infinite chunks by saving off chunks not in view and adding new ones.
