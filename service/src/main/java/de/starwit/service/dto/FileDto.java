package de.starwit.service.dto;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public class FileDto {

    private MultipartFile multipartFile;
    private String name;
    private ByteArrayResource byteArrayResource;
    private Long fileSize;

    public FileDto(MultipartFile multipartFile, String name) {
        this.multipartFile = multipartFile;
        this.name = name;
    }

    public FileDto() {
    }

    public ByteArrayResource getByteArrayResource() {
        return byteArrayResource;
    }

    public void setByteArrayResource(ByteArrayResource byteArrayResource) {
        this.byteArrayResource = byteArrayResource;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}