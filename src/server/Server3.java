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

public class Server3 {

    public static void main(String args[]) {

        ServerLogger serverLogger = new ServerLogger("Server3");

        try {
            HashMap<String, Integer> serverPortMap = ServerProperties.getPortMap();

            PaxosServant server = new PaxosServant(3);
            KVInterface stub = (KVInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(serverPortMap.get("Server3"));
            registry.bind("utils.KVInterface", stub);

            serverLogger.debug("Server3 is listening at ip: " + InetAddress.getLocalHost() +
                    ", port: " + serverPortMap.get("Server3") + " ...");

        } catch (UnknownHostException | AlreadyBoundException | RemoteException e) {
            serverLogger.error("Error setting up server.");
            serverLogger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
