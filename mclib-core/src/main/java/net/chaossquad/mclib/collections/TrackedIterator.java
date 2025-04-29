package net.chaossquad.mclib.collections;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * An iterator that tracks changes.
 * @param <T> type
 */
public class TrackedIterator<T> implements Iterator<T> {
    @NotNull private final Iterator<T> delegate;
    @NotNull private final Callback<T> callback;
    private T next;

    /**
     * Creates a TrackedIterator with an underlying delegate iterator and a tracker callback.
     * @param delegate delegate iterator
     * @param callback callback which will be called on changes
     */
    public TrackedIterator(@NotNull Iterator<T> delegate, @NotNull Callback<T> callback) {
        this.delegate = delegate;
        this.callback = callback;
        this.next = null;
    }

    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    @Override
    public T next() {
        T next = this.delegate.next();
        this.next = next;
        this.callback.onUpdate(this, Action.NEXT, next);
        return next;
    }

    @Override
    public void remove() {
        this.delegate.remove();
        this.callback.onUpdate(this, Action.REMOVE, this.next);
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        this.delegate.forEachRemaining(action);
    }

    /**
     * Returns the cached next value of the iterator.
     * @return next
     */
    public T getNext() {
        return this.next;
    }

    /**
     * Update callback
     * @param <T> type
     */
    public interface Callback<T> {

        /**
         * This method is called by the iterator when it has been updated.
         * @param trackedIterator the iterator object calling this method
         * @param action the action that has been done to the iterator
         * @param element the element that is affected
         */
        void onUpdate(@NotNull TrackedIterator<T> trackedIterator, @NotNull Action action, T element);

    }

    /**
     * Update action.
     */
    public enum Action {

        /**
         * Iterator proceeded to the next element.
         */
        NEXT,

        /**
         * Element has been removed by the iterator.
         */
        REMOVE;

    }

}
