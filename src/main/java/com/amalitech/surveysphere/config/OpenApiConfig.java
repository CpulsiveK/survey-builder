package com.amalitech.surveysphere.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
            contact = @Contact(
                    name = "Survey Builder Team",
                    email = "surveybuilder541@gmail.com",
                    url = "https://survey-sphere.amalitech-dev.net"),
            description = "OpenApi documentation for Survey-Builder",
            title = "Survey Builder",
            version = "1.0",
            termsOfService = "Terms of Service"),
    servers = {
      @Server(description = "Local ENV", url = "http://localhost:3004"),
      @Server(description = "PRE_PROD ENV", url = "https://survey-sphere.amalitech-dev.net")
    })
@SecurityScheme(
    name = "emailPasswordAuth",
    description = "Email and Password Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "basic",
    in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {}
