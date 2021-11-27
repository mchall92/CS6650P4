package utils;

import java.net.SocketTimeoutException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KVInterface extends Remote {
    String PUT(String key, String value) throws RemoteException;

    String GET(String key) throws RemoteException;

    String DELETE(String key) throws RemoteException;

    String prepare(float paxosId) throws RemoteException, SocketTimeoutException;

    String propose(float paxosId, String value) throws RemoteException, SocketTimeoutException;

    String accepted(String value) throws RemoteException, SocketTimeoutException;
}
