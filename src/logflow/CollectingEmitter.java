package logflow;

import java.util.ArrayList;
import java.util.List;

/**
 * Test double: emitted records are collected in memory.
 * It does not touch the file system.
 */
public class CollectingEmitter<T> implements Emitter<T> {
    private final List<T> values = new ArrayList<>();

    @Override
    public void emit(T value) {
        values.add(value);
    }

    public List<T> values() {
        return List.copyOf(values);
    }
}
