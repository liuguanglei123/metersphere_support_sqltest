package io.metersphere.sql.excption;

import io.metersphere.sql.constant.EasyToolsConstant;
import lombok.Getter;

import java.io.Serial;

/**
 * Parameter exceptions
 *
 * @author Jiaju Zhuang
 */
@Getter
public class ParamBusinessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    public ParamBusinessException() {
        super("common.paramError");
    }

    public ParamBusinessException(String paramString) {
        super("common.paramDetailError", new Object[] {paramString});
    }
}