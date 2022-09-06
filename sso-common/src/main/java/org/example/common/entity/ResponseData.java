package org.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseData<T> {
    private Status status;
    private String message;
    private T data;

    public static <T> ResponseData<T> success(T data) {
        return new ResponseData<>(Status.SUCCESS, "操作成功", data);
    }

    public static <T> ResponseData<T> fail(String message) {
        return new ResponseData<>(Status.FAIL, message, null);
    }

    public enum Status {
        SUCCESS(1), FAIL(0);
        private int code;

        private Status(int code) {
            this.code = code;
        }
    }
}
