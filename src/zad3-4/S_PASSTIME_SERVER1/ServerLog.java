package S_PASSTIME_SERVER1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerLog {

    private List<String> serverLog;
    private Map<Integer, String> portsClients;
    private Map<Integer, List<String>> clientsLog;

    public ServerLog() {
        serverLog = new ArrayList<>();
        portsClients = new HashMap<>();
        clientsLog = new HashMap<>();
    }

    public void addClient(Integer clientChannelPort, String id) {
        portsClients.put(clientChannelPort, id);
    }

    public void addToServerLog(Integer clientChannelPort, String activity) {
        serverLog.add(portsClients.get(clientChannelPort) + " " + activity + "\n");
    }

    public void addToClientLog(Integer clientChannelPort, String clientLog) {
        if (clientLog.equals("logged in")) {
            clientsLog.put(clientChannelPort, new ArrayList<>());
            clientsLog.get(clientChannelPort).add("=== " + portsClients.get(clientChannelPort) + " log start ===\n" + clientLog + "\n");
        } else if (clientLog.equals("logged out"))
            clientsLog.get(clientChannelPort).add(clientLog + "\n=== " + portsClients.get(clientChannelPort) + " log end ===\n");
        else
            clientsLog.get(clientChannelPort).add(clientLog + "\n");
    }

    public String get() {
        return get(serverLog);
    }

    public String get(Integer clientChannelPort) {
        return get(clientsLog.get(clientChannelPort));
    }

    public String get(List<String> logs) {
        StringBuilder log = new StringBuilder();
        for (String singleLog : logs) {
            log.append(singleLog);
        }
        return log.toString();
    }

    public String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        Calendar cal = Calendar.getInstance();

        return dateFormat.format(cal.getTime());
    }

    public void clearClientLog(Integer clientPort) {
        clientsLog.remove(clientPort);
        portsClients.remove(clientPort);
    }

    public void clearServerLogIfMoreThan500Records(){
        if (serverLog.size() < 500) return;

        serverLog.subList(0, serverLog.size() - 500).clear();
    }
}
