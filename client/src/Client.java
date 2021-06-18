import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    private static boolean isLoggedIn;
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Scanner input;
    private  static String filename;
    private static int fc=0;

    public static void main(String[] args) {
        //String filename;

        try {
            isLoggedIn = false;
            socket = new Socket("localhost",9999);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            input = new Scanner(System.in);
            out = new PrintWriter(socket.getOutputStream(),true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            System.out.println("Username:");
            String username = input.nextLine();
            System.out.println("Password:");
            String password = input.nextLine();
            System.out.println("Account type:");
            String type = input.nextLine();

            LMessage lMessage = new LMessage(username, password, type);
            out.println(lMessage.CreateMessage());

            String returnMessage = null;
            try {
                returnMessage = in.readLine();
            } catch (IOException e) {
                System.out.println("couldn't read the message");
            }
            if (returnMessage.equals("accepted")){
                System.out.println("Logged in successfully");
                break;
            }
        }

        new Thread(()->receive()).start();

        System.out.println("Enter your message type: S,B,C");
        while (true){
            String messageType = input.nextLine();
            //switch (messageType){
                //case "S":
            if(messageType.equals("S")) {
                System.out.println("Command:");
                String command = input.nextLine();
                System.out.println("Text:");
                String text = input.nextLine();

                SMessage s = new SMessage(command, text);
                out.println(s.CreateMessage());

            }

            //case "B":
            if(messageType.equals("B")) {
                System.out.println("Enter your broadcast message:");
                String bText = input.nextLine();
                BMessage bMessage = new BMessage(bText);
                out.println(bMessage.CreateMessage());
                //break;
            }
                //case "C":
            if(messageType.equals("C")) {
                System.out.println("Receiver:");
                String receiver = input.nextLine();
                System.out.println("Text:");
                String cText = input.nextLine();
                System.out.println("Send File? Y/N:");

                if (input.nextLine().equals("Y")) {
                    System.out.println("Enter file name :");
                    filename = input.nextLine();
                    //filename+=".txt";
                    File file = new File(filename);
                    CMessage c = new CMessage(receiver, cText,filename, Long.toString(file.length()));
                    out.println(c.CreateMessage());

                    try{
                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        OutputStream os = socket.getOutputStream();
                        byte[] contents;
                        long fileLength = file.length();
                        out.println(String.valueOf(fileLength));
                        out.flush();

                        long current = 0;
                        //System.out.println(fileLength);
                        while (current != fileLength) {
                            int size = 10000;
                            if (fileLength - current >= size)
                                current += size;
                            else {
                                size = (int) (fileLength - current);
                                current = fileLength;
                            }
                           // System.out.println(size);
                            contents = new byte[size];
                            bis.read(contents, 0, size);
                            os.write(contents);
                            //System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
                        }
                        os.flush();
                        System.out.println("File sent successfully!");
                    } catch (Exception e) {
                        System.err.println("Could not transfer file.");
                    }
                    //out.println("Downloaded.");
                    out.flush();


                       // OutputStream os = socket.getOutputStream();


                }
                else
                {
                    CMessage c = new CMessage(receiver, cText);
                    out.println(c.CreateMessage());
                }
            }
        }
    }

    private static void receive() {
        while(true){
            try {
                String returnMessage = in.readLine();
                if(returnMessage.contains("size"))
                {

                    try {
                        String size = returnMessage.substring(4);
                       //System.out.println(strRecv);
                        //strRecv = in.readLine();
                        int filesize = Integer.parseInt(size);
                        //System.out.println(filesize);
                        byte[] contents = new byte[10000];
                        fc++;
                        int fileLen = filename.length();//filename lenth
                        FileOutputStream fos = new FileOutputStream(filename.substring(0, fileLen - 5) + Integer.toString(fc) + ".txt");
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        InputStream is = socket.getInputStream();

                        int bytesRead = 0;
                        int total = 0;            //how many bytes read

                        while (total <= filesize)          //ekhane < dilam
                        {
                            bytesRead = is.read(contents);
                            total += bytesRead;
                            bos.write(contents, 0, bytesRead);
                        }
                        bos.flush();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else
                {
                    System.out.println(returnMessage);
                }

                if(returnMessage.equals("disconnect"))
                    System.exit(0);

            }
            catch(Exception e)
            {
                System.err.println("Could not receive file.");
            }
        }
    }
}