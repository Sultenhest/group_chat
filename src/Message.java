import java.io.Serializable;

public class Message implements Serializable {
    private String type;
    private String message;

    public Message() {
        setType("QUIT");
        setMessage("");
    }

    public Message( String type, String message ) {
        setType( type );
        setMessage( message );
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return getType() + getMessage();
    }
}
