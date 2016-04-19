package cn.framework.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * project code
 * package cn.framework.db
 * create at 16/4/1 上午10:48
 *
 * @author wenlai
 */

@Entity
public class Message {

    public long version;

    public int code;

    public String to;

    public String from;

    @PrimaryKey
    public String message;
}
