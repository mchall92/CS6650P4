package server;

public class Learner {

    private KeyValue keyValue;

    private ServerLogger serverLogger = new ServerLogger("Learner");

    public Learner() {
        keyValue = new KeyValue();
    }

    public String commit(String paxosValue) {
        String response = "";
        // parse commit
        String[] commands = paxosValue.split("/");
        if (commands[0].equalsIgnoreCase("PUT") && commands.length == 3) {
            // if action is PUT
            keyValue.put(commands[1], commands[2]);
            response = "PUT request SUCCESS. PUT (Key / Value) : (" + commands[1] + " / " + commands[2] + ")";
        } else if (commands[0].equalsIgnoreCase("GET") && commands.length == 2) {
            if (keyValue.containsKey(commands[1])) {
                response = "GET request SUCCESS. GET (Key / Value) -> (" + commands[1] + " : " +
                        keyValue.get(commands[1]) + ")";
            } else {
                response = "GET request cannot find Key " + commands[1];
            }
        } else if (commands[0].equalsIgnoreCase("DELETE") && commands.length == 2) {
            if (keyValue.containsKey(commands[1])) {
                keyValue.delete(commands[1]);
                response = "DELETE request SUCCESS. DELETE key: " + commands[1];
            } else {
                response = "DELETE request cannot find Key " + commands[1];
            }
        } else {
            serverLogger.error("Incorrect paxosValue format. " +
                    "Should not reach here PaxosValue: " + paxosValue);
            return "Incorrect paxosValue format. Should not reach here. PaxosValue: " + paxosValue;
        }
        serverLogger.debug(response);
        return response;
    }
}
