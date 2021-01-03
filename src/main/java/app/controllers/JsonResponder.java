package app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.X509Certificate;

public class JsonResponder {

    private final ObjectMapper jsonMapper;

    public JsonResponder(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    protected void write(HttpServletResponse response,
                       Object value)
            throws IOException {
        jsonMapper.writeValue(response.getWriter(), value);
    }

    protected void write(HttpServletResponse response,
                         X509Certificate cert)
            throws IOException {
        final StringWriter writer = new StringWriter();
        final JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(cert);
        pemWriter.flush();
        pemWriter.close();
        String res = writer.toString();
        JsonResponse jsonResponse = new JsonResponse(true, res);
        write(response, jsonResponse);
    }

}
