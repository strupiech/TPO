package S_PASSTIME_SERVER1;

import java.util.List;
import java.util.Map;

public class OptionsDAO {
    private String host;
    private int port;
    private boolean concurMode;
    private boolean showSendRes;
    private Map<String, List<String>> clientsMap;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConcurMode() {
        return concurMode;
    }

    public void setConcurMode(boolean concurMode) {
        this.concurMode = concurMode;
    }

    public boolean isShowSendRes() {
        return showSendRes;
    }

    public void setShowSendRes(boolean showSendRes) {
        this.showSendRes = showSendRes;
    }

    public Map<String, List<String>> getClientsMap() {
        return clientsMap;
    }

    public void setClientsMap(Map<String, List<String>> clientsMap) {
        this.clientsMap = clientsMap;
    }

}
