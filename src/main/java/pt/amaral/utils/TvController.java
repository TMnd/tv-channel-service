package pt.amaral.utils;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@ApplicationScoped
public class TvController {

    private final String VLC_ADDRESS_URL = "localhost";
    private final Integer VLC_ADDRESS_PORT = 4212;
    private final String VLC_ADDRESS_PASSWORD = "test";

    public void sendCommand(String command) throws IOException {
        Socket socket = new Socket(VLC_ADDRESS_URL, VLC_ADDRESS_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String response;
        while ((response = in.readLine()) != null) {
            if (response.contains("VLC media")) {
                break;
            }
        }

        out.println(VLC_ADDRESS_PASSWORD);

        while ((response = in.readLine()) != null) {
            if (response.contains("Master")){
                out.println(command);
                break;
            }
        }

    }
}
