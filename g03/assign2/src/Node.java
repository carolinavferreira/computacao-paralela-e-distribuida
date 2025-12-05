
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Hashing.ConsistentHashing;
import Hashing.Hash;

public class Node implements Runnable {
    public ConsistentHashing ring = new ConsistentHashing(4, new Hash());
    String[] arg;
    private static ConcurrentHashMap<String, byte[]> storage;
    public Node(String[] arg){
        this.arg = arg;
        storage = new ConcurrentHashMap<String, byte[]>();
    }
    @Override
    public void run() {
        File theDir = new File("Nodes".concat("\\").concat(arg[2]));
        if (!theDir.exists()){
            theDir.mkdirs();
        }
       // ring.add(arg[2]);
        int broad_port = Integer.parseInt(arg[1]);
        System.out.println(arg[0] + broad_port);
        try {
                new Thread(){
                    public void run() {
                    try {
                        FileWriter myWriter = new FileWriter("Clusterlogs.txt");
                        myWriter.write("str");
                        myWriter.close();
                        receiveUDPMessage(arg[0], broad_port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                }.start();
            } catch (Exception ex) {
                System.out.println("Server exceptiona: " + ex.getMessage());
                ex.printStackTrace();
        }
        int port = Integer.parseInt(arg[3]);
        try (ServerSocket serverSocket = new ServerSocket(port,50,InetAddress.getByName(arg[2]))) {
            System.out.println("Server is listening on port " + port);
            
            ExecutorService pool = Executors.newFixedThreadPool(3);
            while (true) {
                Socket socket = serverSocket.accept();
               /* InputStream in = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String received = reader.readLine();
                System.out.println(received);*/
                Runnable request = new AnswerRequest(socket,storage,this,arg[2]);
                pool.execute(request);
            }
            } catch (IOException ex) {
                System.out.println("Server exceptiond: " + ex.getMessage());
                ex.printStackTrace();
        }
    }
    public void receiveUDPMessage(String ip, int port) throws
         IOException {
      byte[] buffer=new byte[1024];
      MulticastSocket socket=new MulticastSocket(port);
      InetAddress group=InetAddress.getByName(ip);
      socket.joinGroup(group);
      while(true){
         System.out.println("Waiting for multicast message...");
         DatagramPacket packet=new DatagramPacket(buffer,
            buffer.length);
         socket.receive(packet);
         System.out.println(socket);
         String msg=new String(packet.getData(),
         packet.getOffset(),packet.getLength());
         System.out.println("Message received >> "+msg);
         String[] temp = msg.split(":");
         if(!temp[0].equals(arg[2])){
             System.out.println("this");
            ring.add(temp[0]);
         }
         System.out.println(ring.nodeMap());
         if("OK".equals(msg)) {
            break;
         }
      }
      socket.leaveGroup(group);
      socket.close();
   }
    public String getKey() {
      return arg[2].concat(":").concat((arg[4]));
    }
    public ConsistentHashing getRing(){
        return ring;
    }
}
/*
O ip do testclient corresponde ao ip do servidor que pretende contactar sendo que depois
o servidor verifica se tem essa chave caso não tenha verifica sequecialmente qual o primeiro servidor que esta
depois dessa chave no anel
*/