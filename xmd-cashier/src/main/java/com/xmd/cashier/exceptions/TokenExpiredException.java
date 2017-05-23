package com.xmd.cashier.exceptions;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;

/**
 * Created by heyangya on 16-8-24.
 */

public class TokenExpiredException extends ServerException {
    public TokenExpiredException() {
        super(Utils.getStringFromResource(R.string.token_expired), 0);
    }
}
