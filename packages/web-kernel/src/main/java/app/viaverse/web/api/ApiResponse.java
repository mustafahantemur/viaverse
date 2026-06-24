package app.viaverse.web.api;

/**
 * Generic API response envelope shared by backend REST endpoints.
 *
 * <p>Successful responses set {@code success=true}, populate {@code data}, and leave
 * {@code error} null. Error responses invert that contract.
 */
public record ApiResponse<T>(boolean success, T data, ErrorBody error) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorBody(code, message));
    }

    public record ErrorBody(String code, String message) {}
}
