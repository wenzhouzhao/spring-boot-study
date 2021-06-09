package cloud.codecloud.hash;

import cn.hutool.core.date.DateUtil;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * 博客网站案例
 *
 * @author zhaoYoung
 * @date 2021/6/9 10:40
 */
public class BlogTest {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 获取博客id
     */
    public long getBlogId() {
        return jedis.incr("blog_id_counter");
    }

    /**
     * 发表一篇博客
     */
    public boolean publishBlog(long id, Map<String, String> blog) {
        if (jedis.hexists("blog::" + id, "title")) {
            return false;
        }
        blog.put("content_length", String.valueOf(blog.get("content").length()));

        jedis.hmset("blog::" + id, blog);

        return true;
    }

    /**
     * 查看一篇博客
     */
    public Map<String, String> findBlogById(long id) {
        Map<String, String> blog = jedis.hgetAll("blog::" + id);
        incrementBlogViewCount(id);
        return blog;
    }

    /**
     * 更新一篇博客
     */
    public void updateBlog(long id, Map<String, String> updatedBlog) {
        String updatedContent = updatedBlog.get("content");
        if (updatedContent != null && !"".equals(updatedContent)) {
            updatedBlog.put("content_length", String.valueOf(updatedContent.length()));
        }

        jedis.hmset("blog::" + id, updatedBlog);
    }

    /**
     * 对博客进行点赞
     */
    public void incrementBlogLikeCount(long id) {
        jedis.hincrBy("blog::" + id, "like_count", 1);
    }

    /**
     * 增加博客浏览次数
     */
    public void incrementBlogViewCount(long id) {
        jedis.hincrBy("blog::" + id, "view_count", 1);
    }

    public static void main(String[] args) {
        BlogTest demo = new BlogTest();

        // 发表一篇博客
        long id = demo.getBlogId();

        Map<String, String> blog = new HashMap<>(8);
        blog.put("id", String.valueOf(id));
        blog.put("title", "I like learning Redis !");
        blog.put("content", "Learning Redis is a very happy thing !");
        blog.put("author", "zhaoYoung");
        blog.put("time", DateUtil.now());

        demo.publishBlog(id, blog);

        // 更新一篇博客
        Map<String, String> updatedBlog = new HashMap<>(4);
        updatedBlog.put("title", "I especially like learning Redis !");
        updatedBlog.put("content", "I usually like to learn Redis on the official website.");
        updatedBlog.put("time", DateUtil.now());
        demo.updateBlog(id, updatedBlog);

        // 有别人点击进去查看你的博客的详细内容，并且进行点赞
        Map<String, String> blogResult = demo.findBlogById(id);
        System.out.println("查看博客的详细内容：" + blogResult);
        demo.incrementBlogLikeCount(id);

        // 你自己去查看自己的博客，看看浏览次数和点赞次数
        blogResult = demo.findBlogById(id);
        System.out.println("自己查看博客的详细内容：" + blogResult);
    }
}
