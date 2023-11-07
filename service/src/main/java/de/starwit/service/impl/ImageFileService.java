package de.starwit.service.impl;

import de.starwit.persistence.repository.ImageFileRepository;
import de.starwit.service.dto.FileDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Image Service class
 */
@Service
public class ImageFileService {

    @Autowired
    ImageFileRepository imageFileRepository;

    public ResponseEntity<Resource> getImageWithFilename(String fileName) {
        FileDto file = this.prepareFileDto(fileName);
        return prepareResourceResponse(file, fileName);
    }

    public ResponseEntity<Resource> prepareResourceResponse(FileDto resource, String fileName) {

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.getFileSize())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource.getByteArrayResource());

    }

    public FileDto prepareFileDto(String fileName) {
        FileDto fileDto = new FileDto();
        try {

            InputStream stream = imageFileRepository.loadFile(fileName);

            fileDto.setFileSize((long) stream.available());

            fileDto.setByteArrayResource(new ByteArrayResource(stream.readAllBytes()));
        } catch (IOException e) {
            throw new EntityNotFoundException("read_error");
        }
        return fileDto;
    }

}
