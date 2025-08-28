package com.loopers.infrastructure.pg;

import lombok.ToString;

@ToString
public class PgApiResponse<T> {
    private final Metadata meta;
    private final T data;

    public PgApiResponse(Metadata meta, T data) {
        this.meta = meta;
        this.data = data;
    }

    public static PgApiResponse<Object> success() {
        return new PgApiResponse<>(Metadata.success(), null);
    }

    public static <T> PgApiResponse<T> success(T data) {
        return new PgApiResponse<>(Metadata.success(), data);
    }

    public static PgApiResponse<Object> fail(String errorCode, String errorMessage) {
        return new PgApiResponse<>(Metadata.fail(errorCode, errorMessage), null);
    }

    public Metadata getMeta() {
        return meta;
    }

    public T getData() {
        return data;
    }

    public static class Metadata {
        private final Result result;
        private final String errorCode;
        private final String message;

        public Metadata(Result result, String errorCode, String message) {
            this.result = result;
            this.errorCode = errorCode;
            this.message = message;
        }

        public static Metadata success() {
            return new Metadata(Result.SUCCESS, null, null);
        }

        public static Metadata fail(String errorCode, String errorMessage) {
            return new Metadata(Result.FAIL, errorCode, errorMessage);
        }

        public Result getResult() {
            return result;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getMessage() {
            return message;
        }

        public enum Result {SUCCESS, FAIL}
    }
}
