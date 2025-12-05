

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnswerRequest implements Runnable {
    Socket so;
    private ConcurrentHashMap<String, byte[]> storage;
    private Node physicalNode;
    protected int portNum = -1;
    private String ip;
    public AnswerRequest(Socket so,ConcurrentHashMap<String, byte[]> storage,Node physicalNode,String ip) {
        super();
        this.so = so;
        this.ip = ip;
        this.storage = storage;
        this.physicalNode = physicalNode;
    }

    @Override
    public void run() {
        
        try{
            String res = "";
            String[] parse_res = {"",""};
            byte[] message = {0,0};
            boolean Already_exists = false;
            List<String[]> file_lines = new ArrayList<>();
            Scanner scan = new Scanner(new File("Cluster.txt"));
            int counter =0;
            int state=0;
            while (scan.hasNextLine()) {
                file_lines.add(scan.nextLine().split(":"));
                counter++;
            }
            for(String[] line2 : file_lines){
                if(line2[2].equals(ip)){
                    Already_exists = true;
                    state = Integer.parseInt(line2[4]);
                } 
            }
            if(Already_exists && state%2!=0){
                DataInputStream dIn = new DataInputStream(so.getInputStream());
                int a = dIn.readInt();
                if(a==0){
                   int length = dIn.readInt();                    // read length of incoming message
                   if(length>0) {
                       message = new byte[length];
                       dIn.readFully(message, 0, message.length); // read the message
                    }
                }else{
                    int length = dIn.readInt();                    // read length of incoming message
                   if(length>0) {
                       message = new byte[length];
                       dIn.readFully(message, 0, message.length); // read the message
                    }
                    parse_res[1] = new String(message);
                }
                switch (a) {
                   case 0:
                       if(checkUTF8(message)){
                           String string = new String(message);
                           res = put(hashing(string),message);
                           System.out.println(res);
                           
                       }else{
                           System.out.println("Invalid File");
                       }
                        //System.out.println(store());
                        break;
                   case 1:
                       res = delete(parse_res[1]);
                       System.out.println(res);
                       break;
                   case 2:
                       res= get(parse_res[1]);
                       System.out.println(res);
                       break;
                   default:
                       System.out.println("Insert ip: 224.0.0.X, where X:[0,255]");
                        break;
               }
               OutputStream output = so.getOutputStream();
               PrintWriter writer = new PrintWriter(output, true);
               writer.println(res);
            }else{
                System.out.println("This Node did not joined the cluster");
            }
        }catch(Exception e){
            System.out.println("Store exception: " + e);
        }
    }
    public String hashing(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

private static String bytesToHex(byte[] hash) {
    StringBuilder res = new StringBuilder(2 * hash.length);
    for (int j = 0; j < hash.length; j++) {
        String hex = Integer.toHexString(0xff & hash[j]);
        if(hex.length() == 1) {
            res.append('0');
        }
        res.append(hex);
    }
    return res.toString();
}
public boolean checkUTF8(byte[] barr){

    CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    ByteBuffer buf = ByteBuffer.wrap(barr);

    try {
        decoder.decode(buf);

    }
    catch(CharacterCodingException e){
        return false;
    }

    return true;
}
public String put(String key,byte[] value) {
    storage.put(key,value);
    String file_path = "Nodes".concat("\\").concat(ip).concat("\\").concat(key).concat(".txt");
    File pair = new File(file_path);
    try {
        if(pair.createNewFile()){
            System.out.println("Key-Value file Created Sucessfully");
        }
        try (FileOutputStream stream = new FileOutputStream(file_path)) {
            stream.write(value);
        }
        List<String[]> file_lines = new ArrayList<>();
        Scanner scan = new Scanner(new File("Cluster.txt"));
        int counter=0;
        while (scan.hasNextLine()) {
            file_lines.add(scan.nextLine().split(":"));
        }
        for(String[] line2 : file_lines){
            if(line2[2].equals(ip) || Integer.parseInt(line2[4])%2==0) continue;
            counter++;
            try (Socket socket = new Socket(line2[2],Integer.parseInt(line2[3]))){
                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    dOut.writeInt(0);
                    dOut.writeInt(value.length);
                    dOut.write(value);
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String res = reader.readLine();
                    System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
            if(counter>2){
                break;
            }
        }
        //FileWriter myWriter = new FileWriter(file_path);
       // myWriter.append(value);
        //myWriter.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return ("Server response: put " + key + " sucessfuly");
}

public String get(String key) {
    String res = "";
    String value = "";
    String file_path = "Nodes".concat("\\").concat(ip).concat("\\").concat(key).concat(".txt");
    File pair = new File(file_path);
    if (pair.exists()) {
      Scanner scan;
    try {
        scan = new Scanner(new File(file_path));
        while (scan.hasNextLine()) {
           value = value.concat(scan.nextLine());
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
        res = "Server response: Value= " + value;
    } else {
        res = "Server response: get key=" + key + " does not exist";
    }
    return res;
}

public String delete(String key) {
    String res = "";
    String file_path = "Nodes".concat("\\").concat(ip).concat("\\").concat(key).concat(".txt");
    File pair = new File(file_path);
    if (pair.exists()) {
        storage.remove(key);
        pair.delete();
        res = "Server response: delete key=" + key;
    } else {
        res = "Server response: delete key=" + key + " does not exist";
    }

    return res;
}

public String store() {
    String storeResponse = null;

    if (storage.isEmpty()) {
        storeResponse = "Server response: store is empty";
    } else {
        Set<String> keys = storage.keySet();

        StringBuilder sb = new StringBuilder("Server response:*");
        for (String key : keys) {
            sb.append("{key=" + key + ", value=" + storage.get(key) + "}*");
        }
        if (sb.toString().getBytes().length > 65000) {
            byte[] storeByte = sb.toString().getBytes();
            byte[] storeByteTrimmed = new byte[65000];

            for (int i=0; i<65000; i++) {
                storeByteTrimmed[i] = storeByte[i];
            }

            storeResponse = storeByteTrimmed.toString();
        } else {
            storeResponse = sb.toString();
        }
    }
    return storeResponse;
}
}