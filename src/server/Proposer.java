package server;

import utils.KVInterface;

import java.net.SocketTimeoutException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

public class Proposer {

    private ServerLogger serverLogger = new ServerLogger("Proposer");

    private static float paxosId;
    private static float identifierId;

    private String paxosValue;

    HashMap<String, String> serverIpMap;
    HashMap<String, Integer> serverPortMap;

    public Proposer(int serverNumber) {
        paxosId = 0.0f;
        identifierId = (float) serverNumber / 100;
    }


    public String startPaxos(String value) {
        String response = "";
        this.paxosValue = value;
        serverIpMap = ServerProperties.getIpMap();
        serverPortMap = ServerProperties.getPortMap();
        paxosId += 1;

        boolean isPromised = startToPrepare();
        boolean isAccepted = false;
        if (isPromised) {
            // if majority has responded with promise
            serverLogger.debug("The majority of acceptors has responded with a promise " +
                    "with paxosId " + paxosId);

            // continue to propose
            isAccepted = startToPropose();

        } else {
            // if no majority has responded with promise
            serverLogger.debug("The majority of acceptors did not respond with a promise with" +
                    " paxosId " + paxosId);
            response = "Prepare stage failed: The majority of acceptors did not respond with a promise.";
        }

        if (isAccepted) {
            // if majority has responded with an accepted message
            serverLogger.debug("The majority of acceptors has responded with an accepted message." +
                    " Consensus has been reached.");

            // if majority responds with an accepted message, call learners to commit and get response for
            // client requests
            response = prepareToCommit();

        } else {
            serverLogger.debug("The majority of acceptors did not respond with an accepted message.");
            response = "Accept stage failed: The majority of acceptors did not respond with an accepted message.";
        }
        return response;
    }

    private boolean startToPrepare() {
        serverLogger.debug("Starting to prepare with Paxos Id: " + paxosId);
        int count = 0;
        float acceptedPaxosId = -1;
        String acceptedValue = "";
        for(HashMap.Entry<String, String> entry : serverIpMap.entrySet()) {
            try{
                serverLogger.debug("Calling prepare at Ip: " + entry.getValue());
                Registry registry =
                        LocateRegistry.getRegistry(entry.getValue(), serverPortMap.get(entry.getKey()));
                KVInterface stub = (KVInterface) registry.lookup("utils.KVInterface");

                String response = stub.prepare(paxosId + identifierId);
                serverLogger.debug("Received response from prepare: " + response);
                String[] msg = response.split("\\s+");
                if (msg[0].equalsIgnoreCase("Promise")) {
                    count += 1;
                }
                // if length == 1 -> promise/failure message
                // if length == 4 -> promise message with a different promised value
                if (msg.length == 4) {
                    // format: promise, paxosId, accepted_paxosId, accepted_VALUE
                    // check if returned paxosId == this paxosId
                    // update acceptedPaxosId only if returned paxosId is larger
                    float returnedAcceptedPaxosId = Float.parseFloat(msg[2]);
                    if (Float.parseFloat(msg[1]) == paxosId + identifierId) {
                        if (returnedAcceptedPaxosId > acceptedPaxosId) {
                            acceptedPaxosId = returnedAcceptedPaxosId;
                            acceptedValue = msg[3];
                        }
                    } else {
                        serverLogger.error("Should not reach here. Something wrong, " +
                                "returned accepted Paxos Id is not equal to prepared Paxos Id");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                serverLogger.error("Response(msg) format error.");
            } catch(SocketTimeoutException | RemoteException | NotBoundException e) {
                // Proceed and check if the majority of acceptors respond with a promise
            }
        }
        serverLogger.debug(count + " servers have replied with a promise");

        if (count > serverIpMap.size() / 2) {
            if (acceptedValue.length() > 0) {
                paxosValue = acceptedValue;
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean startToPropose() {
        serverLogger.debug("Starting to propose with Paxos Id: " + paxosId);
        int count = 0;
        for(HashMap.Entry<String, String> entry : serverIpMap.entrySet()) {
            try{
                serverLogger.debug("Calling propose at Ip: " + entry.getValue());
                Registry registry =
                        LocateRegistry.getRegistry(entry.getValue(), serverPortMap.get(entry.getKey()));
                KVInterface stub = (KVInterface) registry.lookup("utils.KVInterface");

                String response = stub.propose(paxosId + identifierId, paxosValue);
                serverLogger.debug("Received response from propose: " + response);
                String[] msg = response.split("\\s+");
                if (msg[0].equalsIgnoreCase("Accept")) {
                    count += 1;
                }
                if (msg.length == 3) {
                    // format: promise, accepted_paxosId, accepted_VALUE
                    // check if returned accepted paxosId == this paxosId
                    // check if returned accepted_VALUE == this paxosValue
                    if (!(Float.parseFloat(msg[1]) == (paxosId + identifierId) &&
                            msg[2].equalsIgnoreCase(paxosValue))) {
                        serverLogger.error("Should not reach here. Something wrong, " +
                                "returned accepted Paxos Id is not equal to proposed Paxos Id or " +
                                "returned accepted Paxos value is not equal to proposed Paxos value");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                serverLogger.error("Response(msg) format error.");
            } catch(SocketTimeoutException | RemoteException | NotBoundException e) {
                // Proceed and check if the majority of acceptors respond with a promise
            }
        }
        serverLogger.debug(count + " servers have replied with an accepted message.");

        return count > serverIpMap.size() / 2;
    }

    private String prepareToCommit() {
        String message = "";
        for(HashMap.Entry<String, String> entry : serverIpMap.entrySet()) {
            try{
                serverLogger.debug("Calling learners accepted at Ip: " + entry.getValue());
                Registry registry =
                        LocateRegistry.getRegistry(entry.getValue(), serverPortMap.get(entry.getKey()));
                KVInterface stub = (KVInterface) registry.lookup("utils.KVInterface");

                message = stub.accepted(paxosValue);
                serverLogger.debug("Received response from learner: " + message);
            } catch(SocketTimeoutException | RemoteException | NotBoundException e) {
                // Proceed and check if the majority of acceptors respond with a promise
            }
        }
        return message;
    }
}
