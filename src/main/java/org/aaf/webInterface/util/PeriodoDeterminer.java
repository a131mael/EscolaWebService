package org.aaf.webInterface.util;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PeriodoDeterminer {

    // Método estático que retorna o período atual e o horário de início formatado
    public static PeriodoInfo determinarPeriodo() {
        // Obter o horário atual
        LocalTime agora = LocalTime.now();

        // Definir os horários de início para os períodos
        LocalTime manhaInicio = LocalTime.of(5, 0); // 05:00
        LocalTime manhaFim = LocalTime.of(8, 50); // 08:50

        LocalTime meioDiaInicio = LocalTime.of(10, 50); // 10:50
        LocalTime meioDiaFim = LocalTime.of(14, 30); // 14:30

        LocalTime tardeInicio = LocalTime.of(16, 0); // 16:00
        LocalTime tardeFim = LocalTime.of(20, 0); // 20:00

        // Obter o tempo atual (para retornar no formato completo ISO)
        LocalDateTime dataHoraAtual = LocalDateTime.now();

        // Verificar em qual período o horário atual se encaixa
        if (agora.isAfter(manhaInicio) && agora.isBefore(manhaFim)) {
            return new PeriodoInfo("Manhã", formatarDataHora(dataHoraAtual.toLocalDate().atTime(manhaInicio)));
        } else if (agora.isAfter(meioDiaInicio) && agora.isBefore(meioDiaFim)) {
            return new PeriodoInfo("Meio Dia", formatarDataHora(dataHoraAtual.toLocalDate().atTime(meioDiaInicio)));
        } else if (agora.isAfter(tardeInicio) && agora.isBefore(tardeFim)) {
            return new PeriodoInfo("Tarde", formatarDataHora(dataHoraAtual.toLocalDate().atTime(tardeInicio)));
        } else {
            return new PeriodoInfo("Fora do período definido", null);
        }
    }

    // Método para formatar o LocalDateTime para o padrão ISO 8601
    private static String formatarDataHora(LocalDateTime dataHora) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return dataHora.format(formatter);
    }

    // Método adicional para retornar a data atual no padrão ISO 8601
    public static String obterDataAtual() {
        LocalDateTime agora = LocalDateTime.now();
        return formatarDataHora(agora);
    }

    // Classe interna que armazena o nome do período e o horário de início
    public static class PeriodoInfo {
        private String periodo;
        private String inicio;

        public PeriodoInfo(String periodo, String inicio) {
            this.periodo = periodo;
            this.inicio = inicio;
        }

        public String getPeriodo() {
            return periodo;
        }

        public String getInicio() {
            return inicio;
        }
    }
}
