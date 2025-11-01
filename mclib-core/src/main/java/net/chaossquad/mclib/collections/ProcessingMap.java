package net.chaossquad.mclib.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A map that processes items that are added to it.<br/>
 * Useful for copying items for adding them to the map.
 * @param <K> key type
 * @param <V> value type
 */
public class ProcessingMap<K, V> implements Map<K, V> {
    @NotNull private final Map<K, V> delegate;
    @NotNull private final Processor<K, V> processor;

    /**
     * Creates a processing map based on an underlying delegate map and a processor which processes the added items.
     * @param delegate delegate map
     * @param processor processor which processes the added items
     */
    public ProcessingMap(@NotNull Map<K, V> delegate, @NotNull Processor<K, V> processor) {
        this.delegate = delegate;
        this.processor = processor;
    }

    // ----- MAP -----

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public @Nullable V put(K key, V value) {
        V processed = this.processor.process(key, value);
        this.delegate.put(key, processed);
        return processed;
    }

    @Override
    public V remove(Object key) {
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            V processed = this.processor.process(entry.getKey(), entry.getValue());
            this.delegate.put(entry.getKey(), processed);
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return Set.of();
    }

    @Override
    public @NotNull Collection<V> values() {
        return List.of();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return Set.of();
    }

    // ----- DELEGATE -----

    /**
     * Returns the delegate map.
     * @return delegate
     */
    public final @NotNull Map<K, V> getDelegate() {
        return this.delegate;
    }

    /**
     * Returns the processor.
     * @return processor
     */
    public final @NotNull Processor<K, V> getProcessor() {
        return this.processor;
    }

    // ----- OTHER -----

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    // ----- INTERFACE -----

    /**
     * Processes a specific value.
     * @param <K> key type
     * @param <V> value type
     */
    public interface Processor<K, V> {

        /**
         * Processes a specific value.
         * @param key key
         * @param value value
         * @return processed value
         */
        V process(K key, V value);

    }

}
