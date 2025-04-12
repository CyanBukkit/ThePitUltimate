package net.mizukilab.pit.util;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;

public class RangedStreamLineList<T> extends ConcurrentLinkedDeque<T> {

    private final Predicate<T> predicate;
    private final int maxElement;

    public RangedStreamLineList(int maxElement, Predicate<T> t) {
        super();
        this.maxElement = maxElement;
        this.predicate = t;
    }

    public RangedStreamLineList(int maxElement, Predicate<T> t, Collection<T> t2) {
        super(t2);
        this.maxElement = maxElement;
        this.predicate = t;
    }

    @Override
    public boolean offerFirst(T t) {
        recycle();
        return super.offerFirst(t);
    }

    @Override
    public boolean offerLast(T t) {
        throw new UnsupportedOperationException("Only can offer at first");
    }

    @Override
    public void addFirst(T t) {
        this.offerFirst(t);
    }

    @Override
    public void addLast(T t) {
        this.offerLast(t);
    }

    @Override
    public boolean add(T t) {
        this.offerFirst(t);
        return true;
    }

    public void recycle() {
        //stage 1
        while (peekLast() != null && (size() > maxElement || predicate.test(peekLast()))) {
            pollLast();
        }
    }

}
