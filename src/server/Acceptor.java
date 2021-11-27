package server;

public class Acceptor {

    private float largestId;
    private boolean isProposalAccepted;
    private String proposalValue;

    private ServerLogger serverLogger = new ServerLogger("Acceptor");

    public Acceptor() {
        largestId = 0.0f;
        isProposalAccepted = false;
        proposalValue = "";
    }

    public String receivedPrepare(float preparedId) {
        String response = "";
        if (preparedId <= largestId) {
            serverLogger.debug("preparedId is less than or equal to the largest Id "
                + "current acceptor has seen so far. Will ignore this prepare request.");
            return "Fail";
        } else {
            // check if acceptor has accepted an acceptor
            if (isProposalAccepted) {
                response =
            }
        }
        return null;
    }

    public String receivedPropose() {
        return null;
    }

    private void spindle() {
        // put acceptor to sleep

    }
}
