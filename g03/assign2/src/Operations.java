import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

public class Operations {
    public void control(String[] args){
        String [] node_ip = args[0].split(":");
        try (Socket socket = new Socket(node_ip[0],Integer.parseInt(node_ip[1]))){
            if(args[1].equals("put")){
                File pair = new File(args[2]);
            if(pair.exists()){
                byte[] buffer = new byte[(int) pair.length()];
                buffer = Files.readAllBytes(pair.toPath());
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                dOut.writeInt(0);
                dOut.writeInt(buffer.length); // write length of the message
                dOut.write(buffer);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String res = reader.readLine();
                System.out.println(res);
            }
            }else if(args[1].equals("delete")){
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                dOut.writeInt(1);
                dOut.writeInt(args[2].length());
                dOut.writeBytes(args[2]);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String res = reader.readLine();
                System.out.println(res);
            }else{
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                dOut.writeInt(2);
                dOut.writeInt(args[2].length());
                dOut.writeBytes(args[2]);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String res = reader.readLine();
                System.out.println(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
-Cada No tem uma pasta em que o nome dessa pasta corresponde ao ip do NO
-Cada ficheiro representa um par key(nome do ficheiro) value(conteudo do ficheiro)
*/ 