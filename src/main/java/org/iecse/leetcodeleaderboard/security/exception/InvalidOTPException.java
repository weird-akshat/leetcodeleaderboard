package org.iecse.leetcodeleaderboard.security.exception;

public class InvalidOTPException extends RuntimeException {
    public InvalidOTPException(String message) { super(message); }
}