public class SMessage {
    private  String command,text;

    public SMessage(String command, String text) {
        this.command = command;
        this.text = text;
    }
    public String  CreateMessage()
    {
        String pathabo = "S"+"#"+command+"#"+text;
        return  pathabo;
    }
}
