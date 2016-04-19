/**
 * @项目名称: framework
 * @文件名称: PooledObject.java
 * @Date: 2015年11月6日
 * @author: wenlai
 * @type: PooledObject
 */
package cn.framework.core.pool;

import java.io.Closeable;
import java.io.IOException;

/**
 * 内存池使用说明：<br>
 * 想存入池中的元素继承自{@link PooledObject}<br>
 * 池本身继承自{@link Pool}<br>
 * 代码如下(针对jdk1.8，使用try---with):<blockquote>
 * try(PooledObject obj = pool.get();) { <br>
 * ....<br>
 * <br>
 * }
 * </blockquote>
 * 池中元素基类
 * 
 * @author wenlai
 */
public abstract class PooledObject implements Closeable {
    
    /*
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        container.returnObject(this);
    }
    
    /**
     * 元素所在的池
     */
    public Pool<PooledObject> container;
}
