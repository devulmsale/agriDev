package controllers.weixin;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import ext.pay.weixin.MD5Util;
import ext.pay.weixin.v3.WxpayFactory;
import helper.QRCodeGenerator;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.DateUtil;
import models.order.Order;
import models.product.Product;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import util.common.RandomNumberUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by upshan on 15/8/11.
 */
public class OrderController extends Controller {

    public static String SEP= "/";
    public static String  FILE_UPLOAD_EDIT_DIR= SEP+"public"+SEP+"upload"+SEP+"order"+SEP;  //商铺图片
    public static WxpayFactory wxpayFactory = WxpayFactory.getDefaultInstance();
    public static Integer PAGE_SIZE = 15;

    public static void qrCode(String orderNumber) throws IOException, WriterException {
        Order order = Order.findByOrderNumber(orderNumber);
        if(order == null) {
            //TODO 如果订单不存在 执行操作
        }
        if(StringUtils.isBlank(order.qrImage)) {
            StringBuilder wxPayCode = new StringBuilder();
            Properties conf = wxpayFactory.getConf();
            wxPayCode.append("weixin://wxpay/bizpayurl?");
            String timeStamp = DateUtil.dateToString(new Date(), "yyyyMMddHHmmss");
            String nonce_str = RandomNumberUtil.generateRandomChars(16);
            wxPayCode.append("sign=" + getSign(conf.getProperty("appid"), conf.getProperty("mch_id"), order.orderNumber, conf.getProperty("KEY"), timeStamp, nonce_str));
            wxPayCode.append("&appid=" + conf.getProperty("appid"));
            wxPayCode.append("&mch_id=" + conf.getProperty("mch_id"));
            wxPayCode.append("&product_id=" + order.orderNumber);
            wxPayCode.append("&time_stamp=" + timeStamp);
            wxPayCode.append("&nonce_str=" + nonce_str);
            Logger.info("wxPayCode : %s ------", wxPayCode.toString());
            File orderFile = new File(Play.applicationPath.getAbsolutePath() + FILE_UPLOAD_EDIT_DIR);
            if (orderFile.exists()) {
                orderFile.mkdirs();
            }
            File qrImage = new File(Play.applicationPath.getAbsolutePath() + FILE_UPLOAD_EDIT_DIR + order.orderNumber + ".png");
            if (!qrImage.exists()) {
                qrImage.mkdirs();
            }
            order.qrImage = FILE_UPLOAD_EDIT_DIR + order.orderNumber + ".png";
            order.save();
            FileOutputStream out = new FileOutputStream(Play.applicationPath.getAbsolutePath() + order.qrImage);
            QRCodeGenerator.generateCodeToStream(out, wxPayCode.toString(), BarcodeFormat.QR_CODE, 300, 300, "PNG");
        }
        render(order);
    }




    public static String getSign(String appid , String mchId , String orderNumber , String key , String timeStamp , String nonce_str) {
        StringBuffer signBuild = new StringBuffer();
        signBuild.append("appid="+appid);
        signBuild.append("&mch_id="+mchId);
        signBuild.append("&nonce_str="+ nonce_str);
        signBuild.append("&product_id="+orderNumber);
        signBuild.append("&time_stamp="+ timeStamp);
        signBuild.append("&key="+key);
        return  MD5Util.md5(signBuild.toString()).toUpperCase();
    }

    public static void getAjaxImagePath(String orderNumber) throws IOException, WriterException {
        Map<String , Object> resultmap = new HashMap<>();
        Order order = Order.findByOrderNumber(orderNumber);
        if(order == null) {
            resultmap.put("success" , false);
            resultmap.put("msg" , "订单信息不存在,或订单已删除!");
            renderJSON(resultmap);
        }
        if(StringUtils.isBlank(order.qrImage)) {
            StringBuilder wxPayCode = new StringBuilder();
            Properties conf = wxpayFactory.getConf();
            wxPayCode.append("weixin://wxpay/bizpayurl?");
            String timeStamp = DateUtil.dateToString(new Date(), "yyyyMMddHHmmss");
            String nonce_str = RandomNumberUtil.generateRandomChars(16);
            wxPayCode.append("sign=" + getSign(conf.getProperty("appid"), conf.getProperty("mch_id"), order.orderNumber, conf.getProperty("KEY"), timeStamp, nonce_str));
            wxPayCode.append("&appid=" + conf.getProperty("appid"));
            wxPayCode.append("&mch_id=" + conf.getProperty("mch_id"));
            wxPayCode.append("&product_id=" + order.orderNumber);
            wxPayCode.append("&time_stamp=" + timeStamp);
            wxPayCode.append("&nonce_str=" + nonce_str);
            Logger.info("wxPayCode : %s ------", wxPayCode.toString());
            order.qrImage = FILE_UPLOAD_EDIT_DIR + order.orderNumber + ".png";
            order.save();
            File file = new File(FILE_UPLOAD_EDIT_DIR);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(Play.applicationPath.getAbsolutePath() + order.qrImage);
            QRCodeGenerator.generateCodeToStream(out, wxPayCode.toString(), BarcodeFormat.QR_CODE, 300, 300, "PNG");
        }
        resultmap.put("success" , true);
        resultmap.put("path" , order.qrImage);
        renderJSON(resultmap);
    }

}
