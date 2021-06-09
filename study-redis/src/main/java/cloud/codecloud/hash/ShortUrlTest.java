package cloud.codecloud.hash;

import cn.hutool.crypto.SecureUtil;
import redis.clients.jedis.Jedis;

/**
 * 短网址追踪案例
 *
 * @author zhaoYoung
 * @date 2021/6/9 10:58
 */
public class ShortUrlTest {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 获取短连接网址
     *
     * @param url
     * @return
     */
    public String getShortUrl(String url) {
        String shortUrl = shortUrl(url)[0];

        jedis.hset("short_url_access_count", shortUrl, "0");
        jedis.hset("url_mapping", shortUrl, url);

        return shortUrl;
    }

    /**
     * 给短连接地址进行访问次数的增长
     *
     * @param shortUrl
     */
    public void incrementShortUrlAccessCount(String shortUrl) {
        jedis.hincrBy("short_url_access_count", shortUrl, 1);
    }

    /**
     * 获取短连接地址的访问次数
     *
     * @param shortUrl
     */
    public long getShortUrlAccessCount(String shortUrl) {
        return Long.valueOf(jedis.hget("short_url_access_count", shortUrl));
    }

    public String[] shortUrl(String url) {
        String firstUrl = "https://www.codecloud.cloud/";
        url = firstUrl + url;
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "CodeCloud";
        // 要使用生成 URL 的字符
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h",
                "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
                "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y", "Z"

        };
        // 对传入网址进行 MD5 加密
        String hex = SecureUtil.md5(key + url);

        String[] resUrl = new String[4];
        for (int i = 0; i < 4; i++) {

            // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);

            // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
            StringBuffer outChars = new StringBuffer();
            for (int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                long index = 0x0000003D & lHexLong;
                // 把取得的字符相加
                outChars.append(chars[(int) index]);
                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }
            // 把字符串存入对应索引的输出数组
            resUrl[i] = outChars.toString();
        }
        return resUrl;
    }

    public static void main(String[] args) {
        ShortUrlTest demo = new ShortUrlTest();

        String shortUrl = demo.getShortUrl("https://www.codecloud.cloud");
        System.out.println("页面上展示的短链接地址为：" + shortUrl);

        for (int i = 0; i < 100; i++) {
            demo.incrementShortUrlAccessCount(shortUrl);
        }

        long accessCount = demo.getShortUrlAccessCount(shortUrl);
        System.out.println("短链接被访问的次数为：" + accessCount);
    }

}
