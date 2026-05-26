package org.aaf.webInterface.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.InputStream;

public class FirebaseInitializer {

	public static FirebaseApp initialize() {
		try {
			// Carregar o arquivo JSON de credenciais a partir de recursos
			InputStream serviceAccount = FirebaseInitializer.class.getClassLoader()
					.getResourceAsStream("tefamel-a634e-firebase-adminsdk-fbsvc-ec59c5a867.json");

			if (serviceAccount == null) {
				throw new RuntimeException("Não foi possível encontrar o arquivo JSON de credenciais.");
			}

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
			System.out.println("Firebase Admin SDK inicializado com sucesso.");

			return FirebaseApp.initializeApp(options);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erro ao inicializar o Firebase Admin SDK.");
		}
		return null;
	}

	public static void main(String[] args) {
		initialize(); // Inicializa o SDK
	}
}
