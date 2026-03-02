package com.example.ototocarsrentingapp.model;

public class Result<T> {

    private final T data;
    private final Exception e;

    public Result(T data, Exception e) {
        this.data = data;
        this.e = e;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null);
    }
    public static <T> Result<T> failure(Exception e) {
        return new Result<>(null, e);
    }
    public static <T> Result<T> unset() {
        return new Result<>(null, null);
    }
    public T getData() {
        return data;
    }

    public Exception getError() {
        return e;
    }
    public boolean isUnset() {
        return data == null && e == null;
    }
    public boolean isSuccess() {
        return e == null && data != null;
    }
    public boolean isFailure() {
        return e != null;
    }
}
