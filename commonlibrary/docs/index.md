# APP公共代码整理

类|简要说明
--|------
helper.XLogger|日志类，自动保存日志到缓存文件
helper.CrashHandler|全局异常捕获和处理
helper.TimeProvider|使用NTP协议同步网络时间
helper.PermissionManager|权限检查和申请<font color=red>（未完善）</font>
helper.RetryPool|重试池，直到主动停止或达成条件
network.OkHttpUtil|网络库OkHttp封装，提供全局OkHttpClient<font color=red>（未完善）</font>
widget.XToast|支持在非UI线程中弹出Toast提示
widget.XTextView|支持监听点击Drawable事件的TextView
widget.XViewPageIndicator|轮播图指示器
util.DeviceInfoUtils|获取设备DeviceId,SIM卡序列号，AndroidId等
util.DateUtils|日期和时间格式化和转换
util.MD5Utils|计算给定数据的MD5值
util.SignUtils|获取当前APP的签名信息
util.AppUtils|启动，安装APP
util.AESCipherUtils|AES加密工具