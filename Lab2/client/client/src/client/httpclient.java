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
    //����һ��GET���� 
    public static String get(String path) throws Exception{ 
        HttpURLConnection httpConn=null; 
        BufferedReader in=null; 
        try { 
            URL url=new URL(path); 
            httpConn=(HttpURLConnection)url.openConnection(); 
            //����ֻ�ǶԷ������Ĳ��ԣ����Բ���Ҫ�ж�getResponseCode()�Ľ��
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
//          throw new Exception("�������������!"); 
//      } 

        } catch (IOException e) { 
            e.printStackTrace(); 
        }finally{ 
            httpConn.disconnect(); 
        } 
        return null;
    } 
    //����һ��GET����,������ʽkey1=value1&key2=value2... 
    public static String post(String path,String params) throws Exception{ 
        HttpURLConnection httpConn=null; 
        PrintWriter out=null; 
        BufferedReader in=null; 
        try { 
            URL url=new URL(path); 
            httpConn=(HttpURLConnection)url.openConnection(); 
            httpConn.setRequestMethod("POST"); 
            httpConn.setDoOutput(true); 
               
            //����post������� 
            out=new PrintWriter(httpConn.getOutputStream()); 
            out.println(params); 
            out.flush(); 
            //httpConn.connect();  
            //��ȡ��Ӧ 
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
//                throw new Exception("�������������!"); 
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
    	System.out.println("��ѡ�񷽷� 1.get 2.post"); 
    	int method = sc.nextInt(); 
    	System.out.println("������ͬʱ���͵��߳���"); 
    	int cnt = sc.nextInt(); 
    	ExecutorService pool = Executors.newFixedThreadPool(cnt);
    	
    	//��һ���̶߳�Ӧ get����
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
      //�ڶ����̶߳�Ӧ post����
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
        
        //�ж�ʹ��get��������post����
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