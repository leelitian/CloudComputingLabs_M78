package client;

import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.io.PrintWriter; 
import java.net.HttpURLConnection; 
import java.net.URL; 

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class httpclient { 
    //发送一个GET请求 
    public static String get(String path) throws Exception{ 
        HttpURLConnection httpConn=null; 
        BufferedReader in=null; 
        try { 
            URL url=new URL(path); 
            httpConn=(HttpURLConnection)url.openConnection(); 
            //这里只是对服务器的测试，所以不需要判断getResponseCode()的结果
            System.out.println(httpConn.getResponseCode());
//          if(httpConn.getResponseCode()==HttpURLConnection.HTTP_OK){ 
//          StringBuffer content=new StringBuffer(); 
//          String tempStr=""; 
//          in=new BufferedReader(new InputStreamReader(httpConn.getInputStream())); 
//          while((tempStr=in.readLine())!=null){ 
//              content.append(tempStr); 
//          } 
//          return content.toString(); 
//      }else{ 
//          throw new Exception("请求出现了问题!"); 
//      } 

        } catch (IOException e) { 
            e.printStackTrace(); 
        }finally{ 
            httpConn.disconnect(); 
        } 
        return null;
    } 
    //发送一个GET请求,参数形式key1=value1&key2=value2... 
    public static String post(String path,String params) throws Exception{ 
        HttpURLConnection httpConn=null; 
        PrintWriter out=null; 
        BufferedReader in=null; 
        try { 
            URL url=new URL(path); 
            httpConn=(HttpURLConnection)url.openConnection(); 
            httpConn.setRequestMethod("POST"); 
            httpConn.setDoOutput(true); 
               
            //发送post请求参数 
            out=new PrintWriter(httpConn.getOutputStream()); 
            out.println(params); 
            out.flush(); 
            //httpConn.connect();  
            //读取响应 
            System.out.println(httpConn.getResponseCode());
//            if(httpConn.getResponseCode()==HttpURLConnection.HTTP_OK){ 
//                StringBuffer content=new StringBuffer(); 
//                String tempStr=""; 
//                in=new BufferedReader(new InputStreamReader(httpConn.getInputStream())); 
//                while((tempStr=in.readLine())!=null){ 
//                    content.append(tempStr); 
//                } 
//                return content.toString(); 
//            }else{ 
//                throw new Exception("请求出现了问题!"); 
//            } 
        } catch (IOException e) { 
            e.printStackTrace(); 
        }finally{
        	out.close();
            httpConn.disconnect(); 
        } 
        return null;
    } 
       
    public static void main(String[] args) throws Exception {
    	
    	Scanner sc = new Scanner(System.in); 
    	System.out.println("请选择方法 1.get 2.post"); 
    	int method = sc.nextInt(); 
    	System.out.println("请输入同时发送的线程数"); 
    	int cnt = sc.nextInt(); 
    	ExecutorService pool = Executors.newFixedThreadPool(cnt);
    	
    	//第一个线程对应 get方法
    	Runnable myThread1 = () -> {
            for (int i = 0; i < 100; i++) {
                try {
					String msg =httpclient.get("http://localhost:8999/index.html");
					//System.out.println(msg); 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            }
        };
      //第二个线程对应 post方法
        Runnable myThread2 = () -> {
            for (int i = 0; i < 100; i++) {
                try {
                	String msg =httpclient.post("http://localhost:8999/Post_show", "Name=HNU&ID=2020");
                	//System.out.println(msg); 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            }
        };
        
        //判断使用get方法还是post方法
    	if(method == 1){
    		
    		for(int i=0; i<cnt; i++) {
    			pool.submit(myThread1);
    		}
    		
    	}
    	else if(method == 2){
    		for(int i=0; i<cnt; i++) {
    			pool.submit(myThread2);
    		}
    	}
    	
     
      pool.shutdown();
        
    } 
   
}