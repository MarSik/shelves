package org.marsik.elshelves.backend.controllers;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.marsik.elshelves.api.dtos.LotDeliveryAdHoc;
import org.marsik.elshelves.api.dtos.LotDeliveryTransaction;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.converters.EntityToEmberConversionService;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToLot;
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
import java.util.Objects;
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
    EntityToEmberConversionService entityToEmberConversionService;

    @Autowired
    EmberToLot emberToLot;

    @Autowired
    LotRepository lotRepository;

    private AbstractEntityApiModel cnv(Lot l) {
        return entityToEmberConversionService.convert(l, new THashMap<>());
    }

    private EmberModel prepare(Lot l) {
        AbstractEntityApiModel res = cnv(l);
        EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(res);
        return modelBuilder.build();
    }

    private EmberModel prepare(Iterable<Lot> ls) {
        Map<UUID, Object> cache = new THashMap<>();
        Collection<AbstractEntityApiModel> lots = new THashSet<>();

        for (Lot l: ls) {
            lots.add(entityToEmberConversionService.convert(l, cache));
        }

        EmberModel.Builder<AbstractEntityApiModel> modelBuilder = new EmberModel.Builder<>(lots);
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

        Map<UUID, Object> cache = new THashMap<>();

        EmberModel.Builder<LotApiModel> modelBuilder;

        final Lot lotDb = lotRepository.findById(id);

        Requirement oldRequirement = lotDb.getUsedBy();

        LotSplitResult result = lotService.update(lotDb, lot, currentUser);

        // The expected result of split action is the original (based on ID)
        // lot with lower count, all other objects are sideloaded

        Map<UUID, Lot> lots = new THashMap<>();
        lots.put(result.getRequested().getId(), result.getRequested());
        for (Lot l: result.getOthers()) {
            lots.put(l.getId(), l);
        }

        modelBuilder = new EmberModel.Builder<>(
                entityToEmberConversionService.convert(lots.get(id), cache));
        lots.remove(id);

        for (Lot l: lots.values()) {
            modelBuilder.sideLoad(entityToEmberConversionService.convert(l, cache));
        }

        // Send changed to assignments along
        if (!Objects.equals(lot.getUsedBy(), oldRequirement)) {
            if(lotDb.getUsedBy() != null) {
                modelBuilder.sideLoad(entityToEmberConversionService.convert(lotDb.getUsedBy(), cache));
            }
            if (oldRequirement != null) {
                modelBuilder.sideLoad(entityToEmberConversionService.convert(oldRequirement, cache));
            }
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
            Lot result = lotService.deliverPurchasedLot((PurchasedLot)lot, lot.getExpiration(), currentUser);
            modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));
        } else if (lot0.getPrevious() != null) {
            // The expected result of split action is the new lot with expected count.
            // The remainder is sideloaded.

            LotSplitResult result = lotService.update(lotRepository.findById(lot0.getPrevious().getId()), lot, currentUser);
            Map<UUID, Object> cache = new THashMap<>();

            modelBuilder = new EmberModel.Builder<>(entityToEmberConversionService.convert(result.getRequested(), cache));
            for (Lot l: result.getOthers()) {
                modelBuilder.sideLoad(entityToEmberConversionService.convert(l, cache));
            }
        } else {
            throw new InvalidRequest();
        }

        return modelBuilder.build();
    }

    private <T extends IdentifiedEntity> T buildJustId(UUID id, Class<T> cls) {
        try {
            T object = cls.newInstance();
            object.setId(id);
            return object;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/ad-hoc-delivery", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public EmberModel deliverLotAdHoc(@CurrentUser User currentUser,
                                 @RequestBody @Validated LotDeliveryAdHoc delivery) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
        EmberModel.Builder<LotApiModel> modelBuilder;

        Lot result = lotService.deliverAdHoc(
                buildJustId(delivery.getSource(), Source.class),
                buildJustId(delivery.getType(), Type.class),
                buildJustId(delivery.getLocation(), Box.class),
                delivery.getCount(),
                null,
                currentUser
        );
        modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));

        return modelBuilder.build();
    }


    @RequestMapping(value = "/transaction-delivery", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public EmberModel deliverLotTransaction(@CurrentUser User currentUser,
                                      @RequestBody @Validated LotDeliveryTransaction delivery) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
        EmberModel.Builder<LotApiModel> modelBuilder;

        Lot result = lotService.deliverTypeInTransaction(
                buildJustId(delivery.getTransaction(), Transaction.class),
                buildJustId(delivery.getType(), Type.class),
                buildJustId(delivery.getLocation(), Box.class),
                delivery.getCount(),
                null,
                currentUser
        );
        modelBuilder = new EmberModel.Builder<LotApiModel>(cnv(result));

        return modelBuilder.build();
    }

    @Transactional
	@RequestMapping(value = "/{uuid}/qr", produces = "image/png")
	public void generateQr(HttpServletResponse response,
						   @CurrentUser User currentUser,
						   @PathVariable("uuid") UUID uuid) throws IOException, PermissionDenied, EntityNotFound {
		Lot lot = lotService.get(uuid, currentUser);
		Type type = typeService.get(lot.getType().getId(), currentUser);

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
