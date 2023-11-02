package pt.amaral.utils;

import com.arjuna.ats.internal.arjuna.Header;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@ApplicationScoped
public class HttpClient {
    public String get(String url, Map<String, String> headers) throws IOException {
        URL requestUrl = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");

        // Set custom headers
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            inputStreamReader.close();
            reader.close();
            connection.disconnect();

            return response.toString();
        } else {
            connection.disconnect();
            throw new IOException("HTTP GET request failed with response code: " + responseCode);
        }
    }


}
