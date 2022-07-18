package idmapping;

public class Identity {
    public Identity(String id, String type) {
        this.id = id;
        this.type = type;
    }

    String id;
    String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
