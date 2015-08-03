package controllers;

import models.product.Product;
import models.product.ProductType;
import play.mvc.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

/*import static org.apache.lucene.document.Field.*;*/

public class Application extends Controller {

    public static void index() {
        List<ProductType> productTypeList = ProductType.findTopType();
        render(productTypeList);
    }



    public static void productInfo(Long id) {
        Product product=Product.findById(id);
        render(product);
    }
}