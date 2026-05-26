package org.aaf.webInterface.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.FirebaseAuthException;

public class FirebaseAuthExample {

    public static String createCustomToken(String uid) {
        try {
            // Gerar um token personalizado para o UID do usuário
            String customToken = FirebaseAuth.getInstance().createCustomToken(uid);
            System.out.println("Token gerado com sucesso: " + customToken);
            return customToken;
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            System.err.println("Erro ao gerar token personalizado.");
            return null;
        }
    }

    public static void main(String[] args) {
        String uid = "user123"; // UID do usuário
        createCustomToken(uid);
    }
}
