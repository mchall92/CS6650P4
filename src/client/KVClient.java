package client;

import utils.KVInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.UUID;

public class KVClient {

    private static ClientLogger clientLogger = new ClientLogger("KVClient");
    private static KVInterface kvstub;

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            clientLogger.error("Enter server's IP and port number.");
            return;
        }

        int portNumber = -1;
        try {
            portNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            clientLogger.error("Invalid port number");
            return;
        }

        String ip = args[0];


        Registry registry =  LocateRegistry.getRegistry(ip, portNumber);
        kvstub = (KVInterface) registry.lookup("utils.KVInterface");

        try {
            runHardCodedCommand();
//            instructions();
//            runCustomCommands();

        } catch(Exception e){
            clientLogger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void execute(KVInterface kvstub, String[] request) {
        if (request[0].equalsIgnoreCase("put")) {
            clientLogger.debug(sendPutRequest(kvstub, request[1], request[2]));
        } else if (request[0].equalsIgnoreCase("get")) {
            clientLogger.debug(sendGetRequest(kvstub, request[1]));
        } else if (request[0].equalsIgnoreCase("delete")) {
            clientLogger.debug(sendDeleteRequest(kvstub, request[1]));
        } else {
            clientLogger.error("Incorrect request command and should not reach here!");
        }
    }

    private static String sendPutRequest(KVInterface kvstub, String key, String value) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        clientLogger.debug("Sending request- PUT " + key + " with Value: " + value + "   " + timestamp);
        try {
            timestamp = new Timestamp(System.currentTimeMillis());
            return kvstub.PUT(key, value) + "   " + timestamp;
        } catch (RemoteException e) {
            timestamp = new Timestamp(System.currentTimeMillis());
            return e.getMessage() + "   " + timestamp;
        }
    }

    private static String sendGetRequest(KVInterface kvstub, String key) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        clientLogger.debug("Sending request- GET key: " + key + "   " + timestamp);
        try {
            timestamp = new Timestamp(System.currentTimeMillis());
            return kvstub.GET(key) + "   " + timestamp;
        } catch (RemoteException e) {
            timestamp = new Timestamp(System.currentTimeMillis());
            return e.getMessage() + "   " + timestamp;
        }
    }

    private static String sendDeleteRequest(KVInterface kvstub, String key) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        clientLogger.debug("Sending request- DELETE key: " + key + "   " + timestamp);
        try {
            timestamp = new Timestamp(System.currentTimeMillis());
            return kvstub.DELETE(key) + "   " + timestamp;
        } catch (RemoteException e) {
            timestamp = new Timestamp(System.currentTimeMillis());
            return e.getMessage() + "   " + timestamp;
        }
    }


    /**
     * Hard-coded operations.
     */
    private static void runHardCodedCommand() {
        String[] put1 = new String[]{"put", "A", "1"};
        String[] put2 = new String[]{"put", "B", "2"};
        String[] put3 = new String[]{"put", "C", "3"};
        String[] put4 = new String[]{"put", "D", "4"};
        String[] put5 = new String[]{"put", "A", "5"};

        String[] get1 = new String[]{"get", "A"};
        String[] get2 = new String[]{"get", "B"};
        String[] get3 = new String[]{"get", "C"};
        String[] get4 = new String[]{"get", "D"};
        String[] get5 = new String[]{"get", "A"};

        String[] del1 = new String[]{"delete", "A"};
        String[] del2 = new String[]{"delete", "B"};
        String[] del3 = new String[]{"delete", "C"};
        String[] del4 = new String[]{"delete", "D"};

        String[] put6 = new String[]{"put", "A", "1"};

        String[] del5 = new String[]{"delete", "A"};

        String[] put7 = new String[]{"put", "B", "3"};

        execute(kvstub, put1);
        execute(kvstub, put2);
        execute(kvstub, put3);
        execute(kvstub, put4);
        execute(kvstub, put5);

        execute(kvstub, get1);
        execute(kvstub, get2);
        execute(kvstub, get3);
        execute(kvstub, get4);
        execute(kvstub, get5);

        execute(kvstub, del1);
        execute(kvstub, del2);
        execute(kvstub, del3);
        execute(kvstub, del4);

        execute(kvstub, put6);
        execute(kvstub, del5);
        execute(kvstub, put7);
    }

    private static void runCustomCommands() {
        while (true) {

            Scanner sc= new Scanner(System.in);
            String op = sc.nextLine();

            String[] operation = op.split("\\s+");

            if (operation.length == 1 && operation[0].equalsIgnoreCase("close")) {
                System.exit(0);
            }

            // check if operation from user is correct
            // if so, send request, if not, prompt user to input operation again
            // and output instructions
            if (operation.length >= 2) {
                if (operation[0].equalsIgnoreCase("PUT") && operation.length == 3) {
                    execute(kvstub, operation);
                } else if (operation[0].equalsIgnoreCase("GET") && operation.length == 2) {
                    execute(kvstub, operation);
                } else if (operation[0].equalsIgnoreCase("DELETE") && operation.length == 2) {
                    execute(kvstub, operation);
                } else {
                    instructions();
                }
            } else {
                instructions();
            }
        }
    }

    /**
     * Response to user input error.
     */
    private static void instructions() {
        String msg = "Operation format incorrect, please follow this format:\n"
                + "(1) PUT KEY VAULE\n"
                + "(2) GET KEY\n"
                + "(3) DELETE KEY\n\n"
                + "If you would like to exit, please enter: close";
        clientLogger.debug(msg);
    }
}

