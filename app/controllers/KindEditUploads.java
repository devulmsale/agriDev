package controllers;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import helper.imageupload.ImageUploadResult;
import helper.imageupload.ImageUploader;
import play.Play;
import play.mvc.Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class KindEditUploads extends Controller {

    public static String SEP= "/";
    public static String  FILE_UPLOAD_EDIT_DIR=SEP+"public"+SEP+"upload"+SEP+"kindedit"+SEP;  //商铺图片

    /**
     * Ediuts 图片上传
     * @param imgFile
     * @throws java.io.IOException
     */
    public static void upload(File imgFile) throws Exception {
        File pathFile = new File(FILE_UPLOAD_EDIT_DIR);
        if(!pathFile.exists()) {
            pathFile.mkdirs();
        }

        String toPath= FILE_UPLOAD_EDIT_DIR + System.currentTimeMillis()+imgFile.getName().substring(imgFile.getName().indexOf("."));
        String s="";
        //如果上传的图片不存在 或者 图片大于1M 就返回错误信息
        // 现在这里错误 一会看一下文档 是什么问题!
        if(imgFile == null || imgFile.length()>1000000){
            s="{\"error\":1,\"url\":\"您上传的图片大于1M或图片不存在。请重试!!\"}";
        }else{
            BufferedImage img = ImageIO.read(imgFile);
            if(img.getWidth()>730){
                // 要输出的文件
                int height=Math.round(img.getHeight()/(img.getWidth()/730));
                FileOutputStream newImgFile = new FileOutputStream(Play.applicationPath.getAbsolutePath()+toPath);
                // 新建缓存图片对象
                BufferedImage newImg = new BufferedImage(730, height,BufferedImage.TYPE_INT_RGB);
                // 用新建的对象将要处理的图片重新绘制，宽高是原来的一半
                newImg.getGraphics().drawImage(img, 0, 0, 730, height, null);

                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newImgFile);
                // 将图片重新编码
                encoder.encode(newImg);
                newImgFile.close();
            }else{
                ImageUploadResult imageUploadResult = ImageUploader.upload(imgFile);
//                block.uFid = imageUploadResult.ufId;
                toPath = ImageUploader.getImageUrl(imageUploadResult.ufId, "200x");
            }
            s="{\"error\":0,\"url\":\""+toPath+"\"}";
        }

        renderText(s);

    }
}
