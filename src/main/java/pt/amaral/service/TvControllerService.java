package pt.amaral.service;

import jakarta.enterprise.context.ApplicationScoped;
import pt.amaral.models.AppConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@ApplicationScoped
public class TvControllerService {

    private final AppConfiguration appConfiguration = AppConfiguration.getInstance();

    public void sendCommand(String command) throws IOException {
        Socket socket = new Socket(appConfiguration.getVlcAddress(), appConfiguration.getVlcPort());
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String response;
        while ((response = in.readLine()) != null) {
            if (response.contains("VLC media")) {
                break;
            }
        }

        out.println(appConfiguration.getVlcPassword());

        while ((response = in.readLine()) != null) {
            if (response.contains("Master")){
                out.println(command);
                break;
            }
        }

    }
}
