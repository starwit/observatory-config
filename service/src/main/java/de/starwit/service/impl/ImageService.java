package de.starwit.service.impl;

import java.util.List;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.repository.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.util.Set;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.PolygonRepository;

/**
 *
 * Image Service class
 *
 */
@Service
public class ImageService implements ServiceInterface<ImageEntity, ImageRepository> {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PolygonRepository polygonRepository;

    @Autowired
    private PolygonService polygonService;

    @Override
    public ImageRepository getRepository() {
        return imageRepository;
    }

    public List<ImageEntity> findAllWithoutParkingConfig() {
        return imageRepository.findAllWithoutParkingConfig();
    }

    public List<ImageEntity> findAllWithoutOtherParkingConfig(Long id) {
        return imageRepository.findAllWithoutOtherParkingConfig(id);
    }

    @Override
    public ImageEntity saveOrUpdate(ImageEntity image) {

        Set<PolygonEntity> polygonToSave = image.getPolygon();

        try{
            ImageEntity origImg = this.findById(image.getId());
            this.polygonService.deletePolygonsByImage(origImg);
        } catch (InvalidDataAccessApiUsageException ignored){
        //} catch (EntityNotFoundException ignored){
            System.out.println("blubb");
        }

        if (polygonToSave != null && !polygonToSave.isEmpty()) {
            for (PolygonEntity polygon : polygonToSave) {
                PolygonEntity newPolygon = polygonService.saveOrUpdate(polygon);
                polygonService.assignPolygonToImage(newPolygon, image);
            }
        }

        image = this.getRepository().save(image);
        this.getRepository().flush();

        return this.getRepository().getReferenceById(image.getId());
    }
}
