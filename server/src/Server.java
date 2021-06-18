import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isLoggedIn;
    private String username;
    private int id;
    private boolean isAdmin;
    private static List<Server> loggedInUsers;


    Server(Socket socket){

        this.socket = socket;
        isLoggedIn = false;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String [] args){

        loggedInUsers = new ArrayList<>();

        try {
            ServerSocket ss = new ServerSocket(9999);
            while (true){
                Socket s = ss.accept();
                System.out.println("connected.");
                new Thread(new Server(s)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while(true){

            try {
                String data = in.readLine();
                Manipulate(data);
            } catch (IOException e) {
                continue;
            }
        }
    }

    private void Manipulate(String data) {

        String [] message = data.split("#");

        switch (message[0]){

            case "L":
                if(isLoggedIn)
                    break;

                if(CheckLogIn(message[1],message[2],message[3])){
                    isLoggedIn = true;
                    username = message[1];
                    loggedInUsers.add(this);
                    id = loggedInUsers.size()-1;
                    System.out.println(username+" "+id);
                    out.println("accepted");
                    if(message[3].equals("admin")) isAdmin = true;
                }
                else out.println("decline");
                break;

            case "S":
                if(isLoggedIn){
                    if(message[1].equals("show")){
                        System.out.println(message[2]);

                        out.println("users active right now:");
                        for(int i=0; i<loggedInUsers.size(); i++){
                            out.println(loggedInUsers.get(i).username);
                        }
                    }
                    if(message[1].equals("logout")){
                        System.out.println(message[2]);

                        isLoggedIn = false;
                        loggedInUsers.remove(id);
                        out.println("disconnect");
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.err.println("error in closing socket");
                        }
                    }
                }
                break;

            case "B":
                if(isLoggedIn){
                    if(isAdmin){
                        for(Server receiver:loggedInUsers){
                            if(receiver!=this)
                                receiver.out.println(message[1]+" (from: "+username+", admin)");
                        }
                    }
                    else out.println("sorry! only admins can send broadcast message.");
                }
                break;

            case "C":
                if(isLoggedIn){
                    for(Server receiver:loggedInUsers){
                        if(receiver.username.equals(message[1])) {
                            receiver.out.println(message[2]+" (from: "+username+")");

                            if(message[3].contains(".txt"))
                                // System.out.println("file will be sent");
                                receiver.out.println("size"+message[4]);
                                //receiver.out.println(message[4]);
                                receiver.ReceiveAndSend(this,message[4]);
                        }
                    }
                }
        }
    }

    private void ReceiveAndSend(Server sender,String size) {
        try
        {
            long filesize = Integer.parseInt(size);
            byte[] contents = new byte[10000];

            InputStream is = sender.socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            int bytesRead;
            int total = 0;            //how many bytes read

            while(total!=filesize)  //loop is continued until received byte=totalfilesize
            {
                bytesRead=is.read(contents);
                os.write(contents);
                total += bytesRead;
            }
            os.flush();
        }
        catch(Exception e)
        {
            System.err.println("Server could not transfer file.");
        }
    }

    private boolean CheckLogIn(String username, String password, String type) {

        FileReader fin = null;
        try {
            fin = new FileReader("data.txt");
        } catch (FileNotFoundException e) {
            System.err.println("couldn't read file");
        }

        Scanner in = new Scanner(fin);
        String [] entry;

        while(in.hasNextLine()){
            entry = in.nextLine().split("#");

            if(entry[0].equals(username) && entry[1].equals(password) && entry[2].equals(type)){
                in.close();
                return true;
            }
        }

        in.close();
        return false;
    }
}