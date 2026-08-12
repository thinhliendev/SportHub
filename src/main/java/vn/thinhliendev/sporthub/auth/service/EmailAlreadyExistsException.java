package vn.thinhliendev.sporthub.auth.service;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Email này đã được sử dụng");
    }
}
