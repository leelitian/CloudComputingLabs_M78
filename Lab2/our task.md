#### 任务

编写一个处理HTTP Request的多线程HTTP Server

#### 多线程的方式

线程池/来一个开一个

#### 命令行开启服务器

3个可变参数

#### 处理来自client的请求报文

请求报文：只看请求行，首部行和实体都不用管（但是需要区分开，否则可能会和下一个请求行混淆）

接收报文：首部行可以是任意行（也可以没有）

**1. 处理GET方法**

* **GET 127.0.0.0.1:8888/.../index.html HTTP1.1（处理目录文件）**

  返回**HTTP/1.1 200 OK**，实体内容是html的文件内容

* **GET 127.0.0.0.1:8888/.../...（处理目录）**

  返回**HTTP/1.1 200 OK**，和目录中的index.html

* 若文件不存在

  返回**HTTP/1.1 404 Not Found**，实体内容可以为空

**2. 处理POST方法**

必须有以下形式

```
POST http://127.0.0.1:8888/Post_show HTTP1.1

Name=xxxx&ID=xxxxxx
```

若不是：则返回404

**3. 不是GET或POST**

返回HTTP/1.1 501 Not Inplemented

#### 完成测试报告

**1. 计算每秒能处理的请求数（不同核数）**

**2. 计算每秒能处理的请求数（客户端数量）**

因此需要编写一个发送request的代码

#### 其它

使用命令行运行java程序

编写java的makefile

#### 安装环境

**Linux下curl的安装**

https://www.cnblogs.com/suidouya/p/7387861.html

**Windows下curl的安装**

安装包我发群里了

**Linux下java jdk的安装**

把安装包放到主文件夹下，随后执行以下命令：

```bash
mkdir java
cd java
tar -xzvf ../jdk-8u161-linux-x64.tar.gz
```

然后配置环境变量，用`sudo gedit ~/.bashrc`编辑文件，在最后加上以下内容：

**注意将leelitian改为你自己的用户名！！！**

```bash
export JAVA_HOME=/home/leelitian/java/jdk1.8.0_161  
export JRE_HOME=${JAVA_HOME}/jre  
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib  
export PATH=${JAVA_HOME}/bin:$PATH
```

保存文件并退出，重启命令行，输入`java -version`，如果显示以下内容则成功了：

```bash
java version "1.8.0_161"
Java(TM) SE Runtime Environment (build 1.8.0_161-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.161-b12, mixed mode)
```

