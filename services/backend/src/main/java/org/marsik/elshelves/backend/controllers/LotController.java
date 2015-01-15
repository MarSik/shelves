package org.marsik.elshelves.backend.controllers;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gnu.trove.set.hash.THashSet;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.LotService;
import org.marsik.elshelves.backend.services.PurchaseService;
import org.marsik.elshelves.backend.services.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/lots")
public class LotController {
	@Autowired
	LotService lotService;

	@Autowired
	PurchaseService purchaseService;

	@Autowired
	TypeService typeService;

	@ResponseBody
	@RequestMapping
	@Transactional
	public EmberModel getAll(@CurrentUser User currentUser,
							 @RequestParam(value = "ids[]", required = false) UUID[] ids) throws PermissionDenied, EntityNotFound {
		Collection<LotApiModel> allItems;

		if (ids == null) {
			allItems = lotService.getAll(currentUser);
		} else {
			allItems = new THashSet<>();
			for (UUID id: ids) {
				allItems.add(lotService.get(id, currentUser));
			}
		}

		EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(LotApiModel.class, allItems);
		return modelBuilder.build();
	}

	@RequestMapping("/{uuid}")
	@ResponseBody
	@Transactional
	public EmberModel getOne(@CurrentUser User currentUser,
							 @PathVariable("uuid") UUID id) throws PermissionDenied, EntityNotFound {
		LotApiModel lot = lotService.get(id, currentUser);
		EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(lot);
		return modelBuilder.build();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public EmberModel splitOrDeliver(@CurrentUser User currentUser,
							@RequestBody @Validated LotApiModel lot) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
		EmberModel.Builder<LotApiModel> modelBuilder;

		// Previous lot specified alone - SPLIT operation
		if (lot.getPrevious() != null
				&& lot.getPurchase() == null
                && lot.getLocation() == null
                && lot.getCount() > 0) {
			LotSplitResult result = lotService.split(lot.getPrevious().getId(), lot.getCount(), currentUser, lot.getUsedBy());
			modelBuilder = new EmberModel.Builder<LotApiModel>(result.getRequested());
		// Purchase specified - DELIVER operation
		} else if (lot.getPrevious() == null
				&& lot.getPurchase() != null
				&& lot.getLocation() != null
				&& lot.getCount() > 0) {
            LotApiModel result = lotService.delivery(lot, currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
        // Location and previous specified, but no purchase - MOVE operation
        } else if (lot.getLocation() != null) {
            LotApiModel result = lotService.move(lot.getPrevious(), lot.getLocation(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
        // Unassigned
        } else if (lot.getPrevious() != null
                && lot.getAction().equals(LotAction.UNASSIGNED)) {
            LotApiModel result = lotService.unassign(lot.getId(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
        // Unsolder
        } else if (lot.getPrevious() != null
                && lot.getAction().equals(LotAction.UNSOLDERED)) {
            LotApiModel result = lotService.unsolder(lot.getId(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
        // Solder and possibly assign as well
        } else if (lot.getPrevious() != null
                && lot.getAction().equals(LotAction.SOLDERED)) {
            LotApiModel result = lotService.solder(lot.getId(), currentUser, lot.getUsedBy());
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
        // Assign
        } else if (lot.getPrevious() != null
                && lot.getUsedBy() != null) {
            LotApiModel result = lotService.assign(lot.getId(), currentUser, lot.getUsedBy());
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
        // Invalid combination of arguments
		} else {
			throw new InvalidRequest();
		}

		return modelBuilder.build();
	}

	@Transactional
	@RequestMapping(value = "/{uuid}/qr", produces = "image/png")
	public void generateQr(HttpServletResponse response,
						   @CurrentUser User currentUser,
						   @PathVariable("uuid") UUID uuid) throws IOException, PermissionDenied, EntityNotFound {
		LotApiModel lot = lotService.get(uuid, currentUser);
		PurchaseApiModel purchase = purchaseService.get(lot.getPurchase().getId(), currentUser);
		PartTypeApiModel type = typeService.get(purchase.getType().getId(), currentUser);

		response.setContentType("image/jpg");
		response.setHeader("Content-Disposition", "attachment; filename=" + type.getName() + "-" + lot.getId().toString() + ".png");

		ByteArrayOutputStream os = QRCode
				.from("shv://lots/" + uuid.toString())
				.withSize(250, 250)
				.withErrorCorrection(ErrorCorrectionLevel.M)
				.to(ImageType.PNG)
				.withCharset("utf-8")
				.stream();

		response.getOutputStream().write(os.toByteArray());
		response.flushBuffer();
	}
}
