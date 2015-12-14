import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by ashagal on 12/1/15.
 */
public class baum_velch {
    int state_count;
    int observations_count;
    double[] Pi;
    double[][] T;
    double[][] O;
    int t;
    int[] seq;

    void print(PrintWriter out) {
        out.println("Transitions\n");
        for (int i = 0; i < T.length; i++) {
            for (int j = 0; j < T[i].length; j++) {
                out.print(T[i][j] + " ");
            }
            out.println();
        }

        out.println("\n\nObservations\n");
        for (int i = 0; i < O.length; i++) {
            for (int j = 0; j < O[i].length; j++) {
                out.print(O[i][j] + " ");
            }
            out.println();
        }

        out.close();
    }

    void init() {
        state_count = 2;
        Random r = new Random();
        T = Utils.random(state_count);
        O = Utils.random(state_count, 6);
        observations_count = 6;
        t = 10;
        seq = new int[t];
        for (int i = 0; i < t; ++i) {
            seq[i] = Math.abs(r.nextInt()) % observations_count;
        }
        Pi = Utils.random_list(state_count);
    }

    baum_velch() {
        init();
    }

    baum_velch(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File(filename));
        state_count = in.nextInt();
        observations_count = in.nextInt();
        T = new double[state_count][];
        for (int i = 0; i < state_count; ++i) {
            T[i] = new double[state_count];
        }

        for (int i = 0; i < state_count; ++i) {
            for (int j = 0; j < state_count; j++) {
                T[i][j] = in.nextDouble();
            }
        }

        O = new double[state_count][];
        for (int i = 0; i < state_count; i++) {
            O[i] = new double[observations_count];
        }

        for (int i = 0; i < state_count; i++) {
            for (int j = 0; j < observations_count; j++) {
                O[i][j] = in.nextDouble();
            }
        }

        Pi = new double[state_count];
        for (int i = 0; i < state_count; i++) {
            Pi[i] = in.nextDouble();
        }

        t = in.nextInt();
        seq = new int[t];
        for (int i = 0; i < t; i++) {
            seq[i] = in.nextInt();
        }
    }

    void run() {
        alpha();
    }

    double[][] alpha;
    void alpha() {
        alpha = new double[state_count][];
        for (int i = 0; i < state_count; ++i) {
            alpha[i] = new double[t];
        }

        for (int i = 0; i < state_count; ++i) {
            alpha[i][0] = Pi[i] * O[i][seq[0]];
        }

        for (int i = 1; i < t; ++i) {
            for (int j = 0; j < state_count; ++j) {
                for (int k = 0; k < state_count; k++) {
                    alpha[j][i] += O[j][seq[i]] * alpha[k][i - 1] * T[k][j];
                }
            }
        }
    }

    double[][] beta;
    void beta() {
        beta = new double[state_count][];
        for (int i = 0; i < state_count; ++i) {
            beta[i] = new double[t];
        }

        for (int i = 0; i < state_count; ++i) {
            beta[i][t - 1] = 1.0;
        }

        for (int i = t - 2; i >= 0; --i) {
            for (int j = 0; j < state_count; j++) {
                for (int k = 0; k < state_count; k++) {
                    beta[j][i] += beta[k][i + 1] * T[j][k] * O[k][seq[i + 1]];
                }
            }
        }
    }

    double[][] gamma;
    void gamma() {
        gamma = new double[state_count][];
        for (int i = 0; i < state_count; i++) {
            gamma[i] = new double[t];
        }

        for (int i = 0; i < t; i++) {
            double res = 0.0;
            for (int j = 0; j < state_count; j++) {
                res += alpha[j][i] * beta[j][i];
            }

            for (int j = 0; j < state_count; j++) {
                gamma[j][i] = alpha[j][i] * beta[j][i] / res;
            }
        }
    }

    double[][][] ksi;
    void ksi() {
        ksi = new double[state_count][][];
        for (int i = 0; i < state_count; i++) {
            ksi[i] = new double[state_count][];
        }
        for (int i = 0; i < state_count; i++) {
            for (int j = 0; j < state_count; j++) {
                ksi[i][j] = new double[t];
            }
        }

        for (int i = 0; i < t - 1; i++) {
            double res = 0;
            for (int j = 0; j < state_count; j++) {
                res += alpha[j][i] * beta[j][i];
            }

            for (int j = 0; j < state_count; j++) {
                for (int k = 0; k < state_count; k++) {
                    ksi[j][k][i] = alpha[j][i] * T[j][k] * beta[k][i + 1] * O[k][seq[i + 1]] / res;
                }
            }
        }
    }

    void Pi() {
        for (int i = 0; i < state_count; i++) {
            Pi[i] = gamma[i][0];
        }
    }

    void T() {
        for (int i = 0; i < state_count; i++) {
            double res = 0;
            for (int j = 0; j < t; j++) {
                res += gamma[i][j];
            }

            for (int j = 0; j < state_count; j++) {
                double num = 0;
                for (int k = 0; k < t; k++) {
                    num += ksi[i][j][k];
                }

                T[i][j] = num / res;
            }
        }
    }

    void O() {
        for (int i = 0; i < state_count; i++) {
            double res = 0;
            for (int j = 0; j < t; j++) {
                res += gamma[i][j];
            }

            for (int j = 0; j < observations_count; j++) {
                double num = 0.0;
                for (int k = 0; k < t; k++) {
                    if (seq[k] == j) {
                        num += gamma[i][k];
                    }
                }
                O[i][j] = num/res;
            }
        }
    }


    void baum_welch() {
        while (true) {
            alpha();
            beta();
            gamma();
            ksi();
            double[] PiOld = Pi.clone();
            Pi();
            double[][] TOld = T.clone();
            T();
            double[][] OOld = O.clone();
            O();

            if (Utils.maxdiff(TOld, T) < 0.0001 && Utils.maxdiff(OOld, O) < 0.00001 && Utils.maxdiff(PiOld, Pi) < 0.0001) {
                return;
            }
        }
    }

    double [][] V;
    int [][] path;
    int [] X;
    void viterbi() {
        X = new int[t];
        V = new double[t][];
        for (int i = 0; i < t; i++) {
            V[i] = new double[state_count];
        }
        path = new int[t][];
        for (int i = 0; i < t; i++) {
            path[i] = new int[state_count];
        }

        for (int i = 0; i < state_count; i++) {
            V[0][i] = Pi[i] * O[i][seq[0]];
            path[0][i] = i;
        }

        for (int i = 1; i < t; i++) {
            for (int j = 0; j < state_count; j++) {
                double max1 = Double.MIN_VALUE;
                int max_k = -1;
                for (int k = 0; k < state_count; k++) {
                    if (max1 <= V[i - 1][k] * T[k][j] * O[j][seq[i]]) {
                        max_k = k;
                        max1 = V[i - 1][k] * T[k][j] * O[j][seq[i]];
                    }
                }
                V[i][j] = max1;
                path[i][j] = max_k;
            }
        }

        Utils.print(V);

        double max = Double.MIN_VALUE;
        for (int i = 0; i < state_count; i++) {
            if (V[t - 1][i] > max) {
                X[t - 1] = i;
                max = V[t - 1][i];
            }
        }

        for (int i = t - 1; i >=1 ; i--) {
            X[i - 1] = path[i][X[i]];
        }

        System.out.println("X ");
        for (int i = 0; i < X.length; i++) {
            System.out.print(X[i] + " ");
            System.out.println();
        }
    }
}
