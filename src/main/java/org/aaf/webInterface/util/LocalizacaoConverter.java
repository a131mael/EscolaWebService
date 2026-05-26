package org.aaf.webInterface.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

import org.aaf.escolar.LocationRotaDTO;

public class LocalizacaoConverter {

    public static List<LocationRotaDTO> converterJsonParaDTO(String json) {
        // Cria o ObjectMapper do Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            // Mapeia o JSON para uma lista de LocationRotaDTO
            List<LocationRotaDTO> lista = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, LocationRotaDTO.class));
            return lista;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}