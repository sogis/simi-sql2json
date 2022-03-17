package ch.so.agi.sql2json.generator;

@FunctionalInterface
public interface ExWrapper<T, E extends Exception> {
    void accept(T t) throws E;
}
