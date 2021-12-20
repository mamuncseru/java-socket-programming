import java.io.*;
import java.net.*;

import javax.sound.midi.Receiver;

public class FTPClient {
    public static void main(String args[]) {
        try {
            Socket soc = new Socket("localhost", 6777);
            activity act = new activity(soc);
            act.activate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class activity {
    Socket soc;
    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;

    private String address="C:/Users/HP/Desktop/Java Socket Programming/";
    public activity(Socket soc) {
        try {
            this.soc = soc;
            din = new DataInputStream(soc.getInputStream());
            dout = new DataOutputStream(soc.getOutputStream());
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void activate() throws Exception {
        System.out.println("Type below Instruction to communicate with server\n"+
            "ls = to get list of server .txt file\n" + 
            "send = to send file to server \n" + 
            "recieve =to recieve file from server\n" + 
            "exit = to disconnect the communication\n");

        while(true) {
            System.out.print("\nType (ls, send, receive, exit): ");
            String command = br.readLine();
            command = command.toLowerCase();
            if(command.equals("ls")) getIndex();
            else if(command.equals("send")) {
                sendFile();
            }
            else if(command.equals("receive")) {
                recieveFile();
            }
            else if (command.equals("exit")) {
                System.exit(1);
            }
            else {
                System.out.println("You typed wrong command\n" +
                    "\"ls send recieve exit\"\tare the right command");
            }
        }
    }

    void getIndex() throws Exception {
        dout.writeUTF("LS");
        int len = Integer.parseInt(din.readUTF());
        System.out.println("Showing txt file of server.");
        if (len == 0) {
            System.out.println("No txt file found");
        }
        else for (int i = 0; i < len; i++) {
            System.out.println(din.readUTF());
        }
    }

    public void sendFile(String address) throws Exception {
        String fileName;
        System.out.print("\nwrite fileName you want to send: ");
        fileName = br.readLine();

        File f = new File(address + fileName);
        if (!f.exists()) {
            System.out.println("File doesn't exist: " + fileName);
            return;
        }

        dout.writeUTF("RECEIVE");
        dout.writeUTF(fileName);

        String feedback = din.readUTF();

        if (feedback.equals("exist")) {
            System.out.print("\nServer already contains the file: "+
            fileName+"\nDo you want to override(Y/N):");
            String command = br.readLine();
            command = command.toUpperCase();
            dout.writeUTF(command);

            if(command.equals("N")) return;
        }

        FileInputStream fin = new FileInputStream(f);
        int value;
        do {
            value = fin.read();
            dout.writeUTF(String.valueOf(value));

        }while(value != -1);
        fin.close();
        System.out.println(fileName+ "sent!\n");
    }

    void recieveFile() throws Exception {
        System.out.print("\nWrite file name: ");
        String file = br.readLine();

        File f = new File(address + file);
        String option = "Y";

        if (f.exists()) {
            System.out.print("\nFile already exists" + 
                "do you want to override.(Y/N): ");
                option = br.readLine();
        }

        option = option.toUpperCase();
        if(option.equals("N")) {
            System.out.println("You refused!\n\n");
        }

        else {
            // making contact Now

            dout.writeUTF("GET");
            dout.writeUTF(file);

            String temp = din.readUTF();
            if(temp.equals("errors")) {
                System.out.println("Server does not contain the file: "+file);
                return;
            }

            FileOutputStream fout = new FileOutputStream(f);
            int val;
            String line;
            do {
                line = din.readUTF();
                val = Integer.parseInt(line);
                if (val !=-1) {
                    fout.write(val);
                }
            } while(val != -1);
            fout.close();
            System.out.println("Recieved: "+file+"\n");
        }
    }

}
