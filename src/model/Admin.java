
package model;
public class Admin extends Users {


    public Admin(String userid, String username, String userpassword, String userRole) {
        super(userid, username, userpassword, userRole, userRole);
        
    }
    @Override
public String toString() {
    return String.format(
            "{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", \"user_register_time\":\"%s\", \"user_role\":\"%s\"}",
            userid, username, userpassword, userRegisterTime, userRole
    );

    
}
}
