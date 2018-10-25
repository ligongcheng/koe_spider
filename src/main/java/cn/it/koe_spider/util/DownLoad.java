package cn.it.koe_spider.util;

import cn.it.koe_spider.domain.KoeInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;

public class DownLoad {

    public static boolean downLoadByHttpClient(String url, String filePath) throws IOException {
        FileOutputStream out = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }

            out = new FileOutputStream(file);

            //创建httpclient实例，采用默认的参数配置
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet get = new HttpGet(url);   //使用Get方法提交

            //请求的参数配置，分别设置连接池获取连接的超时时间，连接上服务器的时间，服务器返回数据的时间
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(3000)
                    .setConnectTimeout(3000)
                    .setSocketTimeout(3000)
                    .build();
            //配置信息添加到Get请求中
            get.setConfig(config);
            //通过httpclient的execute提交 请求 ，并用CloseableHttpResponse接受返回信息
            CloseableHttpResponse response = httpClient.execute(get);
            //服务器返回的状态
            int statusCode = response.getStatusLine().getStatusCode();
            //判断返回的状态码是否是200 ，200 代表服务器响应成功，并成功返回信息
            if (statusCode == HttpStatus.SC_OK) {
                IOUtils.copy(response.getEntity().getContent(), out);
                out.flush();
                return true;
            }
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return false;
    }

    public static void waitForDown(ExecutorService threadPool) {
        threadPool.shutdown();
        while (true) {
            if (threadPool.isTerminated()) break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static KoeInfo dlKoe(KoeInfo info) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/");
        String t = simpleDateFormat.format(info.getPublishDate());
        String path = "d:/audio/" + t + info.getType() + "/" + info.getCategory() + "/" + info.getId() + ".mp3";
        File file = new File(path);

        if (file.exists()) {
            file.delete();
        }
        try {
            System.out.println("down:" + info.getId() + " start");
            boolean flag = DownLoad.downLoadByHttpClient(info.getAudio(), path);
            if (flag) {
                info.setDownload(1);
                System.out.println("down:" + info.getId() + " ok");
            } else {
                info.setDownload(2);
                throw new IOException();
            }
        } catch (IOException e) {
            //下载出错，删除错误文件
            info.setDownload(2);
            if (file.exists()) {
                file.delete();
            }
            System.out.println("down:" + info.getId() + " fail");
        }
        return info;
    }

}
