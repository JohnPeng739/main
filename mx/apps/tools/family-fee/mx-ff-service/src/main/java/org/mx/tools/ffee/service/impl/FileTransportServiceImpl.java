package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.FileUtils;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.service.FileTransportService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component("fileTransportService")
public class FileTransportServiceImpl implements FileTransportService {
    private static final Log logger = LogFactory.getLog(FileTransportServiceImpl.class);

    @Override
    public File downloadFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            if (logger.isErrorEnabled()) {
                logger.error("The file path is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The file[%s] not found.", path.toString()));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.FILE_NOT_EXISTED
            );
        }
        return path.toFile();
    }

    @Override
    public String uploadFile(String filePath, InputStream in) {
        if (StringUtils.isBlank(filePath)) {
            if (logger.isErrorEnabled()) {
                logger.error("The file path is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        if (in == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The input stream is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            if (Files.isRegularFile(path)) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The file[%s] has existed.", path.toString()));
                }
            } else if (Files.isDirectory(path)) {
                try {
                    FileUtils.deleteFile(path);
                } catch (IOException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Delete the path[%s] fail.", path.toString()));
                    }
                    throw new UserInterfaceFfeeErrorException(
                            UserInterfaceFfeeErrorException.FfeeErrors.PATH_DELETE_FAIL
                    );
                }
            }
        }
        Path parentPath = path.getParent();
        if (!Files.exists(parentPath)) {
            try {
                Files.createDirectories(parentPath);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Create the directory[%s] fail.", parentPath.toString()));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.PATH_CREATE_FAIL
                );
            }
        }
        try {
            FileUtils.saveFile(path.toString(), in);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Save any data into file[%s] from input stream fail.", path.toString()));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.WRITE_FILE_FAIL
            );
        }
        return path.toString();
    }
}
