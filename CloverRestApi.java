package payment;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CloverRestApi {

    private static final String CLOVER_API_URL = "https://api.clover.com/v1/merchant/transactions";

    public void processPayment(String apiKey, double amount) throws Exception {
        URL url = new URL(CLOVER_API_URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        String data = "amount=" + (int) (amount * 100) + "&currency=USD"; // Amount in cents

        connection.getOutputStream().write(data.getBytes());

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        // Read response...
    }
}
