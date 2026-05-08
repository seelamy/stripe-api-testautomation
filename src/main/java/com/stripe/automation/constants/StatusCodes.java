package com.stripe.automation.constants;

public final class StatusCodes {
    private StatusCodes(){}

    // Success
    public static final int OK = 200;

    // Client Errors
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int REQUEST_FAILED = 402;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int RATE_LIMIT = 429;

    // Server Errors
    public static final int SERVER_ERROR = 500;
    public static final int SERVICE_UNAVAILABLE = 503;
}
