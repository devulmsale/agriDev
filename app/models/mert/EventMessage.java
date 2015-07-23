package models.mert;

import models.base.WeixinUser;
import models.mert.enums.MessageType;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 用户上传的活动消息.
 */
@Entity
@Table(name = "mert_event_messages")
public class EventMessage extends Model {

    /**
     * 所属商户.
     */
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;

    /**
     * 对应的商户活动.
     */
    @ManyToOne
    @JoinColumn(name = "event_id")
    public Event event;

    /**
     * 发送消息的微信用户.
     */
    @ManyToOne
    @JoinColumn(name = "weixin_user_id")
    public WeixinUser weixinUser;

    /**
     * 消息类型.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 10)
    public MessageType messageType;

    /**
     * 图片地址.
     */
    @Column(name = "image_url", length = 200)
    public String imageUrl;

    /**
     * 图片统一id.
     */
    @Column(name = "image_ufid", length = 200)
    public String imageUfid;


    /**
     * 文本消息内容.
     */
    @Column(name = "content", length = 1000)
    public String content;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    public static List<EventMessage> findWithType(Event event, MessageType messageType) {
        return EventMessage.find("event=? and messageType=? and weixinUser.subcribed=? order by id desc",
                event, messageType, Boolean.TRUE).fetch(8);
    }

    public static List<EventMessage> findWithType(Event event, MessageType messageType, Long fromMessageId) {
        return EventMessage.find("event=? and messageType=? and id>=? and weixinUser.subcribed=? order by id",
                event, messageType, (fromMessageId + 1), Boolean.TRUE).fetch(10);
    }
}
