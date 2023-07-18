import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final int width = 100;
        final int height = 100;
        Cell[][] space = new Cell[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                space[i][j] = new Cell();
            }
        }


        // set initial c values
        for (int i = 0; i < width / 2; i++) {
            for (int j = 0; j < height; j++) {
                space[i][j].setC(1.0f);
            }
        }
        for (int i = width / 2; i < width; i++) {
            for (int j = 0; j < height; j++) {
                space[i][j].setC(0.0f);
            }
        }

        // image init
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // simulation/
        for (int it = 0; it < 4000; it++) {
            // collision
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    space[j][i].countFEq();
                    space[j][i].countFOut();
                }
            }

            // streaming
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    float[] neighbours = new float[4];
                    if (i == 0) { // top
                        neighbours[0] = -1.0f;
                    } else {
                        neighbours[0] = space[i-1][j].getfOut()[2];
                    }
                    if (i == height - 1) { // down
                        neighbours[2] = -1.0f;
                    } else {
                        neighbours[2] = space[i+1][j].getfOut()[0];
                    }
                    if (j == 0) { // left
                        neighbours[3] = -1.0f;
                    } else {
                        neighbours[3] = space[i][j-1].getfOut()[1];
                    }
                    if (j == width - 1 || i > 60) { // right
                        neighbours[1] = -1.0f;
                    } else {
                        neighbours[1] = space[i][j+1].getfOut()[3];
                    }
                    space[i][j].countFIn(neighbours);
                }
            }

            // print
            if (it % 40 == 0) {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        img.setRGB(y, x, (new Color(
                                1.0f,
                                1.0f,
                                1.0f - space[y][x].getC()))
                                .getRGB());
                    }
                }
                File outputfile = new File(it + "_image.jpg");
                ImageIO.write(img, "jpg", outputfile);
            }
//
//            // print
//            for (int i = 0; i < width; i++) {
//                //for (int j = 0; j < height; j++) {
//                    System.out.print(space[0][i].getC() + " ");
//                //}
//                System.out.println();
//            }
//            Scanner myObj = new Scanner(System.in);
//            String name = myObj.nextLine();
        }
    }
}