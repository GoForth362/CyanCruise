package v620.cc001.cloud01.app01.mservice.file.impl;

import v620.cc001.cloud01.app01.mservice.file.*;
import kd.bos.fileservice.FileItem;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

class DefaultBosFileServiceClient implements BosFileServiceClient {

    public boolean available() {
        return hasText(System.getProperty("attachmentServer.url")) && fileService() != null;
    }

    public String upload(String objectKey, String originalFilename, byte[] bytes) {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        FileItem item = new FileItem(originalFilename, objectKey, input);
        item.setCreateNewFileWhenExists(true);
        item.setVerified(true);
        return fileService().upload(item);
    }

    public Map<String, Object> preview(String originalFilename, String objectKey) {
        return fileService().preview(originalFilename, objectKey, null);
    }

    public byte[] download(String objectKey) {
        InputStream input = fileService().getInputStream(objectKey);
        if (input == null) {
            return null;
        }
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) >= 0) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        } catch (Exception ex) {
            throw new FileAdapterUnavailableException("FAILED", ex.toString());
        } finally {
            try {
                input.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void delete(String objectKey) {
        fileService().delete(objectKey);
    }

    public String httpUrlPrefix() {
        String prefix = null;
        try {
            prefix = fileService().getHttpUrlPrefix();
        } catch (Exception ignored) {
        }
        if (!hasText(prefix)) {
            prefix = System.getProperty("attachmentServer.url");
        }
        if (!hasText(prefix)) {
            prefix = System.getProperty("attachmentServer.inner.url");
        }
        return prefix;
    }

    private FileService fileService() {
        return FileServiceFactory.getAttachmentFileService();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
