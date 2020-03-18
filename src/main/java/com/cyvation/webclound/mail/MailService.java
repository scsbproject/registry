/**
 * projectName: webclound
 * fileName: MailService.java
 * packageName: com.cyvation.webclound.mail
 * date: 2019-10-29 12:04
 * copyright(c) 2017-2020 同方赛威讯信息技术公司
 */
package com.cyvation.webclound.mail;

/**
 * @version: V1.0
 * @author: 代浩然
 * @interfaceName: MailService
 * @packageName: com.cyvation.webclound.mail
 * @description: 邮件服务接口
 * @data: 2019-10-29 12:04
 **/
public interface MailService {

    /**
     * 发送文本邮件
     * @param subject 邮件主题
     * @param text 邮件的文本内容
     * @throws Exception
     */
    void sendTextMail(String subject,String text)throws Exception;

}