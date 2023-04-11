package pl.owolny.apigateway.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes {

    @Bean
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes, ServerCodecConfigurer serverCodecConfigurer) {
        return (exchange, ex) -> {
            HttpStatus httpStatus;
            String message;

            if (ex instanceof NotFoundException) {
                httpStatus = HttpStatus.NOT_FOUND;
                message = "Resource not found";
            } else if (ex instanceof ResponseStatusException responseStatusException) {
                httpStatus = (HttpStatus) responseStatusException.getStatusCode();
                message = responseStatusException.getMessage();
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "Internal server error";
            }

            ServerRequest serverRequest = ServerRequest.create(exchange, serverCodecConfigurer.getReaders());

            Map<String, Object> errorAttributesMap = errorAttributes.getErrorAttributes(
                    serverRequest, ErrorAttributeOptions.defaults());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", httpStatus.value());
            errorResponse.put("error", message);
            errorResponse.put("message", errorAttributesMap.get("message"));
            errorResponse.put("path", errorAttributesMap.get("path"));

            return ServerResponse.status(httpStatus)
                    .body(BodyInserters.fromValue(errorResponse)).then();
        };
    }
}