package com.app.IVAS.security;

import lombok.Data;
import lombok.Setter;
import org.springframework.stereotype.Component;


import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@Component
public class QrCodeServices {

    @Setter
    public class Size{

        private Integer width = 100;

        private Integer height = 100;

        public Integer getWidth() {
            if (width == 0){
                width = 100;
            }
            return width;
        }

        public Integer getHeight() {
            if (height == 0){
                height = 100;
            }
            return height;
        }
    }

    public byte[] createQrCode(String data, Size size) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size.getWidth(), size.getHeight());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (WriterException | IOException ex) {
            Logger.getLogger(QrCodeServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String createBase64QrCode(String data, Size size) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(
                createQrCode(data, size)
        );
    }

    public String base64CertificateQrCode(String dataValue) {
        return createBase64QrCode(dataValue, new Size());

    }

    public String base64CertificateQrCode_(Map<String, Object> signature) {
        Gson gson = new Gson();
        String dataValue = gson.toJson(signature);
        return createBase64QrCode(dataValue, new Size());
    }

    /**
     * @param signature
     * @return
     */
    public String base64CertificateQrCode(Object signature) {
        Gson gson = new Gson();
        String dataValue = gson.toJson(signature);
        return createBase64QrCode(dataValue, new Size());
    }
}
