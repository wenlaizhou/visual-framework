package cn.framework.cache.resource;

import org.springframework.stereotype.Service;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/4/6 下午6:32
 *
 * @author wenlai
 */
@Pattern("wenlai")
@Service("subTest")
public class SubTest implements EventSubscriber {

    @Override
    public boolean subscriber(CachedEvent event) {

        System.out.println("here");
        System.out.println(event);

        return false;
    }
}
