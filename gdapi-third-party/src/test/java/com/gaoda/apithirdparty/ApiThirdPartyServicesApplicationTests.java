package com.gaoda.apithirdparty;

import cn.hutool.json.JSONUtil;
import com.gaoda.apithirdparty.config.QQEmailConfig;
import com.gaoda.apithirdparty.utils.RandomUtil;
import com.gaoda.apithirdparty.utils.SendMessageOperation;
import com.gundam.gdapicommon.model.entity.User;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.gundam.gdapicommon.constant.RedisConstant.LOGINCODEPRE;

@SpringBootTest
class ApiThirdPartyServicesApplicationTests {

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;


    @Test
    void contextLoads() {
        String userId = (String) redisTemplate.opsForValue().get("api:login:user:gaoda85d337fd-7f44-4ab3-aa45-f6ac9732172c1");

        System.out.println(userId);
    }


    @Test
    public void testMail() {
        SimpleEmail mail=new SimpleEmail();
        try {
            // 设置邮箱服务器信息
            mail.setSslSmtpPort(QQEmailConfig.PORT);
            mail.setHostName(QQEmailConfig.HOST);
            // 设置密码验证器
            mail.setAuthentication(QQEmailConfig.EMAIL, QQEmailConfig.PASSWORD);
            // 设置邮件发送者
            mail.setFrom(QQEmailConfig.EMAIL);
            // 设置邮件接收者
            mail.addTo("474696256@qq.com");
            // 设置邮件编码
            mail.setCharset("UTF-8");
            // 设置邮件主题
            mail.setSubject("Chen API");

            String code = RandomUtil.getFourBitRandom();
            //设置数据的5分钟有效期限
//            redisTemplate.opsForValue().set(LOGINCODEPRE+"474696256@qq.com",code,5, TimeUnit.MINUTES);
            // 设置邮件内容
            mail.setMsg("您的注册 or 登录 验证码为："+code+",验证码5分钟内有效!!!"+"[Chen API]");
            // 设置邮件发送时间
            mail.setSentDate(new Date());
            // 发送邮件
            mail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

}