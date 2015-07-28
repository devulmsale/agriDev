package controllers.merchant;

import controllers.elasticsearch.ElasticSearchController;
import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import models.mert.MerchantUser;
import models.operate.OperateUser;
import net.sf.oval.guard.Post;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import play.Logger;
import play.modules.elasticsearch.ElasticSearch;
import play.modules.elasticsearch.search.SearchResults;
import play.mvc.Controller;
import play.mvc.With;


@With(MerchantSecure.class)
public class Application extends ElasticSearchController {

    public static void index() {
        MerchantUser merchantUser = MerchantSecure.getMerchantUser();
        render(merchantUser);
    }

    public static void search() {
        index();
    }


}