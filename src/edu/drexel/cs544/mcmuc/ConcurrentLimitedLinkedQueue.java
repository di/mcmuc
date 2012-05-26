package edu.drexel.cs544.mcmuc;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ConcurrentLinkedQueue is thread-safe queue that orders elements FIFO. New elements are
 * inserted at the tail of the queue, and the queue retrieval operations obtain elements 
 * at the head of the queue. A ConcurrentLinkedQueue allows mulitple threads to share access
 * to a the collection.
 * 
 * ConcurrentLimitedLinkedQueue extends a CLQ to have a maximum number of elements
 */
@SuppressWarnings("serial")
public class ConcurrentLimitedLinkedQueue<E> extends ConcurrentLinkedQueue<E> {

    // The limit size of the queue
    private int limit;

    /**
     * Creates a CLQ whose size cannot exceed limit - any additions will remove the 
     * element at the head of the queue and add the element to the tail.
     * @param limit int maximum size
     */
    public ConcurrentLimitedLinkedQueue(int limit) {
        this.limit = limit;
    }

    /**
     * if the size of the CLQ has reached limit, the addition of 'o' will remove the 
     * element at the head of the queue and add 'o' to the tail. If 'o' was added, add
     * returns true, and false otherwise.
     */
    @Override
    public boolean add(E o) {
        // Remove if the queue size is above the limit
        while (size() > limit) {
            super.remove();
        }
        // Add the object to be added
        if (!super.contains(o)) {
            return super.add(o);
        } else {
            return false;
        }
    }
}
