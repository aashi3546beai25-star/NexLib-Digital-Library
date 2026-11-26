package util;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic response container demonstrating Java Generics (Lectures 47-48).
 */
public class DataResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean success;
    private final String message;
    private final T data;

    public DataResult(boolean success, String message, T data) {
        this.success = success; this.message = message; this.data = data;
    }

    public static <T> DataResult<T> ok(String msg, T val) { return new DataResult<>(true, msg, val); }
    public static <T> DataResult<T> err(String msg) { return new DataResult<>(false, msg, null); }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    public static <E, R> List<R> transform(List<? extends E> list, Function<E, R> f) {
        return list.stream().map(f).collect(Collectors.toList());
    }
}
