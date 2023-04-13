package skhumeet.backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResponseDTO {
    private String message;
    private TokenDTO tokens;
    private Object data;

    // All arguments constructor
    public HttpResponseDTO(String message, TokenDTO tokens, Object data) {
        this.message = message;
        this.tokens = tokens;
        this.data = data;
    }

    // Message and Tokens constructor
    public HttpResponseDTO(String message, TokenDTO tokens) {
        this.message = message;
        this.tokens = tokens;
    }

    // Message and Data constructor
    public HttpResponseDTO(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    // Message constructor
    public HttpResponseDTO(String message) {
        this.message = message;
    }
}
