package app.controllers;

import app.ICertificateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.io.pem.PemObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

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

    @RequestMapping(value = "/view/generate")
    public String viewGenerate() {
        return "prvtkeygen1";
    }

    @RequestMapping(value = "/view/sign")
    public String viewSign() {
        return "signature1";
    }

    @PostMapping(value = "/verify")
    public void verifyCmsSignedData(@RequestBody String cmsSignedData,
                                    HttpServletResponse response) throws IOException {
        if (!valid(cmsSignedData)) {
            super.write(response, new JsonResponse(false, "Wrong data type"));
            return;
        }
        boolean respVal = verifyCmsSignedData(cmsSignedData);
        String msg = respVal ? "PASSED" : "NOT PASSED";
        super.write(response, new JsonResponse(respVal, msg));
    }

    private boolean valid(String cmsSignedData) {
        return cmsSignedData != null
                && cmsSignedData.startsWith("-----BEGIN CMS-----")
                && cmsSignedData.endsWith("-----END CMS-----");
    }

    private boolean verifyCmsSignedData(String cmsSignedData) {
        try {
            PEMParser parser = new PEMParser(new StringReader(cmsSignedData));
            PemObject o = parser.readPemObject();
            CMSSignedData sigData = new CMSSignedData(o.getContent());
            Store store = sigData.getCertificates();
            SignerInformationStore signers = sigData.getSignerInfos();
            System.out.println(store.toString() + "store");
            Collection c = signers.getSigners();
            Iterator it = c.iterator();

            while (it.hasNext()) {
                System.out.println("enter while loop1");
                SignerInformation signer = (SignerInformation) it.next();

                Collection certCollection = store.getMatches(signer.getSID());
                Iterator certIt = certCollection.iterator();
                System.out.println(store.getMatches(null) + "collection of certs");

                while (certIt.hasNext()) {
                    System.out.println("enter while loop2");
                    X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
                    X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);
                    return signer.verify(new JcaSimpleSignerInfoVerifierBuilder().build(cert));
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public void generateCertificate(@RequestBody String certStr,
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
