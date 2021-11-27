package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class ServerProperties {

    private static ServerLogger serverLogger = new ServerLogger("ServerProperties");

    public static HashMap<String, String> getIpMap(){
        HashMap<String, String> serverIpMap = new HashMap<>();
        try{
            Properties properties = new Properties();
            InputStream inputStream = ServerProperties.class.getResourceAsStream("serversIp.properties");
            properties.load(inputStream);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String server = (String) enumeration.nextElement();
                String ip = properties.getProperty(server);
                serverIpMap.put(server, ip);
            }
        } catch(FileNotFoundException e){
            serverLogger.error("File not found for serversIp.properties");
            serverLogger.error(e.getMessage());
            e.printStackTrace();
        } catch(IOException e){
            serverLogger.error("IO error for serversIp.properties" + e);
            serverLogger.error(e.getMessage());
            e.printStackTrace();
        }
        return serverIpMap;
    }

    public static HashMap<String, Integer> getPortMap(){
        HashMap<String, Integer> serverPortMap = new HashMap<>();
        try{
            Properties properties = new Properties();
            InputStream inputStream = ServerProperties.class.getResourceAsStream("serversPort.properties");
            properties.load(inputStream);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String server = (String) enumeration.nextElement();
                int port = Integer.parseInt(properties.getProperty(server));
                serverPortMap.put(server, port);
            }
        } catch(FileNotFoundException e){
            serverLogger.error("File not found for serversPort.properties");
            serverLogger.error(e.getMessage());
            e.printStackTrace();
        } catch(IOException e){
            serverLogger.error("IO error for serversPort.properties" + e);
            serverLogger.error(e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            serverLogger.error("Incorrect port number format for serversPort.properties" + e);
            serverLogger.error(e.getMessage());
            e.printStackTrace();
        }
        return serverPortMap;
    }


}
