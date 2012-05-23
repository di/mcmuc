package edu.drexel.cs544.mcmuc;

public class DuplicateDetector {
    private static final DuplicateDetector instance = new DuplicateDetector();

    private ConcurrentLimitedLinkedQueue<String> q;
    private int size = 200;

    private DuplicateDetector() {
        q = new ConcurrentLimitedLinkedQueue<String>(size);
    }

    public boolean isDuplicate(String uid) {
        if (q.contains(uid)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean add(String uid) {
        return q.add(uid);
    }

    public static DuplicateDetector getInstance() {
        return instance;
    }
}
