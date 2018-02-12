package org.mx.comps.jwt;

import com.auth0.jwt.interfaces.Claim;

import java.util.Map;
import java.util.function.Predicate;

/**
 * JWT自定义验证函数构建工具
 *
 * @author : john.peng created on date : 2018/2/12
 */
public class JwtVerifyFuncBuilder {
    public static Predicate<Map<String, Claim>> instance() {
        // TODO
        return null;
    }
}
