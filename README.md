# glade


This is sketch for minecraft-like game written on pure java and lwjgl and assimp libs.  

It is highly underdeveloped project and most of code is used to understand best approach.  
The main benefit of this project is to understand how to write 3d game with skeletal animation and easy-loading textures.

It uses raster rendering.  
It contains UI elements (so you can test press host -> connect, wait a bit) (Main keys - mouse, WASD, R, T, Z, SPACE, B, Q) (You can scroll up to first person) (You can put and destroy blocks).  
Multiplayer is present.  
Skeletal animation works perfectly.  
Smart texture loading.  
Easy-models with meshes.  
Mesh compressing block-chunks.  
Solved a lot of pitfalls.  
Camera collisions.  
Physical world collisions.  

It uses LWJGL and redistributable for most platforms, but not tested on IOS and android yet.  
I stopped the development due to lack of time in my life, but I am so keen of finishing it.  
I tried to use ray-tracing and realized that there is no enough support for that even using Vulkan (lack of dynamic animation).  
A lot of architectural things should be changed (eg multiplayer and data locality).  
I also developed cool algorithm for compressing minecraft worlds, but not implemented yet. (it can compress much better than standart minecraft segment-tree compressor) (It is based on hashing).  

![image](https://i.imgur.com/DmHU0Kd.png)
![image](https://i.imgur.com/u2WM07n.jpg)
