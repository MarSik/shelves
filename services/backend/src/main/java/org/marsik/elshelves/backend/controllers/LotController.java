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
import org.marsik.elshelves.backend.services.BoxService;
import org.marsik.elshelves.backend.services.LotService;
import org.marsik.elshelves.backend.services.PurchaseService;
import org.marsik.elshelves.backend.services.RequirementService;
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

    @Autowired
    BoxService boxService;

    @Autowired
    RequirementService requirementService;

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

		// Previous lot specified alone - SPLIT operation (with possible MOVE)
		if (lot.getPrevious() != null
				&& lot.getPurchase() == null
                && lot.getUsedBy() == null
                && lot.getCount() != null
                && lot.getCount() > 0) {
			LotSplitResult result = lotService.split(lot.getPrevious().getId(), lot.getCount(), currentUser, lot.getUsedBy());
            LotApiModel endResult = result.getRequested();

            // Move the requested amount if needed
            if (lot.getLocation() != null
                    && !lot.getLocation().equals(result.getRequested().getLocation())) {
                endResult = lotService.move(endResult.getId(), lot.getLocation(), currentUser);
            }

			modelBuilder = new EmberModel.Builder<LotApiModel>(endResult);
            prepareSideloadedUpdates(endResult, currentUser, modelBuilder);
            prepareSideloadedUpdates(result.getRequested(), currentUser, modelBuilder);
            if (result.getRemainder() != null) {
                prepareSideloadedUpdates(result.getRemainder(), currentUser, modelBuilder);
            }

            // Purchase specified - DELIVER operation
		} else if (lot.getPrevious() == null
				&& lot.getPurchase() != null
				&& lot.getLocation() != null
                && lot.getCount() != null
				&& lot.getCount() > 0) {
            LotApiModel result = lotService.delivery(lot, lot.getExpiration(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
            prepareSideloadedUpdates(result, currentUser, modelBuilder);

        // Location and previous specified, but no purchase - MOVE operation
        } else if (lot.getPrevious() != null
                && lot.getLocation() != null) {
            LotApiModel result = lotService.move(lot.getPrevious().getId(), lot.getLocation(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
            prepareSideloadedUpdates(result, currentUser, modelBuilder);

            // Unassigned
        } else if (lot.getPrevious() != null
                && lot.getAction() != null
                && lot.getAction().equals(LotAction.UNASSIGNED)) {
            LotApiModel result = lotService.unassign(lot.getPrevious().getId(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
            prepareSideloadedUpdates(result, currentUser, modelBuilder);

            // Unsolder
        } else if (lot.getPrevious() != null
                && lot.getAction() != null
                && lot.getAction().equals(LotAction.UNSOLDERED)) {
            LotApiModel result = lotService.unsolder(lot.getPrevious().getId(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
            prepareSideloadedUpdates(result, currentUser, modelBuilder);

            // Solder and possibly assign as well
        } else if (lot.getPrevious() != null
                && lot.getAction() != null
                && lot.getAction().equals(LotAction.SOLDERED)) {
            LotApiModel result = lotService.solder(lot.getPrevious().getId(), currentUser, lot.getUsedBy());
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
            prepareSideloadedUpdates(result, currentUser, modelBuilder);

        // Assign
        } else if (lot.getPrevious() != null
                && lot.getUsedBy() != null) {
            LotApiModel result = lotService.assign(lot.getPrevious().getId(), currentUser, lot.getUsedBy());
            modelBuilder = new EmberModel.Builder<LotApiModel>(result);
            prepareSideloadedUpdates(result, currentUser, modelBuilder);

        // Invalid combination of arguments
		} else {
			throw new InvalidRequest();
		}

		return modelBuilder.build();
	}

    private void prepareSideloadedUpdates(LotApiModel result, User currentUser, EmberModel.Builder<LotApiModel> modelBuilder) throws PermissionDenied, EntityNotFound {
        // Sideload cache updates for all relevant objects
        PurchaseApiModel purchaseApiModel = purchaseService.get(result.getPurchase().getId(), currentUser);
        modelBuilder.purge(purchaseApiModel);
        modelBuilder.purge(typeService.get(purchaseApiModel.getType().getId(), currentUser));

        if (result.getLocation() != null) {
            modelBuilder.purge(boxService.get(result.getLocation().getId(), currentUser));
        }

        if (result.getUsedBy() != null) {
            modelBuilder.purge(requirementService.get(result.getUsedBy().getId(), currentUser));
        }

        /* Update the cache for items that might have lost the entity - box and requirement */
        if (result.getPrevious() != null) {
            LotApiModel previous = lotService.get(result.getPrevious().getId(), currentUser);
            modelBuilder.purge(previous);
            if (previous.getUsedBy() != null
                    && !previous.getUsedBy().equals(result.getUsedBy())) {
                modelBuilder.purge(requirementService.get(previous.getUsedBy().getId(), currentUser));
            }

            if (previous.getLocation() != null
                    && !previous.getLocation().equals(result.getLocation())) {
                modelBuilder.purge(boxService.get(previous.getLocation().getId(), currentUser));
            }
        }
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
