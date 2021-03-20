
package cn.jj.base.common.cache;

import java.io.File;
import java.io.InputStream;

public interface ICacheManager {

    Object get(String key, CacheType type);

    InputStream getStream(String key, CacheType type);

    File getCachedFile(String key, CacheType type);

    void put(String key, Object o, CacheType type);

    void put(String key, Object o, long expired, CacheType type);

    void delete(String key, CacheType type);

    boolean clear();

    boolean contains(String key, CacheType type);

    void destroy();

    enum CacheType {DATA, IMAGE}

}
