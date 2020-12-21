package app;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public interface ICertificateService {

    /**
     * @param csr Certificate Signature Request.
     * @return Signed Certificate in case of VALID CSR
     * otherwise throws :
     * @throws CertificateEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchProviderException
     * @throws SignatureException
     */
    X509Certificate generate(X509Certificate csr)
            throws CertificateEncodingException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchProviderException,
            SignatureException;

}
