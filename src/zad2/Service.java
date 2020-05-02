/**
 * @author Strupiechowski Mateusz S18747
 */

package zad1;


import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Service {

    String country;
    String currencyCode;

    public Service(String country) {
        this.country = country;
        this.currencyCode = this.getCurrency();
    }

    String getCurrency() {

        String countryInfo = readJsonFromUrl("https://restcountries.eu/rest/v2/name/" + this.country + "?fullText=true");
        JSONArray jsonArray = new JSONArray(countryInfo);
        JSONArray data = (JSONArray) jsonArray.getJSONObject(0).get("currencies");
        return data.getJSONObject(0).get("code").toString();
    }

    public String getWeather(String city) {
        return readJsonFromUrl("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=09d24f13993b556df5239ae56c4330d1");
    }

    public Double getRateFor(String currencyCode) {
        String data = readJsonFromUrl("https://api.exchangeratesapi.io/latest?base=" + currencyCode + "&symbols=" + this.currencyCode);
        JSONObject currency = (JSONObject) new JSONObject(data).get("rates");
        return currency.getDouble(this.currencyCode);
    }

    public Double getNBPRate() {
        if (this.currencyCode.equals("PLN")) return 1.0;
        JSONArray data = new JSONObject(readJsonFromUrl("http://api.nbp.pl/api/exchangerates/rates/a/" + this.currencyCode + "/?format=json")).getJSONArray("rates");
        JSONObject rate = new JSONObject(data.getJSONObject(0).toString());
        return rate.getDouble("mid");
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String readJsonFromUrl(String url) {
        try {
            try (InputStream is = new URL(url).openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String jsonText = readAll(rd);
                rd.close();
                return jsonText;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

}  
