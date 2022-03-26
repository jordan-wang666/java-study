package com.action.licensing.controller;

import com.action.licensing.config.ServiceConfig;
import com.action.licensing.model.License;
import com.action.licensing.services.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "v1/organizations/{organizationId}/licenses")
public class LicenseServiceController {
    @Autowired
    private LicenseService licenseService;

    @Autowired
    private ServiceConfig serviceConfig;

    @GetMapping(value = "/")
    public List<License> getLicenses(@PathVariable("organizationId") String organizationId) {

        return licenseService.getLicensesByOrg(organizationId);
    }

    @GetMapping(value = "/{licenseId}")
    public License getLicenses(@PathVariable("organizationId") String organizationId,
                               @PathVariable("licenseId") String licenseId) {

        return licenseService.getLicense(organizationId, licenseId, "");
    }

    @GetMapping(value = "/{licenseId}/{clientType}")
    public License getLicensesWithClient(@PathVariable("organizationId") String organizationId,
                                         @PathVariable("licenseId") String licenseId,
                                         @PathVariable("clientType") String clientType) {

        return licenseService.getLicense(organizationId, licenseId, clientType);
    }

//    @RequestMapping(value = "{licenseId}", method = RequestMethod.PUT)
//    public void updateLicenses(@PathVariable("licenseId") String licenseId, @RequestBody License license) {
//        licenseService.updateLicense(license);
//    }
//
//    @RequestMapping(value = "/", method = RequestMethod.POST)
//    public void saveLicenses(@RequestBody License license) {
//        licenseService.saveLicense(license);
//    }
//
//    @RequestMapping(value = "{licenseId}", method = RequestMethod.DELETE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteLicenses(@PathVariable("licenseId") String licenseId, @RequestBody License license) {
//        licenseService.deleteLicense(license);
//    }
}
