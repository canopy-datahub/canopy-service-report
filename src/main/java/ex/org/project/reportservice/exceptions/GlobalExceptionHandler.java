package ex.org.project.reportservice.exceptions;

import ex.org.project.reportservice.auth.UserAuthenticationException;
import ex.org.project.reportservice.auth.UserAuthorizationException;
import ex.org.project.reportservice.auth.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RequestParamException.class)
    public ResponseEntity<ExceptionResponseDTO> requestParamException(RequestParamException e, WebRequest request) {
        log.warn("Invalid parameters provided", e);
        String params = request.getParameterMap().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()) + "; ")
                .reduce("", String::concat);

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "Invalid Parameters Provided",
                status.value(),
                String.format("%s : { %s }", e.getMessage(), params)
        );
        return new ResponseEntity<>(responseDTO, status);
    }

    @ExceptionHandler(UserActivitiesReportException.class)
    public ResponseEntity<ExceptionResponseDTO> reportGenerationException(UserActivitiesReportException e){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "User Activities Exception",
                status.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(responseDTO, status);
    }

    @ExceptionHandler(BadDataException.class)
    public ResponseEntity<ExceptionResponseDTO> handleBadDataException(BadDataException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "Bad Request",
                status.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(responseDTO, status);
    }

    @ExceptionHandler(HarmonizationReportException.class)
    public ResponseEntity<ExceptionResponseDTO> harmonizationReportException(HarmonizationReportException e){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "Harmonization Metrics Report Job Exception",
                status.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(responseDTO, status);
    }

    @ExceptionHandler(UserAuthorizationException.class)
    ResponseEntity<ExceptionResponseDTO> authorizationException(UserAuthorizationException e){
        HttpStatus status = HttpStatus.FORBIDDEN;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "Unauthorized",
                status.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(responseDTO, status);
    }

    @ExceptionHandler(UserAuthenticationException.class)
    ResponseEntity<ExceptionResponseDTO> authenticationException(UserAuthenticationException e){
        //why in the world does the "unauthorized" exception actually mean unauthenticated
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "Unauthenticated",
                status.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(responseDTO, status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ExceptionResponseDTO> userNotFoundException(UserNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "User Not Found",
                status.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(responseDTO, status);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ExceptionResponseDTO> runtimeException(RuntimeException e){
        log.error(e.getMessage(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "Internal Server Error",
                status.value(),
                "An unknown error has occurred. Please contact support if the issue persists."
        );
        return new ResponseEntity<>(responseDTO, status);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ExceptionResponseDTO> exception(Exception e){
        log.error(e.getMessage(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO(
                "Internal Server Error",
                status.value(),
                "An unknown error has occurred. Please contact support if the issue persists."
        );
        return new ResponseEntity<>(responseDTO, status);
    }

}
