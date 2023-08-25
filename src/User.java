public class User {
    private String username;
    private String password;


    public User(String username, String password) {
        this.username = username;
        this.password = password;

    }

    //im setting getters for later use, depending on architecture
    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}