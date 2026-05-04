package me.delous.otp.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocsController {
    private static final String SPEC_PLACEHOLDER = "{{OPENAPI_SPEC}}";
    private final String docsPage;

    public DocsController() {
        this.docsPage = loadResource("docs/docs.html")
                .replace(SPEC_PLACEHOLDER, loadResource("docs/openapi.json"));
    }

    @GetMapping(value = "/docs", produces = MediaType.TEXT_HTML_VALUE)
    public String docs() {
        return docsPage;
    }

    private static String loadResource(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load docs resource: " + path, ex);
        }
    }
}
