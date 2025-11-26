package cv.portofolio.service;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException (String message) {
        super(message);
    }
}
