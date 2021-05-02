package cloud.codecloud.util;

import org.dozer.Mapper;
import org.dozer.DozerBeanMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * bean转换工具类
 *
 * @author zhaoYoung
 * @date 2021/4/26 23:08
 */
public class BeanUtil {

    /**
     * 避免重复创建DozerMapper消耗资源.
     */
    private static Mapper dozer = new DozerBeanMapper();

    /**
     * 转换对象的类型
     *
     * @param source
     * @param destinationClass
     * @param <T>
     * @return
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }

        return dozer.map(source, destinationClass);
    }

    /**
     * 转换Collection中对象的类型.
     */
    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        if (sourceList == null) {
            return null;
        }
        List<T> destinationList = new ArrayList<>();
        for (Object sourceObject : sourceList) {
            T destinationObject = dozer.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    /**
     * 将对象A的值拷贝到对象B中.
     */
    public static void copy(Object source, Object destinationObject) {
        if (source != null) {
            dozer.map(source, destinationObject);
        }
    }
}
