package models.base;

import models.mert.Merchant;
import play.db.jpa.Model;

import javax.persistence.*;

/**
 * 文章类别，每个商家可以用自己的类别。
 * <p/>
 * 这个类别可用于控制一些微信菜单的文章显示
 */
@Entity
@Table(name = "article_cats")
public class ArticleCategory extends Model {

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;

    /**
     * 显示名.
     */
    @Column(name = "showName")
    public String showName;
}
