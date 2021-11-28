package server;

import java.util.Random;

public class Acceptor extends Thread {

    private float largestId;
    private boolean isProposalAccepted;
    private String proposalValue;
    private Random random;

    private ServerLogger serverLogger = new ServerLogger("Acceptor");

    public Acceptor() {
        largestId = 0.0f;
        isProposalAccepted = false;
        proposalValue = "";
        random = new Random();
    }

    public String receivedPrepare(float preparedId) {

        // 20% change that acceptor will go down
        spindle();

        String response = "";
        if (preparedId <= largestId) {
            serverLogger.debug("preparedId ( " + preparedId + " ) is less than or equal " +
                    "to the largest Id ( " + largestId +
                " ) current acceptor has seen so far. Will ignore this prepare request.");
            return "Fail";
        } else {
            // check if acceptor has accepted an acceptor
            serverLogger.debug("preparedId ( " + preparedId + " ) is greater than " +
                    "the largest Id ( " + largestId +
                    " ) current acceptor has seen so far. Will proceed to promise.");
            if (isProposalAccepted) {
                serverLogger.debug("isProposalAccepted is true, will respond to proposer " +
                    "with this acceptor's previously accepted value and paxosId");
                response = "Promise " + preparedId + " " + largestId + " " + proposalValue;
            } else {
                serverLogger.debug("isProposalAccepted is false, will respond to proposer " +
                        "with proposer's paxosId");
                // update largestId to preparedId
                largestId = preparedId;
                response = "Promise " + preparedId;
            }
        }
        serverLogger.debug("Response to prepare: " + response);
        return response;
    }

    public String receivedPropose(float proposedId, String value) {

        // 20% change that acceptor will go down
        spindle();

        // if proposedId == largestId, we continue to accept the proposal
        // otherwise, ignore it and respond with failure message
        String response = "";
        if (proposedId == largestId) {
            serverLogger.debug("proposedId ( " + proposedId + " ) is equal to largestId " +
                    "( " + largestId + " ), will accept this proposal.");
            isProposalAccepted = true;
            largestId = proposedId;
            proposalValue = value;
            response = "Accept " + proposedId + " " + value;
        } else {
            serverLogger.debug("proposedId ( " + proposedId + " ) is not equal to largestId " +
                    "( " + largestId + " ), will ignore this proposal.");
            response = "Fail";
        }

        serverLogger.debug("Response to proposal: " + response);
        return response;
    }

    private void spindle() {
        if (random.nextInt(3) == 0) {
            // put acceptor to sleep
            serverLogger.debug("Putting acceptor to sleep for 10 seconds.");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                serverLogger.error("Error occurs when putting thread to sleep.");
                e.printStackTrace();
            }
        }
    }
}
