package cloud.codecloud;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RateLimitRedisApplicationTests {

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void unrestricted() throws Exception {
        MvcResult result;
        StringBuilder stringBuilder0 = new StringBuilder();
        System.out.println("\r\n");
        for (int i = 0; i < 5; i++) {
            result = mockMvc.perform(MockMvcRequestBuilders.get("/unrestricted"))
                    .andReturn();
            stringBuilder0.append("未被限流接口第").append(i+1).append("次调用：").append(result.getResponse().getContentAsString()).append("\n");
        }
        System.out.println(stringBuilder0);
        System.out.println("\r\n");

        System.out.println("\r\n");
        StringBuilder stringBuilder1 = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            result = mockMvc.perform(MockMvcRequestBuilders.get("/rateLimitTest1"))
                    .andReturn();
            stringBuilder1.append("被限流接口1第").append(i+1).append("次调用：").append(result.getResponse().getContentAsString()).append("\n");
        }
        System.out.println(stringBuilder1);
        System.out.println("\r\n");

        System.out.println("\r\n");
        StringBuilder stringBuilder2 = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            result = mockMvc.perform(MockMvcRequestBuilders.get("/rateLimitTest2"))
                    .andReturn();
            stringBuilder2.append("被限流接口2第").append(i+1).append("次调用：").append(result.getResponse().getContentAsString()).append("\n");
        }
        System.out.println(stringBuilder2);
        System.out.println("\r\n");
    }

}
