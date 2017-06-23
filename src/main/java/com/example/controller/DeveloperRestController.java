package com.example.controller;

import com.example.model.Developer;
import com.example.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collection;

/**
 * Created on 22.06.2017
 *
 * @author Roman Hayda
 */
@RestController
@RequestMapping("/api/devs")
public class DeveloperRestController {

    @Autowired
    private DeveloperService devService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Collection<Developer>> getDevs() {
        Collection<Developer> devs = this.devService.findAllDevs();
        if (devs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(devs, HttpStatus.OK);
    }

    @RequestMapping(value = "/{devId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Developer> getDev(@PathVariable("devId") int devId) {
        Developer dev = this.devService.findDevById(devId);
        if (dev == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dev, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Developer> addDev(@RequestBody @Valid Developer dev, BindingResult bindingResult,
                                          UriComponentsBuilder ucBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (dev == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        this.devService.saveDev(dev);
        headers.setLocation(ucBuilder.path("/api/devs/{id}").buildAndExpand(dev.getId()).toUri());
        return new ResponseEntity<>(dev, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{devId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Developer> updateDev(@PathVariable("devId") int devId, @RequestBody @Valid Developer dev,
                                             BindingResult bindingResult, UriComponentsBuilder ucBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (dev == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        Developer currentDev = this.devService.findDevById(devId);
        if (currentDev == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentDev.setFirstName(dev.getFirstName());
        currentDev.setLastName(dev.getLastName());
        currentDev.setSpecialty(dev.getSpecialty());
        currentDev.setExperience(dev.getExperience());
        currentDev.setSalary(dev.getSalary());
        this.devService.saveDev(currentDev);
        return new ResponseEntity<>(currentDev, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{devId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional
    public ResponseEntity<Void> deleteOwner(@PathVariable("devId") int devId) {
        Developer dev = this.devService.findDevById(devId);
        if (dev == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.devService.deleteDev(dev);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}