

import RMI.RMIInterface;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Store implements RMIInterface {
    static Node node;
    public Store() {
        super();
    }
    public static void sendUDPMessage(String message,
   String ipAddress, int port) throws IOException {
      DatagramSocket socket = new DatagramSocket();
      InetAddress group = InetAddress.getByName(ipAddress);
      byte[] msg = message.getBytes();
      DatagramPacket packet = new DatagramPacket(msg, msg.length,
         group, port);
      socket.send(packet);
      socket.close();
   }
    public String join(String[] arg) throws RemoteException {
        String message = "";
        int temp= 0;
        String[] broadcast_ip={"",""};
        String[] ip = arg[0].split(":");
        List<String[]> file_lines = new ArrayList<>();
        try {
            Scanner scan = new Scanner(new File("Cluster.txt"));
            int counter = 1;
            while (scan.hasNextLine()) {
                file_lines.add(scan.nextLine().split(":"));
                counter++;
            }
            for(String[] line : file_lines){
                broadcast_ip[0] = line[0];
                broadcast_ip[1] = line[1];
                if(line[2].equals(ip[0])){
                    if(Integer.parseInt(line[4])%2!=0){
                        return "This Node already Joined Cluster!";
                    }
                    System.out.println("Entrei aqui");
                     temp = Integer.parseInt(line[4]);
                    temp++;
                    line[4] = Integer.toString(temp);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileWriter myWriter;
        try {
            myWriter = new FileWriter("Cluster.txt");
            for(String[] line2 : file_lines){
                for(int i=0;i<line2.length;i++){
                    if(i==line2.length-1){
                        myWriter.append(line2[i]);
                        myWriter.append("\n");
                    }else{
                        myWriter.append(line2[i].concat(":"));
                    }
                }
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sendUDPMessage(arg[2].concat(Integer.toString(temp)), broadcast_ip[0], Integer.parseInt(broadcast_ip[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        File node1File = new File("Nodes");
        for(File f : node1File.listFiles()){
            for(File k : f.listFiles()){
               String to = node.ring.get(k.getName());
               byte[] content;
            try {
                content = Files.readAllBytes(k.toPath());
                try (Socket socket = new Socket(to,8000)){
                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    dOut.writeInt(0);
                    dOut.writeInt(content.length);
                    dOut.write(content);
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String res = reader.readLine();
                    System.out.println(res);
                    k.delete();
            } catch (IOException e1) {
                e1.printStackTrace();
            }   
    } catch (Exception e) {
        e.printStackTrace();
    }
            }
        }
        message = "Joined Cluster!";
        System.out.println(message);
        return message;
    }

    public String leave(String[] arg) throws RemoteException {
        String message = "";
        int temp= 0;
        String[] broadcast_ip={"",""};
        String[] ip = arg[0].split(":");
        List<String[]> file_lines = new ArrayList<>();
        try {
            Scanner scan = new Scanner(new File("Cluster.txt"));
            int counter = 1;
            while (scan.hasNextLine()) {
                file_lines.add(scan.nextLine().split(":"));
                counter++;
            }
            for(String[] line : file_lines){
                if(line[2].equals(ip[0])){
                    broadcast_ip[0] = line[0];
                    broadcast_ip[1] = line[1];
                    if(Integer.parseInt(line[4])%2==0){
                        return "This Node already Left Cluster!";
                    }
                    System.out.println("Entrei aqui");
                    temp = Integer.parseInt(line[4]);
                    temp++;
                    line[4] = Integer.toString(temp);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileWriter myWriter;
        try {
            myWriter = new FileWriter("Cluster.txt");
            for(String[] line2 : file_lines){
                for(int i=0;i<line2.length;i++){
                    if(i==line2.length-1){
                        myWriter.append(line2[i]);
                        myWriter.append("\n");
                    }else{
                        myWriter.append(line2[i].concat(":"));
                    }
                }
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sendUDPMessage(arg[2].concat(Integer.toString(temp)), broadcast_ip[0], Integer.parseInt(broadcast_ip[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        node.ring.remove(ip[0]);
        File node1File = new File("Nodes");
        for(File f : node1File.listFiles()){
            for(File k : f.listFiles()){
               String to = node.ring.get(k.getName());
               byte[] content;
            try {
                content = Files.readAllBytes(k.toPath());
                try (Socket socket = new Socket(to,8000)){
                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    dOut.writeInt(0);
                    dOut.writeInt(content.length);
                    dOut.write(content);
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String res = reader.readLine();
                    System.out.println(res);
                    k.delete();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
        message = "Leaved Cluster!";
        System.out.println(message);
        return message;
    }
    public static void main(String[] args) throws IOException {
        String[] arg = new String[4];
        arg[0] = "224.0.0.1";
        arg[1] = "1025";
        arg[2] = "127.0.0.1";
        arg[3] = "8000";
        if(args.length==5){
            System.out.println("Incorrect number of arguments.\n");
            return;
        }
            Store store = new Store();
            File cluster = new File("Cluster.txt");
            if(cluster.createNewFile()){
                System.out.println("Cluster file Created Sucessfully");
            }
            File clusterlogs = new File("Clusterlogs.txt");
            if(clusterlogs.createNewFile()){
                System.out.println("Cluster file Created Sucessfully");
            } 
            File theDir = new File("Nodes");
            if (!theDir.exists()){
                theDir.mkdirs();
            }
           // System.out.println(args[0]+ args[0].length());
            if(!cluster.canWrite()){
                cluster.setWritable(true);
            }
            boolean Already_exists = false;
            List<String[]> file_lines = new ArrayList<>();
            Scanner scan = new Scanner(new File("Cluster.txt"));
            int counter =0;
            while (scan.hasNextLine()) {
                file_lines.add(scan.nextLine().split(":"));
                counter++;
            }
            FileWriter myWriter = new FileWriter("Cluster.txt");
            for(String[] line2 : file_lines){
                if(line2[2].equals(args[2])) Already_exists = true;
                for(int i=0;i<line2.length;i++){
                    if(i==line2.length-1){
                        myWriter.append(line2[i].concat("\n"));
                    }else{
                        myWriter.append(line2[i].concat(":"));
                    }
                }
               // if(counter!=0) myWriter.append("\n");
            }
            //if(counter!=0) myWriter.append("\n");
            if(!Already_exists){
                for(int i=0;i<arg.length;i++){
                    myWriter.append(args[i].concat(":"));
                }
                myWriter.append("0".concat("\n")); 
            }
            myWriter.close();
            RMIInterface Rmi = (RMIInterface) UnicastRemoteObject.exportObject(store,0);
            Registry res = LocateRegistry.getRegistry();
            res.rebind(args[2],Rmi);
            System.out.println(args);
            System.out.println("Node Store Ready!");
            node = new Node(args);
            new Thread(node).start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        List<String[]> file_lines = new ArrayList<>();
                        Scanner scan = new Scanner(new File("Cluster.txt"));
                        while (scan.hasNextLine()) {
                            file_lines.add(scan.nextLine().split(":"));
                        }
                       /* for(String[] line2 : file_lines){
                            if(line2[2].equals(args[2])) file_lines.remove(line2);
                        }*/
                        FileWriter myWriter = new FileWriter("Cluster.txt");
                        for(String[] line2 : file_lines){
                            if(line2[2].equals(args[2])) continue;
                            for(int i=0;i<line2.length;i++){
                                if(i==line2.length-1){
                                    myWriter.append(line2[i].concat("\n"));
                                }else{
                                    myWriter.append(line2[i].concat(":"));
                                }
                            }
                           // if(counter!=0) myWriter.append("\n");
                        }
                        myWriter.close();
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            });
    }
}
