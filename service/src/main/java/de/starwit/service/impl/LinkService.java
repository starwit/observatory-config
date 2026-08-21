package de.starwit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.LinkEntity;
import de.starwit.persistence.repository.LinkRepository;

/**
 * LinkService class
 */
@Service
public class LinkService implements ServiceInterface<LinkEntity, LinkRepository> {

    @Autowired
    private LinkRepository linkRepository;

    @Override
    public LinkRepository getRepository() {
        return linkRepository;
    }
}
