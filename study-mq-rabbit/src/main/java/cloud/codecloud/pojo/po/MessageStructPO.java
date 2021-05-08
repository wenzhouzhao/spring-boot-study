package cloud.codecloud.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试消息体
 *
 * @author zhaoYoung
 * @date 2021/5/8 10:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStructPO implements Serializable {

    private static final long serialVersionUID = 4244532515850821507L;

    private String message;
}
