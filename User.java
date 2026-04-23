public class User {
    private String userId;
    private String username;
    private String passwordHash;
    private String nickname;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String role;

    public User(String userId, String username, String pass, String nick, String fName, String lName, String addr,
            String phone, String role) {
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
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public void changePassword(String newPass) {
        this.passwordHash = newPass;
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public String getFirstName() {
        return firstName;
    }

    public String getRole() {
        return role;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.passwordHash;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPhone() {
        return this.phoneNumber;
    }
}