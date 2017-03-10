package org.marsik.elshelves.backend.controllers;

import com.fasterxml.jackson.databind.ObjectReader;
import org.marsik.elshelves.api.entities.BackupApiModel;
import org.marsik.elshelves.api.entities.RestoreApiModel;
import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToEntityConversionService;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/v1/backup")
public class BackupController {
	@Autowired
	BackupService backupService;

    @Autowired
    MappingJackson2HttpMessageConverter converter;

	/**
	 * Test with
	 * curl -X PUT
	 *      -H "Authorization: Bearer AUTH-TOKEN"
	 *      -H "Content-Type: application/json"
	 *      -F "@export.json"
	 *      http://server/backup
	 * @param backup
	 * @param currentUser
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
    @CircuitBreaker(timeoutMs = 600000)
	public void restoreBackup(@RequestBody RestoreApiModel backup,
							  @CurrentUser User currentUser) {
		backupService.restoreFromBackup(backup, currentUser);
	}

    @RequestMapping
    @Transactional(readOnly = true)
    @CircuitBreaker(timeoutMs = 600000)
    public BackupApiModel getBackup(@CurrentUser User currentUser,
                                    HttpServletResponse response) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        String name = currentUser.getEmail() + "-" + df.format(new Date()) + ".json";
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=" + name);
        return backupService.doBackup(currentUser);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    @CircuitBreaker(timeoutMs = 600000)
    public void upload(@CurrentUser User currentUser,
            @RequestParam("files[]") MultipartFile[] files) throws IOException {
        for (MultipartFile file: files) {
            ObjectReader reader = converter.getObjectMapper().reader().forType(RestoreApiModel.class);
            RestoreApiModel backup = reader.readValue(file.getInputStream());
            restoreBackup(backup, currentUser);
        }
    }
}
