package org.aaf.webInterface.firebase;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

public class FCMNotificationSender {

	public static void sendNotification(String deviceToken, String title, String body) {
	    FirebaseInitializer.initialize();

	    // Crie a notificação
	    Notification notification = Notification.builder()
	            .setTitle(title)
	            .setBody(body)
	            .build();

	    // Configure o AndroidConfig para especificar o canal
	    AndroidConfig androidConfig = AndroidConfig.builder()
	            .setNotification(AndroidNotification.builder()
	                    .setChannelId("canal_tefa") // Substitua pelo ID do canal
	                    .setSound("chegando") // Nome do som personalizado sem extensão
	                    .build())
	            .build();

	    // Construa a mensagem
	    Message message = Message.builder()
	            .setToken(deviceToken)
	            .setNotification(notification)
	            .setAndroidConfig(androidConfig) // Adicione o AndroidConfig
	            .build();

	    try {
	        String response = FirebaseMessaging.getInstance().send(message);
	        System.out.println("Notificação enviada com sucesso. ID: " + response);
	    } catch (Exception e) {
	     //   e.printStackTrace();
	        System.err.println("Erro ao enviar notificação.");
	    }
	}


    public static void main(String[] args) {
     //   String deviceToken = "e5MB3LGpR0KdwT6lS7JH2z:APA91bF1RFSUeTq-KUDFn-LXKfxF1UwbHXeE67cSiEQFZ46cei91T1ODEU8q-geGJTliPF54cQ6V5LdxsvPzr_3UEAFPoJF9fZpxpu1NHSqCsyQqawFI_Io"; // Token do dispositivo
      
        String deviceToken = "e5MB3LGpR0KdwT6lS7JH2z:APA91bF1RFSUeTq-KUDFn-LXKfxF1UwbHXeE67cSiEQFZ46cei91T1ODEU8q-geGJTliPF54cQ6V5LdxsvPzr_3UEAFPoJF9fZpxpu1NHSqCsyQqawFI_Io"; // Token do dispositivo
        
        String title = "TEFAMEL !!!!";
        String body = "TEste de envio de mensagem para =)";
        sendNotification(deviceToken, title, body);
    }
}
