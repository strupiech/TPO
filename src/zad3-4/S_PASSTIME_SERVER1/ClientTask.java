/**
 * @author Strupiechowski Mateusz S18747
 */

package S_PASSTIME_SERVER1;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask implements Runnable {

    Client client;
    List<String> requestList;
    boolean showResult;

    private ClientTask(Client client, List<String> requestList, boolean showResult) {
        this.client = client;
        this.requestList = requestList;
        this.showResult = showResult;
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        return new ClientTask(c, reqList, showRes);
    }

    public String get()
            throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public void run() {

    }
}
