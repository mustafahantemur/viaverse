package app.viaverse.shared.kernel.pagination;

public record PageCursor(String value) {
    public PageCursor {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("cursor value must not be blank");
        }
    }
}

