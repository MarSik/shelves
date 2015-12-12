package org.marsik.elshelves.backend.controllers;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.LotApiModel;
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

    @Autowired
    LotRepository lotRepository;

    private LotApiModel cnv(Lot l) {
        return lotToEmber.convert(l, new THashMap<>());
    }

    private EmberModel prepare(Lot l) {
        LotApiModel res = cnv(l);
        EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(res);
        return modelBuilder.build();
    }

    private EmberModel prepare(Iterable<Lot> ls) {
        Map<UUID, Object> cache = new THashMap<>();
        Collection<LotApiModel> lots = new THashSet<>();

        for (Lot l: ls) {
            lots.add(lotToEmber.convert(l, cache));
        }

        EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<>(lots);
        return modelBuilder.build();
    }

    @ResponseBody
	@RequestMapping
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	public EmberModel getOne(@CurrentUser User currentUser,
							 @PathVariable("uuid") UUID id) throws PermissionDenied, EntityNotFound {
		Lot lot = lotService.get(id, currentUser);

		return prepare(lot);
	}

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public EmberModel updateLot(@CurrentUser User currentUser,
                                @PathVariable("uuid") UUID id,
                                @RequestBody @Validated LotApiModel lot0) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
        Lot lot = emberToLot.convert(lot0, new THashMap<>());
        LotSplitResult result = lotService.update(lotRepository.findById(id), lot, currentUser);

        Map<UUID, Object> cache = new THashMap<>();

        EmberModel.Builder<LotApiModel> modelBuilder;

        // The expected result of split action is the original (based on ID)
        // lot with lower count, the taken lot is sideloaded

        if (result.getRemainder() != null
                && !result.getRemainder().getId().equals(id)) {
            modelBuilder = new EmberModel.Builder<>(
                    lotToEmber.convert(result.getRemainder(), cache));
            modelBuilder.sideLoad(
                    lotToEmber.convert(result.getRequested(), cache));
        } else if (result.getRemainder() != null) {
            modelBuilder = new EmberModel.Builder<>(
                    lotToEmber.convert(result.getRequested(), cache));
            modelBuilder.sideLoad(
                    lotToEmber.convert(result.getRemainder(), cache));
        } else {
            modelBuilder = new EmberModel.Builder<>(
                    lotToEmber.convert(result.getRequested(), cache));
        }

        return modelBuilder.build();
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public EmberModel deliverLot(@CurrentUser User currentUser,
                                 @RequestBody @Validated LotApiModel lot0) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
        EmberModel.Builder<LotApiModel> modelBuilder;

        Lot lot = emberToLot.convert(lot0, new THashMap<>());

        if (lot0.getPurchase() != null && lot0.getPrevious() == null) {
            Lot result = lotService.delivery(lot, lot.getExpiration(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));
        } else if (lot0.getPrevious() != null) {
            // The expected result of split action is the new lot with expected count.
            // The remainder is sideloaded.

            LotSplitResult result = lotService.update(lotRepository.findById(lot0.getPrevious().getId()), lot, currentUser);
            Map<UUID, Object> cache = new THashMap<>();

            modelBuilder = new EmberModel.Builder<>(lotToEmber.convert(result.getRequested(), cache));
            if (result.getRemainder() != null) {
                modelBuilder.sideLoad(lotToEmber.convert(result.getRemainder(), cache));
            }
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
