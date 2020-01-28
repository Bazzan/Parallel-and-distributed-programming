import java.awt.EventQueue;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Factorize implements Runnable {

    private final static long MIN = 2;
    BigInteger min;
    BigInteger max;
    BigInteger step;
    public boolean running;
    BigInteger number;

    static BigInteger product;

    static BigInteger factor1;
    static BigInteger factor2;

    static int threads;
    private Object lock = new Object();
    // private Lock lock = new ReentrantLock();
    Thread[] threadArray;

    Factorize(BigInteger min, BigInteger max, BigInteger step) {

        this.min = min;
        this.max = max;
        this.step = step;

    }       

    private synchronized BigInteger getNumber(){
        return number;
    }

    private synchronized BigInteger setNumber(BigInteger step){
        return number = number.add(step);
    }

    public void run() {

        number = min;

        while (number.compareTo(max.sqrt()) <= 0) {
            if (product.remainder(getNumber()).compareTo(BigInteger.ZERO) == 0) {
                factor1 = number;
                factor2 = product.divide(factor1);


    
                return;

            }
        
            setNumber(step);

        
        }

    }

    public static void main(String[] args) {
        try {

            GetInputs();

            Thread[] threadArray = new Thread[threads];
            Factorize[] factorizer = new Factorize[threads];

            for (int i = 0; i < threads; i++) {
                factorizer[i] = new Factorize(BigInteger.valueOf(i + MIN), product, BigInteger.valueOf(threads));

                System.out.println(factorizer[i].min.longValue() + " " + factorizer[i].max.longValue() + " "
                        + factorizer[i].step.longValue());

                threadArray[i] = new Thread(factorizer[i]);

            }

            long start = System.nanoTime();

            for (int i = 0; i < threads; i++) {
                threadArray[i].start();

            }

            for (int i = 0; i < threads; i++) {
                threadArray[i].join();
            }


            long stop = System.nanoTime();
            System.out.println("true" + " " + factor1 + " " + factor2 + " Time: " + (stop - start) / 1.0E9);

        } catch (Exception e) {
            System.out.println(e);
        }

    }



    private static void GetInputs() {

        Scanner scan = new Scanner(System.in);

        System.out.println("First prime");
        BigInteger firstPrime = new BigInteger(scan.nextLine());

        System.out.println("Second prime");
        BigInteger secondPrime = new BigInteger(scan.nextLine());

        product = firstPrime.multiply(secondPrime);

        System.out.println(product);
        
        System.out.println("number of corse available: " + Runtime.getRuntime().availableProcessors());
        System.out.println("How meny threads?");
        threads = scan.nextInt();
        scan.close();
    }
}