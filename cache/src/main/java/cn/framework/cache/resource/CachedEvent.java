package cn.framework.cache.resource;

import java.io.Serializable;
import java.util.UUID;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/4/6 下午2:32
 *
 * @author wenlai
 */
public class CachedEvent implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * from
     */
    private String from;

    /**
     * message
     */
    private String message;

    /**
     * title
     */
    private String title;

    /**
     * constructor
     */
    public CachedEvent() {
        this.id = UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * constructor
     *
     * @param id id
     */
    public CachedEvent(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("message : [id:%s from:%s title:%s message:%s]", this.id, this.from, this.title, this.message);
    }
}
