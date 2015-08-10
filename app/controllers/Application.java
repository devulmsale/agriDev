package controllers;

import models.product.Product;
import models.product.ProductType;
import play.Logger;
import play.mvc.*;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

/*import static org.apache.lucene.document.Field.*;*/

public class Application extends Controller {

    public static final String FOLID_JSON_URL = "http://gxf.tunnel.mobi/folderJSON";

    //?code=N9RXXkr6&loginName=admin&password=123456

    public static void index() {
        HttpRequest httpRequest = HttpRequest
                .get(FOLID_JSON_URL)
                .form(
                        "code", "N9RXXkr6",
                        "loginName", "admin",
                        "password",  "123456"
                );
        HttpResponse httpResponse = httpRequest.send();

        String responseBody = httpResponse.body();
        Logger.info("responseBody=" + responseBody);



        List<ProductType> productTypeList = ProductType.findTopType();
        Logger.info("商品一级类别 :%s=",productTypeList.size());
        render(productTypeList);
    }



    public static void productInfo(Long id) {
        Product product=Product.findById(id);
        render(product);
    }
}