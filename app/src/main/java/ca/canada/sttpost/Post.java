package ca.canada.sttpost;

public class Post {

//    private String id, body, timestamp, username, imgUrl, imgCode;
//
//    // Empty constructor
//    public Post() {
//    }
//
//    // Full constructor
//    public Post(String id, String body, String timestamp, String username,
//                String imgUrl, String imgCode) {
//        this.id = id;
//        this.body = body;
//        this.timestamp = timestamp;
//        this.username = username;
//        this.imgUrl = imgUrl;
//        this.imgCode = imgCode;
//    }
//
//    // Setters
//    public void setId(String id) {
//        this.id = id;
//    }
//    public void setBody(String body) {
//        this.body = body;
//    }
//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }
//    public void setUsername(String username) {
//        this.username = username;
//    }
//    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
//    public void setImgCode(String imgCode) { this.imgCode = imgCode; }
//
//    // Getters
//    public String getId() {
//        return this.id;
//    }
//    public String getBody() {
//        return this.body;
//    }
//    public String getTimestamp() {
//        return this.timestamp;
//    }
//    public String getUsername() {
//        return this.username;
//    }
//    public String getImgUrl() { return this.imgUrl; }
//    public String getImgCode() { return this.imgCode; }

    private String id;
    private String body;
    private String timestamp;
    private String username;
    private String imgUrl;


    // Empty constructor
    public Post() {
    }

    // Full constructor
    public Post(String id, String body, String timestamp, String username,
                String imgUrl) {
        this.id = id;
        this.body = body;
        this.timestamp = timestamp;
        this.username = username;
        this.imgUrl = imgUrl;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    // Getters
    public String getId() {
        return this.id;
    }
    public String getBody() {
        return this.body;
    }
    public String getTimestamp() {
        return this.timestamp;
    }
    public String getUsername() {
        return this.username;
    }
    public String getImgUrl() { return this.imgUrl; }

}