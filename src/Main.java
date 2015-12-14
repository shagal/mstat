import java.io.PrintWriter;

/**
 * Created by ashagal on 12/1/15.
 */
public class Main {
    public static void run(String[] args) {
        baum_velch b = new baum_velch();
        b.baum_welch();
        b.print(new PrintWriter(System.out));
        b.viterbi();
    }

    public static void test(String[] args) throws Exception{
        baum_velch b = new baum_velch("in.txt");
        b.baum_welch();
    }

    public static void main(String[] args) throws Exception {
        test(args);
    }
}
