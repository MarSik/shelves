package org.marsik.elshelves.backend.controllers;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToLot;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.entities.converters.NamedObjectToEmber;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/lots")
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

    @Autowired
    NamedObjectToEmber namedObjectToEmber;

    @Autowired
    LotToEmber lotToEmber;

    @Autowired
    EmberToLot emberToLot;

    private LotApiModel cnv(Lot l) {
        return lotToEmber.convert(l, 1, new THashMap<>());
    }

    private EmberModel prepare(Lot l) {
        LotApiModel res = lotToEmber.convert(l, 1, new THashMap<>());
        EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(res);
        return modelBuilder.build();
    }

    private EmberModel prepare(Iterable<Lot> ls) {
        Map<UUID, Object> cache = new THashMap<>();
        Collection<LotApiModel> lots = new THashSet<>();

        for (Lot l: ls) {
            lots.add(lotToEmber.convert(l, 1, cache));
        }

        EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<>(LotApiModel.class, lots);
        return modelBuilder.build();
    }

    @ResponseBody
	@RequestMapping
	@Transactional
	public EmberModel getAll(@CurrentUser User currentUser,
							 @RequestParam(value = "ids[]", required = false) UUID[] ids) throws PermissionDenied, EntityNotFound {
		Collection<Lot> allItems;

		if (ids == null) {
			allItems = lotService.getAll(currentUser);
		} else {
			allItems = new THashSet<>();
			for (UUID id: ids) {
				allItems.add(lotService.get(id, currentUser));
			}
		}

		return prepare(allItems);
	}

	@RequestMapping("/{uuid}")
	@ResponseBody
	@Transactional
	public EmberModel getOne(@CurrentUser User currentUser,
							 @PathVariable("uuid") UUID id) throws PermissionDenied, EntityNotFound {
		Lot lot = lotService.get(id, currentUser);

		return prepare(lot);
	}

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public EmberModel updateLot(@CurrentUser User currentUser,
                                @RequestBody @Validated LotApiModel lot) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
        return splitOrDeliver(currentUser, lot);
    }

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public EmberModel splitOrDeliver(@CurrentUser User currentUser,
							@RequestBody @Validated LotApiModel lot0) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
		EmberModel.Builder<LotApiModel> modelBuilder;

        Lot lot = emberToLot.convert(lot0, Integer.MAX_VALUE, new THashMap<>());

		// Previous lot specified alone - SPLIT operation (with possible MOVE)
		if (lot0.getPrevious() != null
				&& lot.getPurchase() == null
                && lot.getUsedBy() == null
                && lot.getCount() != null
                && lot.getCount() > 0) {
			LotSplitResult result = lotService.split(lot0.getPrevious().getId(), lot.getCount(), currentUser, lot.getUsedBy());
            Lot endResult = result.getRequested();

            // Move the requested amount if needed
            if (lot.getLocation() != null
                    && !lot.getLocation().equals(result.getRequested().getLocation())) {
                endResult = lotService.move(endResult.getId(), lot.getLocation(), currentUser);
            }

			modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(endResult));
            if (result.getRemainder() != null) {
                modelBuilder.sideLoad(cnv(result.getRemainder()));
            }

            // Purchase specified - DELIVER operation
		} else if (lot0.getPrevious() == null
				&& lot.getPurchase() != null
				&& lot.getLocation() != null
                && lot.getCount() != null
				&& lot.getCount() > 0) {
            Lot result = lotService.delivery(lot, lot.getExpiration(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));

        // Location and previous specified, but no purchase - MOVE operation
        } else if (lot0.getPrevious() != null
                && lot.getLocation() != null) {
            Lot result = lotService.move(lot0.getPrevious().getId(), lot.getLocation(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));

            // Unassigned
        } else if (lot0.getPrevious() != null
                && lot.getStatus() != null
                && lot.getStatus().equals(LotAction.UNASSIGNED)) {
            Lot result = lotService.unassign(lot0.getPrevious().getId(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));

            // Unsolder
        } else if (lot0.getPrevious() != null
                && lot.getStatus() != null
                && lot.getStatus().equals(LotAction.UNSOLDERED)) {
            Lot result = lotService.unsolder(lot0.getPrevious().getId(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));

            // Solder and possibly assign as well
        } else if (lot0.getPrevious() != null
                && lot.getStatus() != null
                && lot.getStatus().equals(LotAction.SOLDERED)) {
            Lot result = lotService.solder(lot0.getPrevious().getId(), currentUser, lot.getUsedBy());
            modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));

        // Assign
        } else if (lot0.getPrevious() != null
                && lot.getUsedBy() != null) {
            Lot result = lotService.assign(lot0.getPrevious().getId(), currentUser, lot.getUsedBy());
            modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));

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
		Lot lot = lotService.get(uuid, currentUser);
		Purchase purchase = purchaseService.get(lot.getPurchase().getId(), currentUser);
		Type type = typeService.get(purchase.getType().getId(), currentUser);

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
