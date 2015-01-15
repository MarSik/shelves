package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.BackupApiModel;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/backup")
public class BackupController {
	@Autowired
	BackupService backupService;

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
	public void restoreBackup(@RequestBody BackupApiModel backup,
							  @CurrentUser User currentUser) {
		backupService.restoreFromBackup(backup, currentUser);
	}

    @RequestMapping
    @Transactional(readOnly = true)
    public BackupApiModel getBackup(@CurrentUser User currentUser,
                                    HttpServletResponse response) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        String name = currentUser.getEmail() + "-" + df.format(new Date()) + ".json";
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=" + name);
        return backupService.doBackup(currentUser);
    }
}
