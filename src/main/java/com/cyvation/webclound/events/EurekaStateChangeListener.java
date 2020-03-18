package com.cyvation.webclound.events;

import com.cyvation.webclound.mail.MailService;
import com.netflix.appinfo.InstanceInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * eureka状态事件监听
 */
@Component
public class EurekaStateChangeListener {

    private static Logger LOGGER = LoggerFactory.getLogger(EurekaStateChangeListener.class);

    private MailService mailService;

    @Autowired
    public EurekaStateChangeListener(MailService mailService){
        this.mailService = mailService;
    }

    private Map<String, String> serverList = new ConcurrentHashMap<>();

    @EventListener
    public void listen(EurekaInstanceCanceledEvent event) {
        String appName = event.getAppName();
        String serverId = event.getServerId();
        String serverKey = appName + "." + serverId;
        String oldStatus = serverList.get(serverKey);

        if (StringUtils.isBlank(oldStatus)) {
            oldStatus = "";
        }
        if (!oldStatus.equals("OUT_OF_SERVICE")) {
            serverList.put(serverKey, "OUT_OF_SERVICE");

            long timeStamp = System.currentTimeMillis();
            String title = "[" + appName + "] Eureka 服务下线通知";
            String msg = getMessage("已下线", appName, serverId, "OUT_OF_SERVICE", timeStamp);
            try {
                mailService.sendTextMail(title, msg);
            }catch (Exception e){
                LOGGER.error("发送邮件失败，邮件消息:" + msg,e);
            }
        }
    }

    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        if (instanceInfo == null) {
            return;
        }
        if (instanceInfo.getStatus() != null && instanceInfo.getStatus() != InstanceInfo.InstanceStatus.UP && instanceInfo.getStatus() != InstanceInfo.InstanceStatus.STARTING) {
            return;
        }
        String appName = instanceInfo.getAppName();
        String serverId = instanceInfo.getId();
        String serverKey = appName + "." + serverId;
        String oldStatus = serverList.get(serverKey);

        serverList.put(serverKey, "UP");
        long timeStamp = System.currentTimeMillis();
        if (StringUtils.isBlank(oldStatus)) {
            String title = "[" + appName + "] Eureka 服务上线通知";
            String msg = getMessage("已上线", appName, serverId, "UP", timeStamp);
            try {
                mailService.sendTextMail(title, msg);
            }catch (Exception e){
                LOGGER.error("发送邮件失败，邮件消息:" + msg,e);
            }
        } else if (!oldStatus.equals("UP")) {
            String title = "[" + appName + "] Eureka 服务重新上线通知";
            String msg = getMessage("已重新上线", appName, serverId, "UP", timeStamp);
            try {
                mailService.sendTextMail(title, msg);
            }catch (Exception e){
                LOGGER.error("发送邮件失败，邮件消息:" + msg,e);
            }
        }
    }

    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        if (instanceInfo == null) {
            return;
        }
        InstanceInfo.InstanceStatus status = instanceInfo.getStatus();
        String appName = event.getAppName();
        String serverId = event.getServerId();
        String serverKey = appName + "." + serverId;
        String oldStatus = serverList.get(serverKey);
        String currentStatus = status.toString();

        if (currentStatus.equals(oldStatus)) {
            return;
        }
        if (StringUtils.isBlank(oldStatus)) {
            oldStatus = "";
        }

        boolean isDown = true;
        if (oldStatus.equals("UP") || oldStatus.equals("STARTING")) {
            isDown = false;
        }

        serverList.put(serverKey, status.toString());
        long timeStamp = System.currentTimeMillis();
        if (status == InstanceInfo.InstanceStatus.UP || status == InstanceInfo.InstanceStatus.STARTING) {
            if (isDown) {
                String title = "[" + appName + "] Eureka 服务恢复通知";
                String msg = getMessage("已恢复", appName, serverId, status.toString(), timeStamp);
                try {
                    mailService.sendTextMail(title, msg);
                }catch (Exception e){
                    LOGGER.error("发送邮件失败，邮件消息:" + msg,e);
                }
            }
        } else {
            if (!isDown) {
                String title = "[" + appName + "] Eureka 服务离线通知";
                String msg = getMessage("已离线", appName, serverId, status.toString(), timeStamp);
                try {
                    mailService.sendTextMail(title, msg);
                }catch (Exception e){
                    LOGGER.error("发送邮件失败，邮件消息:" + msg,e);
                }
            }
        }
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
        //HH: 注册中心启动事件
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.S");
        System.out.println(currentDate.format(System.currentTimeMillis()) + "  INFO " + "注册中心启动");

        //sendEmail("EurekaServer 注册中心启动", "EurekaServer 注册中心已启动");
    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        // HH: Server 启动事件
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.S");
        System.out.println(currentDate.format(System.currentTimeMillis()) + "  INFO " + "EurekaServer 启动");
        //sendEmail("EurekaServer 服务启动", "EurekaServer Started");
    }

    /**
     * 获取消息
     * @param content
     * @param appName
     * @param serverId
     * @param status
     * @param timeStamp
     * @return
     */
    private String getMessage(String content, String appName, String serverId, String status, long timeStamp) {
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.S");
        String message = "微服务：" + appName + " [ " + serverId + " ] " + content + " " + status
                + "\r\n时间戳：" + currentDate.format(timeStamp) + " [" + timeStamp + "]";
        return message;
    }
}
