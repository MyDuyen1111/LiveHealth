package com.livehealth.shared.base;

import java.io.IOException;

public interface MultipartFile {
    byte[] getBytes() throws IOException;
    String getName();
    String getOriginalFilename();
    String getContentType();
    boolean isEmpty();
    long getSize();
}
