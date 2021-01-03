package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

@SpringBootApplication
public class AppLoader {

    public static void main(String[] args) {
        SpringApplication.run(AppLoader.class);
    }

    @Bean
    public CertificateFactory certificateFactoryX509()
            throws CertificateException {
        return CertificateFactory.getInstance("X.509");
    }

    @Bean
    public KeyPair serverKeyPair()
            throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        return kpg.generateKeyPair();
    }

}
