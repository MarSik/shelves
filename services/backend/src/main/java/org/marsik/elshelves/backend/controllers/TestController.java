package org.marsik.elshelves.backend.controllers;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.Lot;
import org.marsik.elshelves.api.entities.PartGroup;
import org.marsik.elshelves.api.entities.User;
import org.marsik.elshelves.backend.dtos.StickerSettings;
import org.marsik.elshelves.backend.services.ElshelvesUserDetailsService;
import org.marsik.elshelves.backend.services.MailgunService;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.StickerService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class TestController {
    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    ElshelvesUserDetailsService userDetailsService;

    @Autowired
    MailgunService mailgunService;

    @Autowired
    StickerService stickerService;

    @RequestMapping("/groups/{id}")
    public EmberModel getGroup(@PathVariable("id") UUID id) {
        PartGroup g = new PartGroup(id, "TEST");
        return new EmberModel.Builder<PartGroup>(g).build();
    }

    @RequestMapping("/lots/{id}")
    public EmberModel getLot(@PathVariable("id") UUID id) {
        Lot lot = new Lot();
        lot.setId(id);
        lot.setCreated(new Date());

        Lot l1 = new Lot();
        l1.setId(uuidGenerator.generate());

        Lot l2 = new Lot();
        l2.setId(uuidGenerator.generate());

        List<Lot> lots = new ArrayList<>();
        lots.add(l1);
        lots.add(l2);
        lot.setNext(lots);

        return new EmberModel.Builder<Lot>(lot)
                .sideLoad(Lot.class, lots)
                .build();
    }

    @RequestMapping("/lots/{id}/next")
    public EmberModel getNextLots(@PathVariable("id") UUID id) {
        Lot l1 = new Lot();
        l1.setId(uuidGenerator.generate());

        Lot l2 = new Lot();
        l2.setId(uuidGenerator.generate());

        List<Lot> lots = new ArrayList<>();
        lots.add(l1);
        lots.add(l2);

        return new EmberModel.Builder<Lot>(Lot.class, lots).build();
    }

    @Transactional
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<Void> registerUser(@RequestBody @Valid User user) {
        if (userDetailsService.loadUserByUsername(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String verificationCode = userDetailsService.createUser(user);
        mailgunService.sendVerificationCode(user.getEmail(), verificationCode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @RequestMapping(value = "/users/verify/{code}", method = RequestMethod.POST)
    public EmberModel verifyUser(@PathVariable("code") String code) {
        String password = userDetailsService.verifyUser(code);
        return new EmberModel.Builder<String>(password).build();
    }

    @RequestMapping(value = "/test/stickers", produces = "application/pdf")
    public void generateStickers(HttpServletResponse response) throws IOException {
        List<StickerCapable> stickers = new ArrayList<>();
        for (int i=0; i<50; i++) {
            stickers.add(new StickerCapable() {
            });
        }

        StickerSettings stickerSettings = new StickerSettings()
                .setPage(StickerSettings.PageSize.A4)
                .setStickerHeightMm(37f)
                .setStickerWidthMm(70f)
                .setStickerVerticalCount(8)
                .setStickerHorizontalCount(3);

        StickerService.Result pdf = stickerService.generateStickers(stickerSettings, stickers);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=somefile.pdf");

        pdf.save(response.getOutputStream());
        pdf.close();
        response.flushBuffer();
    }

    @RequestMapping(value = "/test/qr", produces = "image/jpg")
    public void generateQr(HttpServletResponse response) throws IOException {
        response.setContentType("image/jpg");
        response.setHeader("Content-Disposition", "attachment; filename=qr.jpg");

        ByteArrayOutputStream os = QRCode
                .from("Hello World")
                .withSize(500, 500)
                .withErrorCorrection(ErrorCorrectionLevel.M)
                .to(ImageType.JPG)
                .withCharset("utf-8")
                .stream();

        response.getOutputStream().write(os.toByteArray());
        response.flushBuffer();
    }
}
