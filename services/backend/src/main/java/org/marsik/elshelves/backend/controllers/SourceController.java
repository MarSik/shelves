package org.marsik.elshelves.backend.controllers;

import net.sf.image4j.codec.ico.ICODecoder;
import org.apache.http.HttpStatus;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.SourceService;
import org.marsik.elshelves.backend.services.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sources")
public class SourceController extends AbstractRestController<Source, SourceApiModel, SourceService> {
	final SourceService sourceService;

	@Autowired
	StorageManager storageManager;

	@Autowired
	public SourceController(SourceService service) {
		super(SourceApiModel.class, service);
		this.sourceService = service;
	}

	@RequestMapping(value = "/{uuid}/icon")
	public void getIcon(@CurrentUser User currentUser,
						@PathVariable("uuid") UUID uuid,
						@RequestParam(value = "size", required = false) Integer size,
						HttpServletResponse response) throws PermissionDenied, EntityNotFound, IOException {
		SourceApiModel s = getService().get(uuid, currentUser);

		if (s.isHasIcon()) {
			BufferedImage imgToSend = null;
			List<BufferedImage> images = ICODecoder.read(storageManager.get(uuid));

			if (size == null) {
				size = 32;
			}

			for (BufferedImage img: images) {
				if (imgToSend == null) {
					imgToSend = img;
				} else if (img.getHeight() == size) {
					imgToSend = img;
				} else if (img.getHeight() > size) {
					imgToSend = img;
				}
			}

			if (imgToSend == null) {
				response.setStatus(HttpStatus.SC_NOT_FOUND);
				return;
			}


			// Resize to the proper size
			if (imgToSend.getHeight() != size) {
				BufferedImage dest = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = dest.createGraphics();
				AffineTransform at = AffineTransform.getScaleInstance(size.floatValue() / imgToSend.getWidth(),
						size.floatValue() / imgToSend.getHeight());
				g.drawRenderedImage(imgToSend, at);
				imgToSend = dest;
			}

			response.setContentType("image/x-icon");
			ImageIO.write(imgToSend, "png", response.getOutputStream());
			response.setStatus(HttpStatus.SC_OK);
		} else {
			response.setStatus(HttpStatus.SC_NOT_FOUND);
		}
	}
}
