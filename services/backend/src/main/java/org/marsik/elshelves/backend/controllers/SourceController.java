package org.marsik.elshelves.backend.controllers;

import com.docuverse.identicon.NineBlockIdenticonRenderer;
import net.sf.image4j.codec.ico.ICODecoder;
import org.apache.http.HttpStatus;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToSource;
import org.marsik.elshelves.backend.entities.converters.SourceToEmber;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.SourceService;
import org.marsik.elshelves.backend.services.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/sources")
public class SourceController extends AbstractRestController<Source, SourceApiModel, SourceService> {
	final SourceService sourceService;

	@Autowired
	StorageManager storageManager;

	@Autowired
	public SourceController(SourceService service,
							SourceToEmber dbToRest,
							EmberToSource restToDb) {
		super(SourceApiModel.class, service, dbToRest, restToDb);
		this.sourceService = service;
	}

	@RequestMapping(value = "/{uuid}/icon")
    @Transactional(readOnly = true)
	public void getIcon(@CurrentUser User currentUser,
						@PathVariable("uuid") UUID uuid,
						@RequestParam(value = "size", required = false) Integer size,
						HttpServletResponse response) throws PermissionDenied, EntityNotFound, IOException {
		Source s = getService().get(uuid, currentUser);

        BufferedImage imgToSend = null;

        if (size == null
                || size > 256
                || size < 16) {
            size = 32;
        }

		if (s.isHasIcon()) {
			List<BufferedImage> images = ICODecoder.read(storageManager.get(uuid));

			for (BufferedImage img: images) {
				if (imgToSend == null) {
					imgToSend = img;
				} else if (img.getHeight() == size) {
					imgToSend = img;
				} else if (img.getHeight() > size) {
					imgToSend = img;
				}
			}

			// Resize to the proper size
			if (imgToSend != null && imgToSend.getHeight() != size) {
				BufferedImage dest = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = dest.createGraphics();
				AffineTransform at = AffineTransform.getScaleInstance(size.floatValue() / imgToSend.getWidth(),
						size.floatValue() / imgToSend.getHeight());
				g.drawRenderedImage(imgToSend, at);
				imgToSend = dest;
			}
		}

        if (imgToSend == null) {
            imgToSend = new NineBlockIdenticonRenderer().render(uuid.hashCode(), size);
        }

        response.setContentType("image/x-icon");
        ImageIO.write(imgToSend, "png", response.getOutputStream());
        response.setStatus(HttpStatus.SC_OK);
	}
}
