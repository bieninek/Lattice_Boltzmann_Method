import java.util.ArrayList;
import java.util.Arrays;

public class Cell {

    // ======= directions ==========
    // 1 up, 2 right, 3 down, 4 left
    public float[] fIn;
    public float[] fOut;
    public float[] fEq;
    public float c;
    private final float weight; // used for counting f_eq
    private final float tau; // used for counting f_out

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

    public void countFEq() {
        for (int i = 0; i < 4; i++) {
                fEq[i] = weight * c;
        }
    }

    public void countFOut() {
        for (int i = 0; i < 4; i++) {
                fOut[i] = fIn[i] + 1.0f / tau * (fEq[i] - fIn[i]);
        }
    }

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



    public void setC(float c) {
        this.c = c;
    }

    public float getC() {
        return c;
    }



    public float getWeight() {
        return weight;
    }

    public float getTau() {
        return tau;
    }

    public float[] getfIn() {
        return fIn;
    }

    public void setfIn(float[] fIn) {
        this.fIn = fIn;
    }

    public float[] getfOut() {
        return fOut;
    }

    public void setfOut(float[] fOut) {
        this.fOut = fOut;
    }

    public float[] getfEq() {
        return fEq;
    }

    public void setfEq(float[] fEq) {
        this.fEq = fEq;
    }
}
