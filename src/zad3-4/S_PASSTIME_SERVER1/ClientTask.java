/**
 *
 *  @author Strupiechowski Mateusz S18747
 *
 */

package S_PASSTIME_SERVER1;


import java.util.ArrayList;
import java.util.List;

public class ClientTask implements Runnable{

    Client client;
    List<String> requestList;
    boolean showResult;

    private ClientTask(Client client, List<String> requestList, boolean showResult){
        this.client = client;
        this.requestList = requestList;
        this.showResult = showResult;
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        ClientTask clientTask = new ClientTask(c, reqList, showRes);

        return clientTask;
    }

    public String get() {
        return null;
    }

    @Override
    public void run() {

    }
}
