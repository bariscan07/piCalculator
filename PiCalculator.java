
import java.util.Scanner;

public class PiCalculator {

    //This is the class to implement the multithreading functionality.
    static class PiThread extends Thread {

        //Number of operations that a single thread should perform. Its value will be passed in the master node.
        private int numOfOps;
        //Point that a single thread should start to operate. Its value will be passed in the master node.
        private int startPoint;
        //Result of the run function which evaluates the number will be stored in this variable.
        private double result = 0.0;
        //Total execution time of a single thread.
        private long duration;

        //Constructor to initiate a interval or a range that a thread will perform the operations between.
        public PiThread(int startPoint, int numOfOps) {
            this.startPoint = startPoint;
            this.numOfOps = numOfOps;
        }

        /*This is the part where actual computation takes place. Each thread has its own values to do their parts correctly.
          You can consider it as the main() of the new thread */
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            //Let each thread deal with their interval by using the formula inside the for loop.
            for (int i = startPoint; i < startPoint + numOfOps; i++) {
                /*Taylor Series calculation. Math.pow() is used to alter between plus and minus signs, and the 2 * i + 1
                   part computes the denominators which are odd numbers. Result of each iteration adds up to get the
                   final result from a thread.*/
                //System.out.println("Operation: " + Math.pow(-1, i) + "/" +  (2 * i + 1) + " by " + this.getName());
                result += Math.pow(-1, i) / (2 * i + 1);
            }
            duration = System.currentTimeMillis() - start;
        }

        //Getter method for the result.
        public double getResult() {
            return result;
        }

        //Getter method for the duration of a single thread.
        public long getDuration() {
            return duration;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //Variable that will store our approximation.
        double piAppx = 0.0;
        //Scanner object to read inputs from the user.
        Scanner scanner = new Scanner(System.in);
        //Ask to user for number of threads that is needed.
        System.out.print("Please enter the number of threads: ");
        //Assign the number of threads to variable which is an integer.
        int numOfThreads = scanner.nextInt();
        //Ask to user for number of operations that is needed.
        System.out.print("Please enter the number of operations: ");
        //Assign the number operations to variable which is an integer.
        int numOfOps = scanner.nextInt();
        //Evaluate the number of operations per thread.
        int opsPerThread = numOfOps / numOfThreads;
        //Calculation will start from the first operation. Please notice that, operation indexes start from zero.
        int opFirst = 0;
        //If operations could not be divided equally between the threads, remainders will be handled by the main thread.
        int remainingOps = numOfOps % numOfThreads;
        if (remainingOps != 0) {
            //Compute the start point for the main thread. Finish point is the numOfOps itself already.
            int opFirstMain = opsPerThread * numOfThreads;
            System.out.println("Start point for the main thread is " + opFirstMain);
            //Let the main thread handle the remaining operations just like the PiThread class does.
            for (int i = opFirstMain; i < numOfOps; i++) {
                piAppx += Math.pow(-1, i) / (2 * i + 1);
                System.out.println("Main thread handled the " + i + "th operation.");
            }
        }

        //Initialize the thread list which will store each node.
        PiThread[] threads = new PiThread[numOfThreads];

        //Start each of them by using the PiThread class.
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new PiThread(opFirst, opsPerThread);
            //Start the thread.
            threads[i].start();
            //Compute the next thread's start point by proceeding operation per thread times ahead.
            opFirst += opsPerThread;
        }

        //Join all the threads to wait them to die.
        for (PiThread thread : threads) {
            thread.join();
        }

        //Collect the results from each thread and sum them up to get PI/4 which is the left-hand side of the formula.
        for (PiThread thread : threads) {
            piAppx += thread.getResult();
        }

        long duration = 0;
        //Get the average duration.
        for (PiThread thread : threads) {
            duration += thread.getDuration();
        }

        long aveDuration = duration / numOfThreads;


        System.out.println("Approximation of PI is: " + piAppx * 4.0);
        System.out.println("Actual PI number is: " + Math.PI);
        double error = Math.PI - piAppx * 4.0;
        System.out.println("Approximation error is: " + error);

        System.out.println("Execution time is: " + aveDuration + " ms.");

    }
}