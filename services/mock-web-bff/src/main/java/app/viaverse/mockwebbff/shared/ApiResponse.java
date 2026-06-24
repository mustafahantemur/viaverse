package app.viaverse.mockwebbff.shared;

public record ApiResponse<T>(
    boolean success,
    T data,
    String code,
    String detail
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }
}
