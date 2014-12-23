package org.marsik.elshelves.backend.controllers;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.BoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/boxes")
public class BoxController extends AbstractRestController<Box, BoxApiModel> {
    BoxService boxService;

    @Autowired
    public BoxController(BoxService boxService) {
        super(BoxApiModel.class,
              boxService);
        this.boxService = boxService;
    }

    @Override
    protected void sideLoad(BoxApiModel dto, EmberModel.Builder<BoxApiModel> builder) {
        if (dto.getBoxes() != null) {
            builder.sideLoad(BoxApiModel.class, dto.getBoxes());
        }

        if (dto.getLots() != null) {
            builder.sideLoad(LotApiModel.class, dto.getLots());
        }

        if (dto.getParent() != null) {
            builder.sideLoad(dto.getParent());
        }

        if (dto.getBelongsTo() != null) {
            builder.sideLoad(dto.getBelongsTo());
        }
    }

    @Transactional
    @RequestMapping(value = "/{uuid}/qr", produces = "image/png")
    public void generateQr(HttpServletResponse response,
                           @CurrentUser User currentUser,
                           @PathVariable("uuid") UUID uuid) throws IOException, PermissionDenied {
        BoxApiModel box = boxService.get(uuid, currentUser);

        response.setContentType("image/jpg");
        response.setHeader("Content-Disposition", "attachment; filename=" + box.getName() + ".png");

        ByteArrayOutputStream os = QRCode
                .from("shv://boxes/"+uuid.toString())
                .withSize(250, 250)
                .withErrorCorrection(ErrorCorrectionLevel.M)
                .to(ImageType.PNG)
                .withCharset("utf-8")
                .stream();

        response.getOutputStream().write(os.toByteArray());
        response.flushBuffer();
    }
}
