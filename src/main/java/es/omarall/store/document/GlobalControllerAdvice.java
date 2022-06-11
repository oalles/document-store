package es.omarall.store.document;

import es.omarall.store.document.dto.ErrorInfo;
import es.omarall.store.document.exceptions.RequestValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> handleMaxUploadSizeExceededException(HttpServletRequest req, MaxUploadSizeExceededException ex) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setMessage("Maximum upload size exceeded");

        if (!StringUtils.hasText(errorInfo.getPath())) {
            errorInfo.setPath(req.getRequestURL().toString());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RequestValidationException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> handleRequestValidationException(HttpServletRequest req, RequestValidationException ex) {
        ErrorInfo errorInfo = null;
        if (ex.getErrorInfo() != null) {
            errorInfo = ex.getErrorInfo();
        } else {
            errorInfo = new ErrorInfo();
            errorInfo.setMessage(ex.getMessage());
        }
        if(!StringUtils.hasText(errorInfo.getPath())) {
            errorInfo.setPath(req.getRequestURL().toString());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorInfo> handleNoSuchElementException(HttpServletRequest req, NoSuchElementException e) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .code("NoSuchElement")
                .path(req.getRequestURL().toString())
                .message("Requested resource doesnot exist")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorInfo);
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorInfo> handleThrowable(HttpServletRequest req, Throwable t) {
        log.error("API temporarily unavailable due to an unexpected error", t);

        ErrorInfo error = ErrorInfo.builder()
                .code("ServerUnavailable")
                .path(req.getRequestURL().toString())
                .message("API temporarily unavailable due to an unexpected error")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
