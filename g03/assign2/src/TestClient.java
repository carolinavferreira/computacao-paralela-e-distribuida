
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMI.RMIInterface;

public class TestClient {

    public static void main(String[] args) {
        // Create testClient object
        TestClient testClient = new TestClient();
        String[] arg = new String[3];
        arg[0] = "224.0.0.1:1025";
        arg[1] = "join";
        arg[2] = "Node.class";
        testClient.run(args);
    }
    private void run(String[] arg)  { 
        if((arg.length < 3)|| (arg.length > 4)){
            System.out.println("Incorrect number of arguments.\n");
            return;
        }
        try {
            String[] ip = arg[0].split(":");
            System.out.println(ip[0]);
            RMIInterface manager = (RMIInterface) LocateRegistry.getRegistry("localhost").lookup(ip[0]);
            String op = arg[1].toLowerCase();
            Operations ops;
            switch(op){
                 case "join":
                     System.out.println(manager.join(arg));
                     break;
                 case "leave":
                     System.out.println(manager.leave(arg));
                     break;
                 case "put":
                    ops = new Operations();       
                     ops.control(arg);
                    return;
                 case "get":
                 case "delete": {
                     ops = new Operations();
                     ops.control(arg);
                     break;
                 }
                 default:
                     System.out.println("Wrong arguments");
                     return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    }