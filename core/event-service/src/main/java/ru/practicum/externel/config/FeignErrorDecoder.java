package main.java.ru.practicum.externel.config;

import feign.Response;
import feign.codec.ErrorDecoder;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.exception.NotMeetRulesEditionException;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {
            case 400 -> {
                return new IllegalArgumentException(Exceptions.EXCEPTION_NOT_ILLEGAL_ARGUMENT);
            }
            case 404 -> {
                return new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND);
            }
            case 409 -> {
                return new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_FOUND);
            }
            case 500 -> {
                return new Exception(Exceptions.EXCEPTION_INTERNAL_SERVER);
            }
        }

        return defaultDecoder.decode(methodKey, response);
    }
}
