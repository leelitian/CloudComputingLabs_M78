package client;

import okhttp3.*;

import java.io.IOException;

/**
 * @author csqure
 * 这是一个基于OKhttp的客户端，用来测试对服务器的HTTP连接请求
 */
public class MyOkHttpClient {
    /**唯一一个客户端对象，使用它来进行发送HTTP请求*/
    final static OkHttpClient client=new OkHttpClient();

    /**
     * 这是GET方法的调用
     * @param url 所输入的URL地址
     * */
    public static void GetMethod(String url){
        /**构建HTTP请求，默认为GET方法
         * */
        Request request=new Request.Builder()
                .url(url)
                .build();
        /**调用准备
         * */
        Call call=client.newCall(request);
        try {
            /**执行并返回结果
             * */
            Response response=call.execute();
            /**返回的是回应头和回应体*/
            System.out.println(response.headers()+"\n"+response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**这是post方法
     * @param url 要请求的网址
     * @param name 用户名字
     * @param id 用户id*/
    public static void PostMethod(String url,String name,String id){
        /**POST方法要构造请求体，使用这种简单的方法就行
         * .add(key,value)
         * 这代表着xxxxx.com/?key=value
         * */
        RequestBody formBody = new FormBody.Builder()
                .add("Name", name)
                .add("ID", id)
                .build();
        /**这是对请求的构造
         * .post(RequestBody)构造了POST方法
         * */
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        try {
            Response response=call.execute();
            /**返回的是回应头和回应体*/
            System.out.println(response.headers()+"\n"+response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void PutMethod(String url){
        FormBody body = new FormBody.Builder()
                .add("communityId", String.valueOf(123))
                .build();
        /**构建HTTP请求，默认为GET方法
         * */
        Request request=new Request.Builder()
                .url(url)
                .put(body)
                .build();
        /**调用准备
         * */
        Call call=client.newCall(request);
        try {
            /**执行并返回结果
             * */
            Response response=call.execute();
            /**返回的是回应头和回应体*/
            System.out.println(response.message()+"\n"+response.headers()+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

//        GetMethod("http://127.0.0.1:7397");
        PostMethod("http://127.0.0.1:7397/Post_show","HNU","cs1024");
//        PutMethod("http://127.0.0.1:7397");

//        ExecutorService pool = Executors.newFixedThreadPool(6);
//        Runnable myThread = () -> {
//            for (int i = 0; i < 100; i++) {
//                System.out.println(i);
//                GetMethod("http://www.baidu.com");
//            }
//        };
//        // 向线程池中提交两个线程
//        pool.submit(myThread);
//        pool.submit(myThread);
//        // 关闭线程池
//        pool.shutdown();
    }
}
