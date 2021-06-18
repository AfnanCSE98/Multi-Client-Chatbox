public class LMessage {
    public LMessage(String username, String password, String type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    private String username;
    private String password;
    private String type;

   public  String CreateMessage()
   {
       return  "L#"+username+"#"+password+"#"+type;
   }
}

