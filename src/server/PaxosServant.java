package server;

import utils.KVInterface;

import java.rmi.RemoteException;

public class PaxosServant implements KVInterface {

    private Proposer proposer;
    private Acceptor acceptor;
    private Learner learner;

    private ServerLogger serverLogger = new ServerLogger("PaxosServant");

    public PaxosServant(int serverNumber) {
        proposer = new Proposer(serverNumber);
        acceptor = new Acceptor();
        learner = new Learner();
    }

    @Override
    public String PUT(String key, String value) {
        serverLogger.debug("Received PUT request: " + key + " / " + value);
        return proposer.startPaxos("PUT/" + key + "/" + value);
    }

    @Override
    public String GET(String key) throws RemoteException {
        serverLogger.debug("Received GET request: " + key);
        return proposer.startPaxos("GET/" + key);
    }

    @Override
    public String DELETE(String key) throws RemoteException {
        serverLogger.debug("Received DELETE request: " + key);
        return proposer.startPaxos("DELETE/" + key);
    }

    @Override
    public String prepare(float paxosId) {
        serverLogger.debug("Received PREPARE request, Paxos Id: " + paxosId);
        return acceptor.receivedPrepare(paxosId);
    }

    @Override
    public String propose(float paxosId, String value) {
        serverLogger.debug("Received PROPOSE request, Paxos Id: " + paxosId + ", value: " + value);
        return acceptor.receivedPropose(paxosId, value);
    }

    @Override
    public String accepted(String value) {
        serverLogger.debug("Received ACCEPTED request, value: " + value);
        return learner.commit(value);
    }
}
