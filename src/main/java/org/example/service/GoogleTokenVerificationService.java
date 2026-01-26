package org.example.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerificationService {
    
    private static final String CLIENT_ID = "901364424092-lkasstpmskh4sn01bi9nvt9uir0tkdq2.apps.googleusercontent.com";
    
    private final GoogleIdTokenVerifier verifier;
    
    public GoogleTokenVerificationService() {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }
    
    public GoogleIdToken.Payload verifyToken(String idTokenString) throws Exception {
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new SecurityException("Invalid Google ID token");
        }
    }
}