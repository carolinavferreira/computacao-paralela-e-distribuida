package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {
    String join(String[] args) throws RemoteException;
    String leave(String[] args) throws  RemoteException;
}
