package com.smsrz.url_shortener;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
public record ApplicationProperties(
        @NotBlank
        @DefaultValue("http://locahost:8080")
        String baseurl,
        @DefaultValue("30")
        @Min(1)
        @Max(30)
        int defaultExpiryDays,
        @DefaultValue("true")
        Boolean validateOriginalUrl
) {
}
