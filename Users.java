import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract class Users {
    private String userid;
    private String username;
    private String userpassword;
    private String userRegisterTime;
    private String userRole;
    public Users (String userid, String username, String userpassword,String userRegisterTime, String userRole){
    this.userid = userid;
    this.username = username;
    this.userpassword = userpassword;
    this.userRegisterTime = userRegisterTime;
    this.userRole = userRole;
   @Override
    public String toString() {
        return String.format(
            "{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", \"user_register_time\":\"%s\", \"user_role\":\"%s\"}",
            userid, username, userpassword, userRegisterTime, userRole
        );
    }
    public String getUserRole() {
        return userRole;
    }
}
