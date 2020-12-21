package app;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CertificateRepository implements ICertificateRepository {

    private final String certificatesStoragePath;
    private Map<String, File> certificates;

    public CertificateRepository(@Value("'${cert_storage_path}'")
                                         String certificatesStoragePath) {
        this.certificatesStoragePath = certificatesStoragePath;
        this.certificates = new ConcurrentHashMap<>();
    }

    @Override
    public void store(String id, MultipartFile mfile)
            throws IOException {
        String name = String.format("%d", System.currentTimeMillis());
        String ext = FilenameUtils.getExtension(
                mfile.getOriginalFilename()
        );
        ext = '.' + ext;
        String fullPath = certificatesStoragePath + ('/' + name + ext);
        File file = new File(fullPath);
        if (!file.exists() && file.createNewFile()) {
            mfile.transferTo(file);
            certificates.put(id, file);
        } else {
            throw new IOException("Please try again!");
        }
    }

    @Override
    public void store(String id, File file)
            throws IOException {
        certificates.put(id, file);
    }

    @Override
    public File get(String id) {
        return certificates.get(id);
    }

    @Override
    public @NonNull Certificate getCertificate(String id, String pwd) {
        return null;
    }



}
