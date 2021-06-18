import java.io.File;

public class CMessage {
    String receiver;
    String text;
    String sendFile;
    String fileSize;

    public CMessage(String receiver, String text, String sendFile, String fileSize) {
        this.receiver = receiver;
        this.text = text;
        this.sendFile = sendFile;
        this.fileSize = fileSize;
    }

    public CMessage(String receiver, String text) {
        this.receiver = receiver;
        this.text = text;
    }

    public  String CreateMessage ()
    {
        return  "C#"+receiver+"#"+text+"#"+sendFile+"#"+fileSize;
    }
}
