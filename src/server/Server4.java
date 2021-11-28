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

public class Server4 {

    public static void main(String args[]) {

        ServerLogger serverLogger = new ServerLogger("Server4");

        try {
            HashMap<String, Integer> serverPortMap = ServerProperties.getPortMap();

            PaxosServant server = new PaxosServant(4);
            KVInterface stub = (KVInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(serverPortMap.get("Server4"));
            registry.bind("utils.KVInterface", stub);

            serverLogger.debug("Server4 is listening at ip: " + InetAddress.getLocalHost() +
                    ", port: " + serverPortMap.get("Server4") + " ...");

        } catch (UnknownHostException | AlreadyBoundException | RemoteException e) {
            serverLogger.error("Error setting up server.");
            serverLogger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
