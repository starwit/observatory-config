package de.starwit.rest.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.starwit.persistence.entity.LinkEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import de.starwit.service.dto.LinkDto;
import de.starwit.service.impl.LinkService;
import de.starwit.service.mapper.LinkMapper;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Link RestController
 */
@RestController
@RequestMapping(path = "${rest.base-path}/link")
public class LinkController {

    static final Logger LOG = LoggerFactory.getLogger(LinkController.class);

    @Autowired
    private LinkService linkService;

    private LinkMapper linkMapper = new LinkMapper();

    @Operation(summary = "Get all links")
    @GetMapping
    public List<LinkEntity> findAll() {
        return this.linkService.findAll();
    }

    @Operation(summary = "Get link with id")
    @GetMapping(value = "/{id}")
    public LinkEntity findById(@PathVariable("id") Long id) {
        return linkService.findById(id);
    }

    @Operation(summary = "Create new link")
    @PostMapping
    public ResponseEntity<LinkDto> create(@Valid @RequestBody LinkDto dto) {
        LinkEntity link = linkMapper.convertToEntity(dto);
        LinkEntity created = linkService.saveOrUpdate(link);
        return new ResponseEntity<>(linkMapper.convertToDto(created), HttpStatus.CREATED);
    }

    @Operation(summary = "Update link")
    @PutMapping(value = "/{id}")
    public ResponseEntity<LinkDto> update(@PathVariable("id") Long id, @Valid @RequestBody LinkDto dto) {
        LinkEntity link = linkService.findById(id);
        link = linkMapper.convertToEntity(dto, link);
        LinkEntity updated = linkService.saveOrUpdate(link);
        return new ResponseEntity<>(linkMapper.convertToDto(updated), HttpStatus.OK);
    }

    @Operation(summary = "Delete link")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws NotificationException {
        linkService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<NotificationDto> handleEntityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(new NotificationDto("error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<NotificationDto> handleNotification(NotificationException ex) {
        return new ResponseEntity<>(new NotificationDto("error", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
