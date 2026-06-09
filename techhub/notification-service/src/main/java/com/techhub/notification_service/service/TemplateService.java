package com.techhub.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateEngine templateEngine;

    /**
     * Renders a Thymeleaf HTML template from src/main/resources/templates/email/
     *
     * @param templateName  filename without extension e.g. "welcome"
     * @param variables     map of variables injected into the template
     * @return              rendered HTML string
     */
    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String fullPath = "email/" + templateName;
        log.debug("[TemplateService] Rendering template={}", fullPath);

        return templateEngine.process(fullPath, context);
    }
}