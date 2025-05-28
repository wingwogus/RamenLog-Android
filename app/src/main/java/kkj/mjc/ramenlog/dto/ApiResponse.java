package kkj.mjc.ramenlog.dto;

public class ApiResponse<T>{
    private boolean success;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // 정적 메서드는 생략 가능 (Android에서 Retrofit 용도로만 쓸 경우)

}
