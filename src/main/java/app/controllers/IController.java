package app.controllers;

import app.ICertificateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Controller
public class IController
        extends JsonResponder {

    private final CertificateFactory certificateFactory;

    private final ICertificateService certificateGenerator;

    public IController(CertificateFactory certificateFactory,
                       ObjectMapper jsonMapper,
                       ICertificateService certificateGenerator) {
        super(jsonMapper);
        this.certificateFactory = certificateFactory;
        this.certificateGenerator = certificateGenerator;
    }

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public void parse(@RequestBody String certStr,
                      HttpServletResponse response)
            throws CertificateException, IOException {
        InputStream is = IOUtils.toInputStream(certStr, Charset.defaultCharset());
        Certificate cert = certificateFactory.generateCertificate(is);
        if (!(cert instanceof X509Certificate)) {
            super.write(response, new JsonResponse(false,
                    "Wrong certificate. Please upload X509 Certificate."));
            return;
        }
        X509Certificate csr = (X509Certificate) cert;
        try {
            X509Certificate signedCertificate =
                    certificateGenerator.generate(csr);
            super.write(response, signedCertificate);
        } catch (NoSuchAlgorithmException
                | SignatureException
                | InvalidKeyException e) {
            super.write(response, new JsonResponse(false,
                    "Not valid Certificate Signature Request. Please try again."));
        } catch (NoSuchProviderException e) {
            super.write(response, new JsonResponse(false,
                    "Internal Server Error. Please try later or contact us"));
        }

    }

}
