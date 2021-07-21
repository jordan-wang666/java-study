# Linux 创建 SFTP

### centos7 搭建SFTP

1. 创建sftp组
   查看组信息
```
groupadd sftp
cat /etc/group
```
2. 创建一个sftp用户mysftp并加入到创建的sftp组中，同时修改mysftp用户的密码
```
useradd -g sftp -s /bin/false mysftp 
passwd mysftp
```
3. 新建目录，指定为mysftp用户的主目录
```
mkdir -p /sftp/mysftp
usermod -d /sftp/mysftp mysftp
```
4. 编辑配置文件/etc/ssh/sshd_config
   将如下这行用#符号注释掉
```
# Subsystem      sftp    /usr/libexec/openssh/sftp-server
```
并在文件最后面添加如下几行内容然后保存（最好放在文件末尾）
```
Subsystem       sftp    internal-sftp   
Match Group sftp  
ChrootDirectory /sftp/%u    
ForceCommand    internal-sftp    
AllowTcpForwarding no   
X11Forwarding no 
```
5. 设置Chroot目录权限
```
chown root:sftp /sftp/mysftp  #文件夹所有者必须为root，用户组可以不是root
chmod 755 /sftp/mysftp   #权限不能超过755，否则会导致登录报错，可以是755
```
6. 新建一个目录供stp用户mysftp上传文件，这个目录所有者为mysftp所有组为sftp，所有者有写入权限所有组无写入权限
```
mkdir /sftp/mysftp/upload  
chown mysftp:sftp /sftp/mysftp/upload  
chmod 755 /sftp/mysftp/upload 
```
7. 关闭selinux并重启sshd服务，然后测试
```
setenforce 0
service sshd restart
```
8.在其他服务器上进行验证,sftp 用户名@ip地址
```
sftp -P 10001 mysftp@127.0.0.1 
```

 查看当前系统版本
```
cat /proc/version
```
#### 查看centos版本
```
cat  /etc/redhat-release
```
