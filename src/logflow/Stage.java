package logflow;

public interface Stage<I, O> {
    void setNext(Emitter<O> next);
    void accept(I input);
}
