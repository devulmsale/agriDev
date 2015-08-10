package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import helper.imageupload.ImageUploadResult;
import helper.imageupload.ImageUploader;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.JSONEntity;
import models.constants.DeletedStatus;
import models.operate.OperateUser;
import models.product.*;
import models.product.enums.MarketingMode;
import models.product.enums.PackageMethod;
import models.product.enums.ShippingMethod;
import models.product.enums.StoreMethod;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片上传
 */
@With(MerchantSecure.class)
public class ImageController extends Controller {
    public static final String FOLID_JSON_URL = "http://gxf.tunnel.mobi/imageJSON1";

    public static void index(File pic) {

        render();
    }

    private static void initData() {
        // 绠＄悊鍛樹俊鎭�
        OperateUser operateUser = Secure.getOperateUser();
        renderArgs.put("operateUser" , operateUser);

        //绠＄悊鍛橀偖绠�
        Long count = 8l;
        renderArgs.put("emailCount" , count);
    }

    public static void uploadImage(File pic) {
        HttpRequest httpRequest = HttpRequest
                .post(FOLID_JSON_URL)
                .form(
                        "image", pic
                );
        HttpResponse httpResponse = httpRequest.send();

        String responseBody = httpResponse.body();
        Logger.info("responseBody=" + responseBody);
    }
}