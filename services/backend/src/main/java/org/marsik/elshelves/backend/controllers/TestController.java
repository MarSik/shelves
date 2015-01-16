package org.marsik.elshelves.backend.controllers;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import nl.marcus.ember.EmberSchema;
import org.marsik.elshelves.backend.dtos.StickerSettings;
import org.marsik.elshelves.backend.services.EmberSchemaService;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.StickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {
    @Autowired
    StickerService stickerService;

    @Autowired
    EmberSchemaService emberSchemaService;

    @RequestMapping("/schema")
    @ResponseBody
    public EmberSchema getEmberSchema() {
        return emberSchemaService.getEmberSchema();
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
