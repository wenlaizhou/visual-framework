/**
 * @项目名称: framework
 * @文件名称: Log4jStdOut.java
 * @Date: 2015年10月27日
 * @author: wenlai
 * @type: Log4jStdOut
 */
package cn.framework.core.log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author wenlai
 *
 */
public class Log4jStdOut extends OutputStream {
    
    private static final int MAX_BLOCK_SIZE = 1024 * 1024;
    
    private final byte[] buf = new byte[1024 * 1024];
    
    private int pos = 0;
    
    /*
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        drain();
    }
    
    /*
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }
    
    /*
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (pos >= MAX_BLOCK_SIZE)
            drain();
        int wlen = Math.min(len, MAX_BLOCK_SIZE - pos);
        System.arraycopy(b, off, buf, pos, wlen);
        pos += wlen;
    }
    
    /*
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        if (pos >= MAX_BLOCK_SIZE)
            drain();
        buf[pos++] = (byte) b;
    }
    
    /**
     * 
     */
    private void drain() {
        if (pos == 0)
            return;
        LogProvider.getFrameworkInfoLogger().info(buf);
        pos = 0;
    }
    
}
