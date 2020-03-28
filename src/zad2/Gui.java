package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Gui {

    public Gui() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(this::start);
    }

    private void start() {
        JFrame frame = new JFrame("WEBCLIENTS");
        frame.setMinimumSize(new Dimension(1640, 920));

        JPanel panel = new JPanel(new FlowLayout());
        panel.setPreferredSize(new Dimension(1640, 920));

        JLabel city = new JLabel("podaj miasto: ");
        JLabel country = new JLabel("podaj kraj: ");
        JLabel currency = new JLabel("podaj kod waluty: ");

        JTextField cityChoose = new JTextField();
        cityChoose.setPreferredSize(new Dimension(200, 25));
        JTextField countryChoose = new JTextField();
        countryChoose.setPreferredSize(new Dimension(200, 25));
        JTextField currencyCode = new JTextField();
        currencyCode.setPreferredSize(new Dimension(200, 25));
        JTextArea weatherInfo = new JTextArea();
        weatherInfo.setLineWrap(true);

        JButton submitWet = new JButton("pokaż informacje o pogodzie w mieście");
        JButton submitCur = new JButton("pokaż informacje o kursie waluty");
        JButton website = new JButton("pokaż informacje o mieście na wikipedii");

        JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(1080,900));

        submitWet.addActionListener(e -> {
            String cityWeather = cityChoose.getText();
            String country1 = countryChoose.getText();
            String weatherForecast = new Service(country1).getWeather(cityWeather);
            weatherInfo.setText(" " + weatherForecast);
        });
        submitCur.addActionListener(e -> {
            String country1 = countryChoose.getText();
            String currCode = currencyCode.getText();
            Service service = new Service(country1);
            weatherInfo.setText("\nWaluta "+ service.getCurrency() +" kosztuje w " + currCode + " " + service.getRateFor(currCode).toString() + "\n");
            weatherInfo.append("Waluta "+ service.getCurrency() +" w NBP kosztuje "+ service.getNBPRate().toString());
        });
        website.addActionListener(e -> Platform.runLater(() -> {
            WebView webView = new WebView();
            jfxPanel.setScene(new Scene(webView));
            webView.getEngine().load("https://pl.wikipedia.org/wiki/" + cityChoose.getText());
        }));

        weatherInfo.setPreferredSize(new Dimension(400, 200));
        panel.add(city);
        panel.add(cityChoose);
        panel.add(country);
        panel.add(countryChoose);
        panel.add(currency);
        panel.add(currencyCode);
        panel.add(submitWet);
        panel.add(submitCur);
        panel.add(website);
        panel.add(weatherInfo);
        panel.add(jfxPanel);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


}
