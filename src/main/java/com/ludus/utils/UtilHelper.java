package com.ludus.utils;

import org.springframework.stereotype.Component;
import com.ludus.dtos.responses.InfoDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Value;

@Component
public class UtilHelper {
    @Value("${api.base_url}")
    private String baseUrl;

    public InfoDtoResponse buildPageableInfoDto(Page<?> responsePage, String endpoint) {
        String normalizedEndpoint = endpoint.startsWith("/") ? endpoint.substring(1) : endpoint;

        return new InfoDtoResponse(
            responsePage.getTotalElements(),
            responsePage.getTotalPages(),
            responsePage.hasNext() ? baseUrl + "/" + normalizedEndpoint + "?page=" + (responsePage.getNumber() + 1) : null,
            responsePage.hasPrevious() ? baseUrl + "/" + normalizedEndpoint + "?page=" + (responsePage.getNumber() - 1) : null
        );
    }

    public String getEnumValues(Class<?> enumClass) {
        if (enumClass.isEnum()) {
            StringBuilder sb = new StringBuilder();
            for (Object constant : enumClass.getEnumConstants()) {
                sb.append(((Enum<?>) constant).name()).append(", ");
            }
            return !sb.isEmpty() ? sb.substring(0, sb.length() - 2) : "";
        } else {
            return "";
        }
    }
}
