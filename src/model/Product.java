package model;

public  class Users {
    protected String userid;
    protected String username;
    protected String userpassword;
    protected String userRegisterTime;
    protected String userRole;
    public Users (String userid, String username, String userpassword, String userRole,String userRegisterTime){
    this.userid = userid;
    this.username = username;
    this.userpassword = userpassword;
    this.userRegisterTime = userRegisterTime;
    this.userRole = userRole;
    
    
    

}

    
    @Override
    public String toString() {
        return String.format(
            "{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", \"user_register_time\":\"%s\", \"user_role\":\"%s\"}",
            userid, username, userpassword, userRegisterTime, userRole
        );
    }
}
