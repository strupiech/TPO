package S_PASSTIME_SERVER1;

import java.util.HashMap;
import java.util.Map;

public class ServerLog {

    Map<String, String> clientsLog;

    public ServerLog() {
        clientsLog = new HashMap<>();
    }

    public void add(String clientId, String clientLog) {
        if (!clientsLog.containsKey(clientId))
            clientsLog.put(clientId, clientLog);
        else {
            String previousLog = clientsLog.get(clientId);
            previousLog += "\n" + clientLog;
            clientsLog.put(clientId, previousLog);
        }
    }

    public String get() {
        StringBuilder serverLog = new StringBuilder();
        clientsLog.forEach((id, clientLog) -> serverLog.append(id).append("\n").append(clientLog));
        return serverLog.toString();
    }

    public String get(String clientId) {
        return clientsLog.get(clientId);
    }

}
