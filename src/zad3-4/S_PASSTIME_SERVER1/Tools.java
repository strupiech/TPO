/**
 *
 *  @author Strupiechowski Mateusz S18747
 *
 */

package S_PASSTIME_SERVER1;


import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tools {


    public static Options createOptionsFromYaml(String fileName) throws IOException {
        Yaml yaml = new Yaml();
        OptionsDAO data;
        try(InputStream inputStream = new FileInputStream(new File(fileName))){
            data = yaml.loadAs(inputStream, OptionsDAO.class);
        }
        return new Options(data.getHost(), data.getPort(), data.isConcurMode(), data.isShowSendRes(), data.getClientsMap());
    }
}
