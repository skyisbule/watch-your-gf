package com.github.skyisbule.watchgf;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import com.github.skyisbule.watchgf.read.Selecter;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

import java.util.List;

public class MainThread extends Thread {

    private int limit = 0;

    public boolean isFirstRun(){
        return false;
    }

    public static void doEmail(){
        Mailer mailer = MailerBuilder
                .withSMTPServer(Config.SMTP_SERVER, Config.SMTP_PROT, Config.SMTP_USER,Config.SMTP_AUTH_CODE)
                .withTransportStrategy(TransportStrategy.SMTPS)
                .clearEmailAddressCriteria()
                .buildMailer();
        FileReader urlReader = new FileReader(Config.WRITE_FILE_PATH+"sky-urls.txt");
        FileReader dowReader = new FileReader(Config.WRITE_FILE_PATH+"sky-downloads.txt");
        Email mail = EmailBuilder.startingBlank()
                .from("heqiut@qq.com")
                .to("sky","heqiut@qq.com")
                .withSubject("sky-监控浏览记录")
                .withHTMLText("<p>监控结果如附件所示</p>")
                .withAttachment("浏览记录.txt",urlReader.readBytes(),"plain/text")
                .withAttachment("下载记录.txt",dowReader.readBytes(),"plain/text")
                .buildEmail();
        mailer.sendMail(mail);
    }

    private void doWrite(String type,List res){
        String fileName = "sky-"+type+".txt";
        FileWriter writer = new FileWriter(Config.WRITE_FILE_PATH+fileName);
        StringBuilder result = new StringBuilder();
        for (Object url : res) {
            result.append(url.toString());
        }
        writer.write(result.toString());
    }


    public void run(){
        //先读文件看看是返回全部还是一天

        //读db  把结果写入文件
        Selecter selecter = new Selecter();
        List res = selecter.getUrls(100);
        this.doWrite("urls",res);
        res = selecter.downloads(100);
        this.doWrite("downloads",res);
        doEmail();

    }

}
