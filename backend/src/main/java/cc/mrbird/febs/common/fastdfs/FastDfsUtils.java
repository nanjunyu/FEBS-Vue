package cc.mrbird.febs.common.fastdfs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author Frank
 * @Date Create in  2019/5/24 16:31
 */
@PropertySource("classpath:fastDfs-client.properties")
public class FastDfsUtils {
    @Value("${trackerServer}")
    public static  String trackerServer;

}
