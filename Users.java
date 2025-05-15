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
    if (!userid.matches("u_\\d{10}")){
         throw new IllegalArgumentException("Error");

    }
    if (username == null|| username.isEmpty() ){
        throw new IllegalArgumentException("your name can not be empty");
    }
    if (userpassword == null|| userpassword.isEmpty() ){
        throw new IllegalArgumentException("your password can not be empty");
    }
    

}
public void setRegisterTimeNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.userRegisterTime = LocalDateTime.now().format(formatter);
    }
}