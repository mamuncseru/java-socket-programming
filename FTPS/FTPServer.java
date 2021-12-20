package FTPS;

import java.io.*;
import java.net.*;

public class FTPServer{
    
    public static void main(String args[]){
        try {
            ServerSocket soc = new ServerSocket(6666);
            System.out.println("Waiting for connection");
            new activity(soc.accept());
            soc.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
}
class activity{
    Socket soc;
    DataInputStream din;
    DataOutputStream dout;
    
    private String address = "F:/3rd year 1st semester/Computer networking/FTP/FTPS/";
    activity(Socket s){
        soc = s;
        try{
            din = new DataInputStream(soc.getInputStream());
            dout = new DataOutputStream(soc.getOutputStream());
            
        }catch(Exception e){}
        connect();
    }
    void connect(){
        try {
            System.out.println("Connected!!");

            while(true){
                System.out.println("Waiting for command");
                String command=din.readUTF();
                command = command.toUpperCase();

                if(command.equals("GET")){
                    System.out.println("\tGET command recieved..");
                    sendFile(address);
                }
                else if(command.equals("RECIEVE")){
                    System.out.println("\tRECIEVE command recieved..");
                    recieveFile();
                }
                else if(command.equals("LS")){
                    System.out.println("\tLS command recieved..");
                    sendName();
                }
                else System.exit(1);
                
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }
    void sendName() throws Exception{
        File f = new File(address);
        FileFilter filter = new FileFilter(){
            public boolean accept(File f){
                return f.getName().endsWith(".txt");
            }
        };

        File[] files = f.listFiles(filter);
        dout.writeUTF(files.length+"");
        for(File file: files){
            dout.writeUTF(file.getName());
        }
    }

    public void sendFile(String address) throws Exception{
        String fileName;
        fileName = din.readUTF();

        File f = new File(address+fileName);
        /// checking
        System.out.println("this file recommended: "+fileName);
        if(!f.exists()){
            dout.writeUTF("error");
            return;
        }
        dout.writeUTF("READY");
        FileInputStream fin = new FileInputStream(f);
        int value;
        do{
            value = fin.read();
            dout.writeUTF(String.valueOf(value));

        }while(value!=-1);
        fin.close();
        System.out.println(fileName+" sent!");
    }

    void recieveFile() throws Exception{
        String file = din.readUTF();
        
        File f = new File(address+file);
        String option="Y";
        if(f.exists()){
            
            dout.writeUTF("exist");
            option = din.readUTF();
        }
        else dout.writeUTF("ready");

        option = option.toUpperCase();
        if(!option.equals("Y")) return;
        
        
        FileOutputStream fout = new FileOutputStream(f);
        int val;
        String line;
        do{
            line = din.readUTF();
            val = Integer.parseInt(line);
            if(val !=-1){
                fout.write(val);
            }
        }while(val!=-1);
        fout.close();
        System.out.println("Recieved: "+file);
        
    }
}