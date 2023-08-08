package de.starwit.rest.controller;

import de.starwit.service.impl.ImageFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Image RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/imageFile")
public class ImageFileController {

    static final Logger LOG = LoggerFactory.getLogger(ImageFileController.class);

    @Autowired
    private ImageFileService imageFileService;

    @GetMapping(value = "/name/{imageFilename}")
    public ResponseEntity<Resource> getImageWithFileName(@PathVariable("imageFilename") String imageFilename) {
        return this.imageFileService.getImageWithFilename(imageFilename);
    }
}
