package helper.imageupload;

import com.google.gson.Gson;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.io.FileNameUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Play;

import java.io.File;

/**
 * 文件上传器.
 */
public class ImageUploader {

    public static final String IMAGE_CDN_BASE   = Play.configuration.getProperty("image-uploader.cdn.base",
            "http://121.42.146.152:9292/");
    public static final String UPLOAD_IMAGE_URL = Play.configuration.getProperty("image-uploader.url",
            "http://121.42.146.152:9292/images");


    public static ImageUploadResult upload(File imageFile) throws Exception {
        String fileName = imageFile.getName();
        return upload(fileName, imageFile);
    }

    public static ImageUploadResult upload(String fileName, File imageFile) throws Exception {
        Logger.info("image-uploader.url=" + UPLOAD_IMAGE_URL);
        HttpRequest httpRequest = HttpRequest
                .post(UPLOAD_IMAGE_URL)
                .form(
                        "title", "UploadImage",
                        "filename", fileName,
                        "data",  imageFile
                );
        HttpResponse httpResponse = httpRequest.send();

        String responseBody = httpResponse.body();
        Logger.info("responseBody=" + responseBody);
        ImageUploadJson imageUploadJson = new Gson().fromJson(responseBody, ImageUploadJson.class);
        // {"result_message":"Clean","result_code":0,"id":7,"file_uuid":"a6si5ui133bona4or7ffql2g18",
        //    "file_name":"TooBusyToImprove.png","file_size":894143,"content_type":"image/png"}

        if (StringUtils.isBlank(imageUploadJson.id)) {
            return ImageUploadResult.FAILED;
        }

        ImageUploadResult result = new ImageUploadResult();

        result.ufId = imageUploadJson.file_uuid + "." + FileNameUtil.getExtension(imageUploadJson.file_name);
        result.origFileName = imageUploadJson.file_name;

        return result;
    }

    /**
     * 得到图片网址.
     * @param ufId 统一文件ID，带后缀，如: a6si5ui133bona4or7ffql2g18.png
     * @param op 文件操作，如: "200x100", "x100", "200x", 为空表示显示原图
     * @return 生成带校验的图片网址
     */
    public static String getImageUrl(String ufId, String op) {
        int lastDotIndex = ufId.lastIndexOf(".");
        if (lastDotIndex < 0) {
            Logger.warn("Bad ufId:" + ufId);
            return null;  //TODO: 返回一个默认图片
        }
        String uuid = ufId.substring(0, lastDotIndex);
        StringBuilder sbUrl = new StringBuilder(IMAGE_CDN_BASE);
        sbUrl.append(uuid.substring(0, 2)).append('/')
                .append(uuid.substring(2, 4)).append('/')
                .append(uuid.substring(4, 6)).append('/');
        // checkValue
        sbUrl.append(sign(uuid, op)).append('-');

        String filePart = uuid.substring(6);
        String ext = ufId.substring(lastDotIndex);
        sbUrl.append(filePart);
        if (StringUtils.isNotBlank(op)) {
            sbUrl.append('-').append(op);
        }
        sbUrl.append(ext);
        Logger.info("imageUrl=" + sbUrl);
        return sbUrl.toString();
    }

    /**
     * 得到原始图片网址，不进行任何图片操作.
     * @param ufId 统一文件ID，带后缀，如: a6si5ui133bona4or7ffql2g18.png
     * @return 生成带校验的图片网址
     */
    public static String getImageUrl(String ufId) {
        return getImageUrl(ufId, null);
    }

    /**
     * 实现与imageServer一样的签名机制
     * @param uuid 文件UUID名
     * @param op 操作名
     * @return 与Digest::SHA1.hexdigest("adfinz823z8355t_#{uuid}_#{opname}")[0,8]一样的签名
     */
    private static String sign(String uuid, String op) {
        StringBuilder sb = new StringBuilder("adfinz823z8355t_");
        sb.append(uuid).append('_');
        if (StringUtils.isNotBlank(op)) {
            sb.append(op);
        }

        String sha1 = DigestUtils.shaHex(sb.toString());

        return sha1.substring(0, 8);
    }
}
