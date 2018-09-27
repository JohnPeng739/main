package org.mx.tools.ffee.service;

import java.io.File;
import java.io.InputStream;

public interface FileTransportService {
    File downloadFile(String filePath);

    String uploadFile(String filePath, InputStream in);
}
