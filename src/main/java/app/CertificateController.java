package app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/certificate")
public class CertificateController {

    @RequestMapping("/save")
    public void saveCertificate(@RequestParam("certicate") MultipartFile file,
                                @RequestParam("pwd") String pwd) {

    }
}
