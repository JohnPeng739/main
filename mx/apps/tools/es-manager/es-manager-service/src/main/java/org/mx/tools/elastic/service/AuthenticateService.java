package org.mx.tools.elastic.service;

/**
 * 描述：
 *
 * @author : john date : 2018/8/31 下午9:10
 */
public interface AuthenticateService {
    AccountBean login(String code, String password);

    AccountBean changePassword(String code, String oldPassword, String newPassword);

    class AccountBean {
        private String code, token;
        private long loginTime;

        public AccountBean(String code, String token) {
            super();
            this.code = code;
            this.token = token;
            this.loginTime = System.currentTimeMillis();
        }

        public String getCode() {
            return code;
        }

        public String getToken() {
            return token;
        }

        public long getLoginTime() {
            return loginTime;
        }
    }
}
