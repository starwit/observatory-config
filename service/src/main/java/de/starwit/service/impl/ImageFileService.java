package de.starwit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.repository.ImageFileRepository;
import de.starwit.persistence.repository.ImageRepository;
import de.starwit.service.dto.FileDto;

/**
 * Image Service class
 */
@Service
public class ImageFileService {

    @Autowired
    ImageFileRepository imageFileRepository;

    @Autowired
    ImageRepository imageRepository;

    public ResponseEntity<Resource> getImageWithId(Long id) {
        ImageEntity image = imageRepository.getReferenceById(id);
        FileDto file = this.prepareFileDto(image);
        return prepareResourceResponse(file, image.getName());
    }

    public ResponseEntity<Resource> prepareResourceResponse(FileDto resource, String name) {

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.getFileSize())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource.getByteArrayResource());

    }

    public FileDto prepareFileDto(ImageEntity image) {
        FileDto fileDto = new FileDto();
        fileDto.setByteArrayResource(new ByteArrayResource(image.getData()));
        return fileDto;
    }

}
