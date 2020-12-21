package app;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;

public interface ICertificateRepository {
    void store(String id, MultipartFile mfile)
            throws IOException;

    void store(String id, File file)
            throws IOException;

    File get(String id);

    Certificate getCertificate(String id, String pwd);
}
