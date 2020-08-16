package vstocks.model.rest;

public class UsernameExistsResponse {
    private String username;
    private boolean exists;

    public UsernameExistsResponse() {
    }

    public String getUsername() {
        return username;
    }

    public UsernameExistsResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean isExists() {
        return exists;
    }

    public UsernameExistsResponse setExists(boolean exists) {
        this.exists = exists;
        return this;
    }
}
