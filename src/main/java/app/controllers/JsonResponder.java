package app.controllers;

import app.SunSecuredConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.util.encoders.Base64Encoder;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class JsonResponder {

    private final ObjectMapper jsonMapper;

    private final Base64Encoder base64Encoder;

    public JsonResponder(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
        base64Encoder = new Base64Encoder();
    }

    protected void write(HttpServletResponse response,
                       Object value)
            throws IOException {
        jsonMapper.writeValue(response.getWriter(), value);
    }

    protected void write(HttpServletResponse response,
                         X509Certificate cert)
            throws CertificateEncodingException, IOException {
        byte[] data = cert.getEncoded();
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
        base64Encoder.encode(data, 0, data.length, out);
        String base64Cert = new String(out.toByteArray(), StandardCharsets.UTF_8);
        String certStr = SunSecuredConstants.BEGIN_CERT;
        certStr = certStr + base64Cert;
        certStr = certStr + SunSecuredConstants.END_CERT;
        JsonResponse jsonResponse = new JsonResponse(true, certStr);
        write(response, jsonResponse);
    }

}
