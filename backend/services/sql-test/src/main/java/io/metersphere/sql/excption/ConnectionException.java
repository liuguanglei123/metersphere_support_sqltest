package io.metersphere.sql.excption;

import lombok.Getter;

@Getter
public class ConnectionException extends BusinessException {


    public ConnectionException() {
        this("connection.error");
    }

    public ConnectionException(String code) {
        this(code, null);
    }

    public ConnectionException(String code, Object[] args) {
        super(code,args);
    }

    public ConnectionException(String code, Object[] args, Throwable throwable) {
        super(code,args, throwable);
    }
}
