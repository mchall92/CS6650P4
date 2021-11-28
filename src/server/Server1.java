package server;

import utils.KVInterface;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server1 {

    public static void main(String args[]) {

        ServerLogger serverLogger = new ServerLogger("Server1");

        try {
            HashMap<String, Integer> serverPortMap = ServerProperties.getPortMap();

            PaxosServant server = new PaxosServant(1);
            KVInterface stub = (KVInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(serverPortMap.get("Server1"));
            registry.bind("utils.KVInterface", stub);

            serverLogger.debug("Server1 is listening at ip: " + InetAddress.getLocalHost() +
                    ", port: " + serverPortMap.get("Server1") + " ...");

        } catch (UnknownHostException | AlreadyBoundException | RemoteException e) {
            serverLogger.error("Error setting up server.");
            serverLogger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
