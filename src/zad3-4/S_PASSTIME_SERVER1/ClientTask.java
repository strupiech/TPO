/**
 * @author Strupiechowski Mateusz S18747
 */

package S_PASSTIME_SERVER1;

import java.util.List;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {

    private ClientTask(Client client, List<String> requestList, boolean showResult) {
        super(() -> {
            client.connect();
            client.send("login " + client.getId());
            for (String request : requestList){
                String result = client.send(request);
                if (showResult)
                    System.out.println(result);
            }
            return client.send("bye and log transfer");
        });
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        return new ClientTask(c, reqList, showRes);
    }
}
