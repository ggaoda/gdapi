package com.gundam.gdapi.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

public class QiniuUtils {

    private static String accessKey = "dRb1JFMdCdlZp3kD07EyTe_CirqffPJIG1nGCu6t";
    private static String secretKey = "WUPfPsttzxbiVcTSQ_L7sfV-cUD1j4g25902JopT";
    private static String bucketName = "gdapi";
    private static String path = "http://api.ggaoda.cn";

    private static final String customSuffix = ".png";//定义图片保存后的后缀


    /**
     * 上传图片到七牛云
     * @param file 图片
     * @return 返回图片存储后的新图片名
     * @throws Exception
     */
    public static Object QiniuCloudUploadImage(MultipartFile file) throws Exception{
        if(file.isEmpty()) {
            return "文件为空";
        }else if(file.getSize() > 1024*1024*10){
            return "文件大于10M";
        }
        //获取图片后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //允许上传的图片格式集合
        String[] suffixes = new String[]{".bmp", ".jpeg", ".jpg", ".png"};
        boolean bool = false;
        //判断格式是否在suffixes中
        for(String string : suffixes){
            if (string.equals(suffix)){
                bool = true;
                break;
            }
        }
        if(!bool){
            return "图片格式错误";
        }
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;//指定分片上传版本
        UploadManager uploadManager = new UploadManager(cfg);
        //生成上传凭证，然后准备上传
        byte[] bytes = file.getBytes();
        String imageName = DigestUtils.md5DigestAsHex(bytes);//将图片md5的值作为图片名，避免重复图片浪费空间
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = imageName + customSuffix;//图片保存到七牛云后的文件名

        try {
            byte[] uploadBytes = file.getBytes();
            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucketName);

            try {
                uploadManager.put(byteInputStream,key,upToken,null, null);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println("七牛云ERROR:" + r.toString());
                try {
                    System.err.println("七牛云ERROR:" + r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (UnsupportedEncodingException ex) {
            //ignore
        }

        return key;
    }
}