public class User {
    protected String userId;
    protected String username;
    protected String passwordHash;
    protected String nickname;
    protected String firstName;
    protected String lastName;
    protected String address;
    protected String phoneNumber;
    protected String role;

    public User(String userId, String username, String pass, String nick, String fName, String lName, String addr, String phone, String role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = pass;
        this.nickname = nick;
        this.firstName = fName;
        this.lastName = lName;
        this.address = addr;
        this.phoneNumber = phone;
        this.role = role;
    }

    public boolean login(String inputUsername, String inputPass) {
        return this.username.equals(inputUsername) && this.passwordHash.equals(inputPass);
    }

    public void logout() {
        System.out.println("[System] " + firstName + " has logged out.");
    }

    public void updateProfile() {
        System.out.println("[System] Profile updated for: " + firstName);
    }

    public void changePassword(String newPass) {
        this.passwordHash = newPass;
        System.out.println("[System] Password changed successfully.");
    }

    public String getFirstName() { return firstName; }
    public String getRole() { return role; }
}