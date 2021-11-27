package server;

import utils.KVInterface;

import java.net.SocketTimeoutException;
import java.rmi.RemoteException;

public class PaxosServant implements KVInterface {

    private Proposer proposer;
    private Acceptor acceptor;
    private Learner learner;

    public PaxosServant(int serverNumber) {
        proposer = new Proposer(serverNumber);
        acceptor = new Acceptor();
        learner = new Learner();
    }

    @Override
    public String PUT(String key, String value) {
        return proposer.startPaxos("PUT/" + key + "/" + value);
    }

    @Override
    public String GET(String key) throws RemoteException {
        return proposer.startPaxos("GET/" + key);
    }

    @Override
    public String DELETE(String key) throws RemoteException {
        return proposer.startPaxos("DELETE/" + key);
    }

    @Override
    public String prepare(float paxosId) {
        return acceptor.receivedPrepare(paxosId);
    }

    @Override
    public String propose(float paxosId, String value) {
        return acceptor.receivedPropose();
    }

    @Override
    public String accepted(String value){
        return null;
    }
}
