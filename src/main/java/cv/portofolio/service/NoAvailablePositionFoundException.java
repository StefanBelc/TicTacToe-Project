package cv.portofolio.service;

public class NoAvailablePositionFoundException extends RuntimeException {

    public NoAvailablePositionFoundException(String message) {
        super(message);
    }
}
