public class AdminOperation {
    private static AdminOperation instance;
    AdminOperation(){}
    public static AdminOperation getInstance(){
        if(instance == null){
            instance = new AdminOperation();

        }
        return instance;
}
}
