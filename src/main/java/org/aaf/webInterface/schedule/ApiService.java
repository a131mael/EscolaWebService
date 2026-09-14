package org.aaf.webInterface.schedule;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiService {

    private static final int CONNECT_TIMEOUT_MS = 8000;
    private static final int READ_TIMEOUT_MS = 8000;

    // Cache do token -- evita logar de novo na Getrak a cada chamada (esse
    // metodo era chamado a cada 5s pelo MessageScheduler sem nenhum cache,
    // fazendo login OAuth completo toda vez). Token da Getrak dura tipicamente
    // bem mais que os 50min de margem usados aqui.
    private static volatile String cachedToken;
    private static volatile long tokenExpiraEm;

    // Método para obter o token de acesso (com cache)
    public static synchronized String getToken() throws IOException {
        long agora = System.currentTimeMillis();
        if (cachedToken != null && agora < tokenExpiraEm) {
            return cachedToken;
        }

        String url = "https://api.getrak.com/newkoauth/oauth/token";
        String urlParameters = "grant_type=password&username=tefamel.abimael@federaltracker&password=10203040";

        // Configuração do cabeçalho Authorization
        String authorization = "Basic ZmVkZXJhbHRyYWNrZXI6OGRSVmpVMU9lcnVP";

        // Configuração da URL
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setConnectTimeout(CONNECT_TIMEOUT_MS);
        con.setReadTimeout(READ_TIMEOUT_MS);

        try {
            // Definir o método como POST
            con.setRequestMethod("POST");

            // Definir o cabeçalho Authorization
            con.setRequestProperty("Authorization", authorization);

            // Definir o cabeçalho Content-Type como x-www-form-urlencoded
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Habilitar o envio de dados no corpo da requisição
            con.setDoOutput(true);

            // Enviar os parâmetros do corpo
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                wr.write(postData);
                wr.flush();
            }

            // Obter o código de resposta
            int responseCode = con.getResponseCode();

            // Ler a resposta do servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // O token está na resposta em formato JSON, por exemplo {"access_token":"seu_token"}
            String jsonResponse = response.toString();

            // Retornar o token extraído da resposta
            String token = extractToken(jsonResponse);
            cachedToken = token;
            tokenExpiraEm = agora + (50L * 60 * 1000); // cache por 50min
            return token;
        } finally {
            con.disconnect();
        }
    }

    // Método para extrair o token do JSON
    private static String extractToken(String jsonResponse) {
        // Você pode usar alguma biblioteca JSON como Jackson ou Gson
        // Para simplificar, vamos usar regex para pegar o token
        String regex = "\"access_token\":\"(.*?)\"";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(jsonResponse);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

 // Método para pegar a localização do carro
    public static String getLocalizacaoCarro(String carId, String startDate, String endDate, String token) throws IOException {
        // Construir a URL com os parâmetros
        String url = "https://api.getrak.com/v0.1/public/trajetos/" + carId + "/" + startDate + "/" + endDate;

        // Configuração da URL
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setConnectTimeout(CONNECT_TIMEOUT_MS);
        con.setReadTimeout(READ_TIMEOUT_MS);

        try {
            // Definir o método como GET
            con.setRequestMethod("GET");

            // Adicionar o cabeçalho Authorization com Bearer + Token
            con.setRequestProperty("Authorization", "Bearer " + token);

            // Obter o código de resposta
            int responseCode = con.getResponseCode();

            // Ler a resposta do servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Retornar a resposta da API (os trajetos)
            return response.toString();
        } finally {
            con.disconnect();
        }
    }
    
    

}
