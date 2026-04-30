package com.livehealth.shared.base;

import org.jboss.resteasy.reactive.multipart.FileUpload;
import java.io.IOException;
import java.nio.file.Files;

public class QuarkusMultipartFile implements MultipartFile {
    private final FileUpload fileUpload;

    public QuarkusMultipartFile(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    @Override
    public byte[] getBytes() throws IOException {
        if (fileUpload == null || fileUpload.filePath() == null) {
            return new byte[0];
        }
        return Files.readAllBytes(fileUpload.filePath());
    }

    @Override
    public String getName() {
        return fileUpload != null ? fileUpload.name() : "";
    }

    @Override
    public String getOriginalFilename() {
        return fileUpload != null ? fileUpload.fileName() : "";
    }

    @Override
    public String getContentType() {
        return fileUpload != null ? fileUpload.contentType() : "";
    }

    @Override
    public boolean isEmpty() {
        return fileUpload == null || fileUpload.filePath() == null || getSize() == 0;
    }

    @Override
    public long getSize() {
        try {
            return fileUpload != null && fileUpload.filePath() != null ? Files.size(fileUpload.filePath()) : 0;
        } catch (IOException e) {
            return 0;
        }
    }
}
