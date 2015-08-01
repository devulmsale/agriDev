package helper.imageupload;

/**
 * 图片上传Result.
 */
public class ImageUploadResult {

    public int result = 0;


    /**
     * 文件UUID加扩展名，组成ufId.
     *
     * 格式形如：2g9crkdgjnc7rshc4lrukdb4ki.png
     */
    public String ufId;

    /**
     * 原始文件名.
     */
    public String origFileName;

    public ImageUploadResult() {
        this.result = 0;
    }

    public ImageUploadResult(int result) {
        this.result = result;
    }

    public static ImageUploadResult FAILED = new ImageUploadResult(100);
}
