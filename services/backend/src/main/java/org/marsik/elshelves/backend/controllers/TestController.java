package org.marsik.elshelves.backend.controllers;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gnu.trove.map.hash.THashMap;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import nl.marcus.ember.EmberSchema;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.UserExists;
import org.marsik.elshelves.backend.dtos.StickerSettings;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.ElshelvesUserDetailsService;
import org.marsik.elshelves.backend.services.EmberSchemaService;
import org.marsik.elshelves.backend.services.MailgunService;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.StickerService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    UserToEmber userToEmber;

    @Autowired
    EmberSchemaService emberSchemaService;

    @RequestMapping("/schema")
    @ResponseBody
    public EmberSchema getEmberSchema() {
        return emberSchemaService.getEmberSchema();
    }

    @Transactional
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public EmberModel registerUser(@RequestBody @Valid UserApiModel user) throws UserExists {
        if (userDetailsService.getUser(user.getEmail()) != null) {
            throw new UserExists();
        }

        String verificationCode = userDetailsService.createUser(user);
        mailgunService.sendVerificationCode(user.getEmail(), verificationCode);

        return new EmberModel.Builder<UserApiModel>(user).build();
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

    @Transactional
    @RequestMapping("/users/whoami")
    public EmberModel getCurrentUser(@CurrentUser User currentUser) {
        return new EmberModel.Builder<UserApiModel>(userToEmber.convert(currentUser, 1, new THashMap<UUID, Object>())).build();
    }
}
