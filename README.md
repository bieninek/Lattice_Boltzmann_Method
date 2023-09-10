# Description of the model and implementation
The program consists of two classes: Cell.java and Main.java. The Main.java class contains the implementation of the following instructions included in the task instructions:
```java
// ======= directions ==========
    // 1 up, 2 right, 3 down, 4 left
    public float[] fIn;
    public float[] fOut;
    public float[] fEq;
    public float c;
    private final float weight; // used for counting f_eq
    private final float tau; // used for counting f_out
```
Each cell contains three tables: they store four values, i.e. the values of streams in each direction (we consider four directions). The name of each array suggests the values for which stream they are stored.
Outside these tables there is a variable responsible for the concentration of gas (fluid) in the cell.
There are also values of weights for directions (0.25 for each, then it is initialized) and tau set at the input (unlike the other class fields, they do not change while the program is running). For safety, I added the final keyword.
```java
    public Cell() {
        fIn = new float[4];
        fEq = new float[4];
        fOut = new float[4];
        for (int i = 0; i < 4; i++) {
            fIn[i] = 0.0f;
            fEq[i] = 0.0f;
            fOut[i] = 0.0f;
        }

        weight = 0.25f;
        tau = 1.0f;
    }
```
When creating each cell, constant values (weights and tau) are assigned and the tables storing the stream values are reset to zero.  
The first public method calculates the equilibrium distribution according to the formula:  
$`f_i^{eq} = w_iC`$  
The implementation is as follows. It is simply a loop iterating in all directions and multiplying the concentration value by the weight (always 0.25).
```java
    public void countFEq() {
        for (int i = 0; i < 4; i++) {
                fEq[i] = weight * c;
        }
    }
```
The second function is used to calculate the output stream. I used the formula:  
$`f_i^{out} = f_i^{in} + {\Delta \over \tau} [f_i^{eq} - f_i^{in}]`$  
In the program, I assumed tau equal to 1. Therefore, the formula simplifies. The output stream is actually the equilibrium stream.
The code of the function is almost the same as the one above, only the formula differs for each iteration.
```java
    public void countFOut() {
        for (int i = 0; i < 4; i++) {
                fOut[i] = fIn[i] + 1.0f / tau * (fEq[i] - fIn[i]);
        }
    }
```
The last one calculates the input stream. As an argument, it receives an array of output stream values from neighbors:  
$`f_i^{in}(r,t + \Delta t) = f_i^{out} (r - c_i, t)`$   
Note: when the neighbor is a wall, this value is -1.0. In such a situation, I assign the value of the output stream from this cell to the input stream (when the wall is on the right, the cell will receive from the right side as much as it would like to send to the right) according to the formula:  
$`f_i^{in}(r,t + \Delta t) = f_i^{out} (r, t)`$   
The implementation additionally takes into account the calculation of a new concentration, i.e. the sum of the input stream values for each of the four directions. In particular, when a cell has a wall as its neighbor, an appropriate output stream is added.
```java
    public void countFIn(float[] neighbourFOut) {
        float sum = 0.0f;

        for (int i = 0; i < 4; i++) {
            if (neighbourFOut[i] == -1.0f) {
                fIn[i] = this.fOut[i];
            } else {
                fIn[i] = neighbourFOut[i];
            }
            sum += fIn[i];
        }
        this.c = sum;
    }
```
In addition to these methods, there are getters and setters in the class.
The simulation is performed by the Main class. The beginning involves assigning initial conditions and creating the necessary objects.
First I do __streaming__. For this purpose, I call two methods of the Cell class: equilibrium and output distribution. Concentrations are calculated by the method called later, for the first condition the concentration is taken from the initial conditions.
```java
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    space[j][i].countFEq();
                    space[j][i].countFOut();
                }
            }
```
This is followed by a __collision__. I look at the cell's neighbors and get the appropriate output streams from them. I also pay attention to whether the cell has a wall next to it. If so, it is reflected - I assign a value of -1.0, which is then interpreted appropriately. The extended condition in a conditional statement is caused by a wall.
```java
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    float[] neighbours = new float[4];
                    if (i == 0 || (i == 34 && (j < 50 || j > 60))) { // left
                        neighbours[3] = -1.0f;
                    } else {
                        neighbours[3] = space[i-1][j].getfOut()[1];
                    }
                    if (i == height - 1 || (i == 33 && (j < 50 || j > 60))) { // right
                        neighbours[1] = -1.0f;
                    } else {
                        neighbours[1] = space[i+1][j].getfOut()[3];
                    }
                    if (j == 0) { // top
                        neighbours[0] = -1.0f;
                    } else {
                        neighbours[0] = space[i][j-1].getfOut()[2];
                    }
                    if (j == width - 1) { // bottom
                        neighbours[2] = -1.0f;
                    } else {
                        neighbours[2] = space[i][j+1].getfOut()[0];
                    }
                    space[i][j].countFIn(neighbours);
                }
            }
```
Finally, I print the image and save it to a file.
```java
            // print
            if (it % 1000 == 0) {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        img.setRGB(y, x, (new Color(
                                1.0f - space[y][x].getC()/3,
                                1.0f - space[y][x].getC()/2,
                                1.0f - space[y][x].getC()))
                                .getRGB());
                    }
                }
                File outputfile = new File(it + "_image.jpg");
                ImageIO.write(img, "jpg", outputfile);
            }
```

# Modeling results
Initially, I ran the simulations for an area without a wall.  
![](1.png)  
You can see the difference interestingly in the multi-photo screenshot (the number indicates the iteration number).  
![](2.png)  
The simulation was run approximately until equilibrium was reached.
The second simulation was in the area with a wall.  
![](3.png)  
Screenshot:  
![](4.png)  
The simulation did not achieve equilibrium in the same number of iterations, it can be seen that the wall makes mixing difficult. Therefore, I repeated it all until I achieved balance. Prints made less frequently.  
![](5.png)  
I created two videos showing diffusion based on photo frames:
- Area without wall: https://photos.app.goo.gl/yEnRLDcmYQAUeF8u7
- Area with a wall: https://photos.app.goo.gl/w6vKTiXu4MzWB16k8
