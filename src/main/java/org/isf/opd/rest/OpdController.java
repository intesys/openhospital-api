package org.isf.opd.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@RestController
@Api(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class OpdController {

    private static final String DEFAULT_PAGE_SIZE = "80";

    @Autowired
    protected OpdBrowserManager opdManager;

    private final Logger logger = LoggerFactory.getLogger(OpdController.class);

    public OpdController(OpdBrowserManager opdBrowserManager) {
        this.opdManager = opdBrowserManager;
    }

    @PostMapping(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Integer> newOpd(@RequestBody OpdDTO newOpd) throws OHServiceException {
        logger.info("Create opd for patient " + newOpd.getPatient().getCode());
        boolean isCreated = opdManager.newOpd(getObjectMapper().map(newOpd, Opd.class));
        Opd lastOpd = opdManager.getLastOpd(newOpd.getPatient().getCode());
        if (!isCreated || lastOpd == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Opd is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(lastOpd.getCode());
    }

}
