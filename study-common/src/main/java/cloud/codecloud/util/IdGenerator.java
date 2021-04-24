package cloud.codecloud.util;

import lombok.extern.slf4j.Slf4j;

/**
 基于Snowflake算法的ID操作工具类，53位long类型的ID, 兼容js支持的最大长度为16位的number类型
 * <pre>
 * 当前前端js支持的number类型的最大值为2的53次方，为9007199254740992。如果超过这个值，那么js会出现不精确的问题，故只生成53位的ID。
 * 使用 Snowflake 算法生成的 53 位 long 类型的 ID，结构如下:
 * 0 - 0000000000 0000000000 0000000000 000000000 - 00000 - 00000000
 * 1) 01 位标识，由于 long 在 Java 中是有符号的，最高位是符号位，正数是 0，负数是 1，ID 一般使用正数，所以最高位是 0
 * 2) 39 位时间截(毫秒级)，注意，时间截不是存储当前时间的时间截，而是存储时间截的差值(当前时间 - 开始时间)得到的值，
 *       开始时间截，一般是业务开始的时间，由我们程序来指定，如 SnowflakeIdWorker 中的 START_TIMESTAMP 属性。
 *       39 位的时间截，可以使用 17 年: (2^39)/(1000*60*60*24*365) = 17.43 年
 * 3) 5 位的数据机器位，可以部署在 32 个节点
 * 4) 8 位序列，毫秒内的计数，计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生 256 个 ID 序号
 *
 * 特性：
 * 1）整体上按照时间自增排序，并且整个分布式系统内不会产生 ID 碰撞(由机器 ID 作区分)；
 * 2）最多支持 32 台机器，每台机器每毫秒能够生成最多 256 个 ID，整个集群理论上每秒可以生成 32 * 1000 * 256 = 800万 个ID；
 *
 * @author zhaoYoung
 * @date 2021/4/24 16:53
 */
@Slf4j
public class IdGenerator {

    private static IdGenerator INSTANCE = new IdGenerator();
    /**
     * 开始时间戳 (2019-01-01)
     */
    private final static long START_STAMP = 1546272000000L;

    /**
     * 机器 ID 所占的位数
     */
    private static final long MACHINE_ID_BITS = 5L;

    /**
     * 序列在 ID 中占的位数
     */
    private static final long SEQUENCE_BITS = 8L;

    /**
     * 机器 ID 向左移 8 位
     */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 时间截向左移 13 位(8+5)
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    /**
     * 生成序列的掩码，这里为 256(0B11111111=0xFF=256)
     */
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    /**
     * 工作机器 ID(0~31)
     */
    private long machineId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成 ID 的时间截
     */
    private long lastTimestamp = -1L;

    /**
     * 获取uuid的工具方法
     *
     * @return
     */
    public static long generateId() {
        return INSTANCE.nextId();
    }

    /**
     * 使用工作机器的序号，范围是 [0, 31]
     */
    private IdGenerator() {
        this.machineId = 31;
    }

    /**
     * 获得下一个 ID(该方法是线程安全的)
     *
     * @return long 类型的 ID
     */
    private synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String
                    .format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;

            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成 ID 的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成 53 位的 ID
        return ((timestamp - START_STAMP) << TIMESTAMP_LEFT_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成 ID 的时间截
     * @return 当前时间戳(毫秒)
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }

        return timestamp;
    }

    /**
     * 返回当前时间，以毫秒为单位
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
