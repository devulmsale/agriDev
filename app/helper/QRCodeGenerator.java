package helper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by upshan on 15/7/11.
 */
public class QRCodeGenerator {

    public static void generateCodeToStream(OutputStream out, String qrCodeString, BarcodeFormat format,
                                            int height, int width, String contentType) throws WriterException, IOException {
        // get a byte matrix for the data

        BitMatrix matrix = null;
        com.google.zxing.Writer writer = new MultiFormatWriter();
        try {
            matrix = writer.encode(qrCodeString, format, width, height);
        } catch (IllegalArgumentException ex) {
            throw new WriterException(ex.getMessage());
        }
        MatrixToImageWriter.writeToStream(matrix, contentType, out);
    }

    public static void main(String[] args) throws Exception {
        FileOutputStream out = new FileOutputStream(
                "/tmp/qr_code.png");

        //generateQrCodeToStream(out,"12345678901",BarcodeFormat.UPC_A,30,100,"PNG");

        generateCodeToStream(out, "http://www.hyshi.cn/test?website=x&a=2", BarcodeFormat.QR_CODE, 300, 300, "PNG");
    }


}
