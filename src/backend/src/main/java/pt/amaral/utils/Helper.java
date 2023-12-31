package pt.amaral.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;
import java.util.Random;

public class Helper {

    public static Integer randomValue(Integer upperbound) {
        Random rand = new Random();
        return rand.nextInt(upperbound);
    }

    public static Map<String, Object> serilizeResponse(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.readValue(response, Map.class);
    }

}
