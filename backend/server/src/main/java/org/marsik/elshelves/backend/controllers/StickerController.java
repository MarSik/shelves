package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.StickerSettings;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.StickerService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/v1/stickers")
public class StickerController {
	@Autowired
	StickerService stickerService;

	@Autowired
	OwnedEntityRepository ownedEntityRepository;

	@Autowired
	UuidGenerator uuidGenerator;

	private static class RandomSticker implements StickerCapable {
		private final UUID id;

		public RandomSticker(UUID id) {
			this.id = id;
		}

		@Override
		public UUID getId() {
			return id;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getSummary() {
			return null;
		}

		@Override
		public String getBaseUrl() {
			return "id";
		}
	}

	@Transactional(readOnly = true)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(produces = "application/pdf")
	public void generateStickers(HttpServletResponse response,
								 @CurrentUser User currentUser,
                                 @Valid StickerSettings stickerSettings,
								 @RequestParam("uuids[]") UUID[] uuids) throws IOException, PermissionDenied, EntityNotFound, InvalidRequest {
        /*
		StickerSettings stickerSettings = new StickerSettings()
				.setPage(StickerSettings.PageSize.A4)
				.setStickerHeightMm(37f)
				.setStickerWidthMm(70f)
				.setStickerVerticalCount(8)
				.setStickerHorizontalCount(3);
		*/

		List<StickerCapable> objects = new ArrayList<>();
		for (UUID uuid: uuids) {
			if (uuid.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
				objects.add(new RandomSticker(uuidGenerator.generate()));
				continue;
			}

			OwnedEntity e = ownedEntityRepository.findById(uuid);
			if (e == null) {
				throw new EntityNotFound();
			}

			if (!e.getOwner().equals(currentUser)) {
				throw new PermissionDenied();
			}

			if (!StickerCapable.class.isAssignableFrom(e.getClass())) {
				throw new InvalidRequest();
			}

			objects.add((StickerCapable) e);
		}

		StickerService.Result pdf = stickerService.generateStickers(stickerSettings, objects);

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=stickers.pdf");

		pdf.save(response.getOutputStream());
		pdf.close();
		response.flushBuffer();
	}

}
