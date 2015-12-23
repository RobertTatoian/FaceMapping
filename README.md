# FaceMapping

Final project for CSC 406 by Robert and Warren.

This project has the following features:
+ It has animation
+ Collision detection using separating axis theorem
+ Relative and absolute bounding boxes are computed
+ The classes are organized into a hierarchy of simple and complex 3d graphic objects
+ Opencv is used for face detection
+ Image processing is used to blend multiple detected faces together
+ 3D camera controls

# Usage
Run `main.java` to start the program.

+ Press the "`" key to enter "debug mode" and see the webcam input.
+ Press the "w" key to move the camera forward.
+ Press the "s" key to move the camera backward.
+ Press the "a" key to move the camera left.
+ Press the "d" key to move the camera right.
+ Use the mouse to rotate the camera (like in a FPS). You may need to move the a
  camera around if you aren't facing in the right direction.

The program uses the front and side of your face to build a texture. However,
to improve results, only certain detected faces are used in the texture. Yet
there may be some weird results it detects a face where there isn't one since
the project uses default Haar cascades to detect faces and eyes. For
best results don't look up or down, make sure that your face is evenly and
clearly lit, and that the background is simple. The program will only update the
texture with your face if:

+ It detects the front of your face and two eyes of similar size
+ It detects the side of your face and one eye
+ It doesn't detect more than one overlapping faces.
