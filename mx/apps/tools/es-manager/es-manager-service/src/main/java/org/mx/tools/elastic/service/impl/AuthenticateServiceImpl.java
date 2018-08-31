package org.mx.tools.elastic.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.jwt.service.JwtService;
import org.mx.tools.elastic.service.AuthenticateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Component
public class AuthenticateServiceImpl implements AuthenticateService {
    private static final Log logger = LogFactory.getLog(AuthenticateServiceImpl.class);
    private static final String default_parent = "user.home";

    private JwtService jwtService;

    @Autowired
    public AuthenticateServiceImpl(JwtService jwtService) {
        super();
        this.jwtService = jwtService;
    }

    private AccountBean authenticate(String code, String password, Properties prps) {
        String checkedCode = prps.getProperty("code");
        String checkedPassword = prps.getProperty("password");
        if (code.equals(checkedCode) && DigestUtils.md5(password).equals(checkedPassword)) {
            String token = jwtService.signToken(code);
            return new AccountBean(code, token);
        } else {
            throw new UserInterfaceRbacErrorException(
                    UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_PASSWORD_NOT_MATCHED
            );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see AuthenticateService#login(String, String)
     */
    @Override
    public AccountBean login(String code, String password) {
        Path path = Paths.get(System.getProperty(default_parent), "/.es-manager/account.properties");
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                Properties prps = new Properties();
                prps.load(fis);
                fis.close();
                return authenticate(code, password, prps);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Load the file[%s] fail.", path.toString()));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.FILE_READ_ERROR
                );
            }
        } else {
            try {
                Properties prps = new Properties();
                prps.load(AuthenticateServiceImpl.class.getResourceAsStream("/account.properties"));
                return authenticate(code, password, prps);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Load the file[classpath:/account.properties] fail.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.FILE_READ_ERROR
                );
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see AuthenticateService#changePassword(String, String, String)
     */
    @Override
    public AccountBean changePassword(String code, String oldPassword, String newPassword) {
        AccountBean accountBean = login(code, oldPassword);
        Path path = Paths.get(System.getProperty(default_parent), "/.es-manager/account.properties");
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Create the directory[%s] fail.", parent.toString()));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL
                );
            }
        }
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            Properties prps = new Properties();
            prps.put("code", code);
            prps.put("password", DigestUtils.md5(newPassword));
            prps.store(new FileOutputStream(path.toFile()), null);
            fos.close();
            return accountBean;
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Write data into file[%s] fail.", path.toString()));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL
            );
        }
    }
}
