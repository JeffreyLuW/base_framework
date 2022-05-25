### 项目发布说明：

#### (一) clone源码本地开发:

1. clone项目代码 

2.  cd到digital-ui目录,命令行中执行 

   ```npm
   yarn
   npm run build 
   ```
   
3. 运行digital-admin中DigitalApplication.main(..)即可

#### (二) 开发下发布|digital-deploy:

1. 服务器端部署 /digital-deploy/bin/digital-deploy.jar 
启动命令参考同目录下的readme.md  可以自定义端口。或者参考 start-deploy.bat。
确保服务可以访问

2. cd到项目bin目录,双击package.bat打包jar。 jar包文件在 /digital-admin/target

3.  cd到digital-ui目录,命令行中执行 

   ```npm
   npm run build 
   ```

   打包后的静态资源在/digital-ui/dist  右键dist文件夹zip压缩

4.  使用chrome打开digital-deploy的网址,选择前后端文件(对应xx.jar包和dist.zip)，上传发布即可.
(后端发布页面的停止按钮，是根据端口和当前运行的进程来kill掉进程)
  
   ![演示](./imgs/deploy.gif "演示")


#### (三) 服务器手动copy发布:

1.  cd到项目bin目录,双击package.bat打包jar。 jar包文件在 /digital-admin/target

2.  cd到digital-ui目录,命令行中执行 

   ```npm
   npm run build 
   ```

   打包后的静态资源在/digital-ui/dist

3.  copy对应jar包和dist文件，放到服务器相同目录。启动jar包即可

   

