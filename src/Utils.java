import java.util.Random;

/**
 * Created by ashagal on 12/8/15.
 */
public class Utils {
    public static void print(double[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static double[][] random(int n) {
        double [][] a = new double[n][];
        for (int i = 0; i < n; i++) {
            a[i] = random_list(n);
        }
        return a;
    }

    public static double[][] random(int n, int m) {
        double[][] a = new double[n][];
        for (int i = 0; i < n; ++i) {
            a[i] = random_list(m);
        }
        return a;
    }

    public static double[] random_list(int n) {
        Random r = new Random();
        double[] d = new double[n];
        double s = 0.0;
        for (int i = 0; i < n - 1; i++) {
            d[i] = (1 - s) * r.nextDouble();
            s += d[i];
        }

        d[n - 1] = 1 - s;
        return d;
    }

    public static double maxdiff(double[] a, double[] b) {
        assert (a.length == b.length);
        double max = Double.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            max = Math.max(max, Math.abs(a[i] - b[i]));
        }
        return max;
    }

    public static double maxdiff(double[][] a, double[][] b) {
        assert (a.length == b.length);
        double d = Double.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            d = Math.max(d, maxdiff(a[i], b[i]));
        }
        return d;
    }
}
