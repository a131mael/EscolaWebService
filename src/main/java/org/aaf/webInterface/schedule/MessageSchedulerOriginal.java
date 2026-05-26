//package org.aaf.webInterface.schedule;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.locks.ReentrantLock;
//
//import javax.ejb.EJB;
//import javax.ejb.Lock;
//import javax.ejb.LockType;
//import javax.ejb.Schedule;
//import javax.ejb.Singleton;
//import javax.ejb.Startup;
//
//import org.aaf.escolar.LocationRotaDTO;
//import org.aaf.escolar.MemberDTO;
//import org.aaf.webInterface.firebase.FCMNotificationSender;
//import org.aaf.webInterface.firebase.FirebaseInitializer;
//import org.aaf.webInterface.util.DistanceCalculator;
//import org.aaf.webInterface.util.LocalizacaoConverter;
//import org.aaf.webInterface.util.PeriodoDeterminer;
//import org.escolar.service.MemberRegistration;
//import org.escolar.service.UsuarioAPPService;
//import com.google.firebase.FirebaseApp;
//
//@Singleton
//@Startup
//public class MessageSchedulerOriginal {
//
//    @EJB
//    private MemberRegistration userService;
//
//    @EJB
//    private UsuarioAPPService usuarioAPPService;
//
//    private final ReentrantLock lock = new ReentrantLock();
//    private static FirebaseApp firebaseApp;
//    private static final String carId = "11153984";
//    private Map<Long, Map<Integer, Boolean>> membrosNotificados = new ConcurrentHashMap<>(); // Thread-safe
//    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
//    private String ultimoPeriodo = "";
//
//    private void initializeFirebase() throws IOException {
//        if (firebaseApp == null) {
//            synchronized (MessageSchedulerOriginal.class) {
//                if (firebaseApp == null) {
//                    firebaseApp = FirebaseInitializer.initialize();
//                }
//            }
//        }
//    }
//
//    @Lock(LockType.WRITE)
//    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
//    public synchronized void enviarMensagensChegou() {
//        if (lock.tryLock()) {
//            try {
//                initializeFirebase();
//                String token = ApiService.getToken();
//                
//                PeriodoDeterminer.PeriodoInfo periodoAtual = PeriodoDeterminer.determinarPeriodo();
//
//                if (periodoAtual != null && periodoAtual.getInicio() != null) {
//                   
//                	resetaNotificados(periodoAtual);
//
//                    List<LocationRotaDTO> localizacaoList = getLocalizacoes(token, periodoAtual);
//
//                    if (localizacaoList != null && !localizacaoList.isEmpty()) {
//                        ordenaDescrescente(localizacaoList);
//                        LocationRotaDTO ultimoElemento = localizacaoList.get(0);
//                        
//                        List<MemberDTO> membros = getMembrosParaNotificar(periodoAtual);
//
//                        Map<Integer, List<MemberDTO>> membrosParaNotificarComDistancia = notificar(ultimoElemento, membros);
//                        executor.submit(() -> notificaUsuarios(membrosParaNotificarComDistancia));
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                lock.unlock();
//            }
//        } else {
//            System.out.println("Não foi possível obter o bloqueio, a execução foi pulada.");
//        }
//    }
//
//	private List<MemberDTO> getMembrosParaNotificar(PeriodoDeterminer.PeriodoInfo periodoAtual) {
//		List<MemberDTO> membros = new ArrayList<MemberDTO>();
//
//		switch (periodoAtual.getPeriodo()) {
//		    case "Manhã":
//		        membros = usuarioAPPService.getUsuariosValidosManha();
//		        break; 
//		    case "Meio Dia":
//		        membros = usuarioAPPService.getUsuariosValidosMeioDIa();
//		        break;
//		    case "Tarde":
//		        membros = usuarioAPPService.getUsuariosValidosNoite();
//		        break;
//		    default:
//		    	break;
//		}
//		return membros;
//	}
//
//	private List<LocationRotaDTO> getLocalizacoes(String token, PeriodoDeterminer.PeriodoInfo periodoAtual)
//			throws IOException {
//		String localizacao = ApiService.getLocalizacaoCarro(carId, periodoAtual.getInicio(),PeriodoDeterminer.obterDataAtual(), token);
//               
//		List<LocationRotaDTO> localizacaoList = LocalizacaoConverter.converterJsonParaDTO(localizacao);
//		return localizacaoList;
//	}
//
//	private void resetaNotificados(PeriodoDeterminer.PeriodoInfo periodoAtual) {
//		// Reseta notificações se mudou o período
//		if (!ultimoPeriodo.equals(periodoAtual.getPeriodo())) {
//		    membrosNotificados.clear();
//		    ultimoPeriodo = periodoAtual.getPeriodo();
//		}
//	}
//
//    private void notificaUsuarios(Map<Integer, List<MemberDTO>> membrosParaNotificar) {
//        for (Map.Entry<Integer, List<MemberDTO>> entry : membrosParaNotificar.entrySet()) {
//            Integer distancia = entry.getKey();
//            List<MemberDTO> membros = entry.getValue();
//
//            for (MemberDTO membro : membros) {
//                membrosNotificados.computeIfAbsent(membro.getId(), k -> new ConcurrentHashMap<>());
//                Map<Integer, Boolean> distanciasNotificadas = membrosNotificados.get(membro.getId());
//                
//                if (!distanciasNotificadas.getOrDefault(distancia, false)) {
//                    try {
//                        String mensagem = String.format("Estamos a %d metros.", distancia);
//                        FCMNotificationSender.sendNotification(membro.getTokenFCM(), mensagem, mensagem);
//                        distanciasNotificadas.put(distancia, true);
//                    } catch (Exception e) {
//                        System.out.println("Erro ao enviar notificação para o membro ID " + membro.getId() + ": " + e.getMessage());
//                    }
//                }
//            }
//        }
//    }
//
//    private void ordenaDescrescente(List<LocationRotaDTO> localizacaoList) {
//        localizacaoList.sort(Comparator.comparing(LocationRotaDTO::getHora).reversed());
//    }
//
//    private Map<Integer, List<MemberDTO>> notificar(LocationRotaDTO localizacaoCarro, List<MemberDTO> membros) {
//        Map<Integer, List<MemberDTO>> membrosPorDistancia = new HashMap<>();
//        int[] distancias = {60, 2000, 3000};
//
//        for (int distancia : distancias) {
//            membrosPorDistancia.put(distancia, new ArrayList<>());
//        }
//
//        if (localizacaoCarro != null) {
//            for (MemberDTO usuario : membros) {
//                double distancia = DistanceCalculator.calcularDistancia(localizacaoCarro.getLatitude(), localizacaoCarro.getLongitude(),usuario.getLatitude(), usuario.getLongitude());
//                
//                for (int i = distancias.length - 1; i >= 0; i--) {
//                    if (distancia < distancias[i]) {
//                        membrosPorDistancia.get(distancias[i]).add(usuario);
//                        break;
//                    }
//                }
//            }
//        }
//        return membrosPorDistancia;
//    }
//}
