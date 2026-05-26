package org.aaf.webInterface.util;

public class DistanceCalculator {

    // Raio da Terra em metros
    private static final double EARTH_RADIUS = 6371000;

    /**
     * Calcula a distância entre dois pontos (latitude e longitude) usando a fórmula de Haversine.
     *
     * @param lat1 Latitude do ponto 1
     * @param lon1 Longitude do ponto 1
     * @param lat2 Latitude do ponto 2
     * @param lon2 Longitude do ponto 2
     * @return Distância em metros
     */
    public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // Convertendo as coordenadas de graus para radianos
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Diferença entre as latitudes e longitudes
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Fórmula de Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distância em metros
        return EARTH_RADIUS * c;
    }

    /**
     * Verifica se a distância entre dois pontos está dentro de um limite especificado.
     *
     * @param lat1 Latitude do ponto 1
     * @param lon1 Longitude do ponto 1
     * @param lat2 Latitude do ponto 2
     * @param lon2 Longitude do ponto 2
     * @param limite Distância limite em metros
     * @return true se a distância for menor ou igual ao limite, false caso contrário
     */
    public static boolean estaDentroDoLimite(double lat1, double lon1, double lat2, double lon2, double limite) {
        double distancia = calcularDistancia(lat1, lon1, lat2, lon2);
        return distancia <= limite;
    }

    public static void main(String[] args) {
        // Teste: localização 1 e localização 2
        double lat1 = -27.653412; // Exemplo: latitude do ponto 1
        double lon1 = -48.701449; // Exemplo: longitude do ponto 1

        double lat2 = -27.652761; // Exemplo: latitude do ponto 2
        double lon2 = -48.703007; // Exemplo: longitude do ponto 2

        // Verificar distância
        double distancia = calcularDistancia(lat1, lon1, lat2, lon2);
        System.out.println("Distância: " + distancia + " metros");

        // Verificar se está dentro de 300 metros
        boolean dentroDe300Metros = estaDentroDoLimite(lat1, lon1, lat2, lon2, 300);
        System.out.println("Está dentro de 300 metros? " + dentroDe300Metros);
    }
}
