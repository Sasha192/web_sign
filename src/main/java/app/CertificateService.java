package app;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.springframework.stereotype.Service;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

@Service
public class CertificateService
        implements ICertificateService {

    private final ICertificateRepository certificateRepository;

    private final KeyPair keys;

    private static Date YESTERDAY;

    private static Date YEAR_LATER;

    public CertificateService(ICertificateRepository certificateRepository,
                              KeyPair keys) {
        this.certificateRepository = certificateRepository;
        this.keys = keys;
        setDates();
    }

    private void setDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, false);
        YESTERDAY = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        YEAR_LATER = calendar.getTime();
    }

    @Override
    public X509Certificate generate(X509Certificate csr)
            throws CertificateEncodingException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchProviderException,
            SignatureException {
        /*SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(csr.getPublicKey().getEncoded());
        org.bouncycastle.cert.X509v3CertificateBuilder builder =
                new X509v3CertificateBuilder(new X509Name("dc=localhost.com"), SerialNumberGenerator.generate(),
                        YESTERDAY, YEAR_LATER, csr.getSubjectX500Principal(), publicKeyInfo);*/
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        certGen.setSerialNumber(SerialNumberGenerator.generate());
        certGen.setSubjectDN(csr.getSubjectX500Principal());
        certGen.setIssuerDN(new X509Name("dc=localhost.com"));
        certGen.setNotBefore(YESTERDAY);
        certGen.setNotAfter(YEAR_LATER);
        certGen.setPublicKey(csr.getPublicKey());
        certGen.setSignatureAlgorithm(csr.getSigAlgName());
        return signCertificate(certGen);
    }

    private X509Certificate signCertificate(X509V3CertificateGenerator certGen)
            throws NoSuchAlgorithmException,
            CertificateEncodingException,
            InvalidKeyException, SignatureException {
        return certGen.generate(keys.getPrivate());
    }
}
