package hx.ml.hmm;

/**
 * Created by Benchun on 2/18/17
 */
public class FloatMatrix {

    final float[][] elements;
    int rows, cols;

    public FloatMatrix(float[] e, int cols) {
        this.cols = cols;
        rows = e.length / cols + ( e.length % cols == 0 ? 0 : 1 );
        elements = new float[rows][cols];
        for (int i=0; i<rows; i++)
            for (int j=0; j<cols; j++)
                if (i*cols+j < e.length)
                    elements[i][j] = e[i*cols+j];
        if (e.length % cols != 0)
            for (int j=e.length % cols; j<cols; j++)
                elements[rows-1][j] = 0;
    }

    public FloatMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        elements = new float[rows][cols];
    }

    public float get(int i, int j) {
        return elements[i][j];
    }

    public void set(int i, int j, float element) {
        elements[i][j] = element;
    }

    public boolean multiplyMatch(FloatMatrix m) {
        return cols == m.rows;
    }

    public int multiplyOps(FloatMatrix m) {
        if (! multiplyMatch(m))
            throw new IllegalArgumentException();
        return rows * cols * m.cols;
    }

    public void dump() {
        for (float[] row : elements) {
            for (float element : row)
                System.out.print(element + "\t");
            System.out.println();
        }
    }
}
