package de.starwit.persistence.fileRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class ImageFileRepository {


    public InputStream loadFile(String fileName) {
        String fileLocation = "assets/images/" + fileName;
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream(fileLocation);

        if (fileStream == null) {
            throw new EntityNotFoundException("File could not be found: " + fileLocation);
        }

        return fileStream;
    }

}
