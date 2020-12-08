package no.ntnu.tdt4501;

public class Settings {
    public volatile static int THREADS = 1;

    public static final int[] THREADS_TESTS = new int[]{1, 2, 4, 8};

    public static void setThreads(int threads){
        THREADS = threads;
    }

}
