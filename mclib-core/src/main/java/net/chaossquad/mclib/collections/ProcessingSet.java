package net.chaossquad.mclib.collections;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A set that processes items that are added to it.<br/>
 * Useful for copying items for adding them to the set.
 * @param <T> value type
 */
public class ProcessingSet<T> implements Set<T> {
    @NotNull private final Set<T> delegate;
    @NotNull private final Processor<T> processor;

    /**
     * Creates a processing set based on an underlying delegate set and a processor which processes the added items.
     * @param delegate delegate set
     * @param processor processor which processes the added items
     */
    public ProcessingSet(@NotNull Set<T> delegate, @NotNull Processor<T> processor) {
        this.delegate = delegate;
        this.processor = processor;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public @NotNull <T1> T1 @NotNull [] toArray(@NotNull T1 @NotNull [] a) {
        return this.delegate.toArray(a);
    }

    @Override
    public boolean add(T t) {
        T processed = this.processor.process(t);
        return this.delegate.add(processed);
    }

    @Override
    public boolean remove(Object o) {
        return this.delegate.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        boolean modified = false;
        for (T t : c) {
            T processed = this.processor.process(t);
            if (this.delegate.add(processed)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.delegate.retainAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.delegate.removeAll(c);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    // ----- INTERNALS -----

    /**
     * Returns the delegate set.
     * @return delegate
     */
    public final @NotNull Set<T> getDelegate() {
        return this.delegate;
    }

    /**
     * Returns the processor.
     * @return processor
     */
    public final @NotNull Processor<T> getProcessor() {
        return this.processor;
    }

    // ----- PROCESSOR -----

    /**
     * Processes a specific value.
     * @param <T> value type
     */
    public interface Processor<T> {

        /**
         * Processes a specific value.
         * @param t value
         * @return processed value
         */
        T process(T t);

    }

}
