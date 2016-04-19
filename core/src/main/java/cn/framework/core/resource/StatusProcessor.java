package cn.framework.core.resource;

import cn.framework.core.utils.Files;
import cn.framework.core.utils.Springs;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * project code
 * package cn.framework.core.resource
 * create at 16/3/18 下午7:39
 *
 * @author wenlai
 */
@Controller("statusProcessor")
@Scope(Springs.SCOPE_SINGLETON)
public class StatusProcessor {


    /**
     * 获取静态资源
     *
     * @param path 文件路径
     *
     * @return
     */
    public byte[] readResource(String path) {

        byte[] result = null;
        result = Files.readResource(path);
        if (result != null) {
            return result;
        }
        result = Files.readBytes(path);
        return result;
    }

}
