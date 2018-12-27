## modules简介
项目在进行组件化改造中，不可避免的会遇到模块之间的解耦与组件模块之间的服务调用。针对以上问题，本框架提供了一套低侵入、轻便简洁的接入方式。框架通过注解来标识暴露的服务，以确保框架对原代码的低侵入。模块间的服务调用通过调用框架生成的中间类来完成调用，最大程度降低了模块间的耦合

## demo
![image](image/demo.gif)

## 技术原理系列文章
[一种低侵入性的组件化方案之APP组件化简介](https://juejin.im/post/5bc592b1f265da0ace21600b)

[一种低侵入性的组件化方案 之 组件化需要考虑的几个问题](https://juejin.im/post/5bc6fcb36fb9a05cd45713ab)

[一种低侵入性的组件化方案 之 传统组件化方案的问题](https://juejin.im/post/5bc70291e51d450e76336a1f)

[一种简单的低侵入性的组件化方案](https://juejin.im/post/5bc70550e51d450e827b9ca7)


## 一、集成步骤:
### 1、集成plugin
在project的build.gradle文件中添加依赖和属性配置：
```
buildscript {

    repositories {
        jcenter()
        //...

    }
    dependencies {
        classpath 'com.beyondxia.modules:transform-plugin:1.1.1'
        //...

    }
}
```

并在app module的build.gradle 件中引入该插件：
```
apply plugin: 'com.beyondxia.modules.plugin'
```

### 2、新建module
在项目根目录中新建一个名为modules_services_api（工程名要命名为modules_services_api）的android library性质的子module（用来放置生成的公共源码），并在该module的build.gradle文件中添加如下依赖和属性配置：
```
api 'com.beyondxia.modules:api:1.0.2'
```
其他需要组件化业务module均需要依赖此module

### 3、添加annotationProcessor
在需要接入的业务module中添加如下annotationProcessor依赖
```
annotationProcessor 'com.beyondxia.modules:compiler:1.1.1'
```
至此，已完成所有的配置工作

## 二、使用步骤:
### 一、初始化
在Application类的onCreate中加入: 
```
ServiceHelper.init(this);
```
### 二、module向外暴露服务:
以下是一个典型的对外提供的服务的类
```
@ExportService(moduleName = "business1")
public class Login implements ILifeCycle{

    @ExportMethod
    public boolean doLogin(Context context, String userName, String password) {
        //...
    }

    @ExportMethod
    public String getUserName() {
        //...
    }

    // use BCDictionary to export model
    @ExportMethod
    public BCDictionary getUserInfo() {
        //...
    }

    @ExportMethod
    public void nav2LoginActivity(Context context) {
        //...
    }

    @Override
    public void onInstalled() {

    }

    @Override
    public void onUninstalled() {
    	// useless now
    }
}
```
说明:
a、ExportService:标识对外提供服务的类 
参数介绍:moduleName，本服务往外暴露的模块名，不设置默认为空，一个模块内的所有服务类建议相同，但需保证与其他模块的互异性。

preLoad，是否预加载该服务类，默认为false。设置为true时，则在启动app的时候则会实例化该服务类， 否则在使用的时候再实例化，可根据当前的模块特性进行灵活配置。

b、ExportMethod:标识本服务对外提供的方法，在对外提供的服务类中，只有被ExportMethod标记的方法才可以被其他模块所访问。
注:该注解只能标识在为public的成员方法上，否则会抛出异常，终止构建。 

c、ILifeCycle:服务类的生命周期，是否实现该接口为可选。若实现该接口 ，可在服务安装与卸载的回调中做一些初始化与资源释放等操作。

d、特别重要:该类不允许存在父类。

### 三、生成中间类:
完成以上服务类的配置工作后，执行./gradlew :moduleName:clean :moduleName:compileDebugJavaWithJavac任务即可完成对应的服务中间类， 生成的中间类的 录为:rootProjectDir/modules_services_api/src/main/java

<img src="image/modules-api.png" width="40%" height="40%"/>

说明:服务中间类的生成规则:假如服务类名为ClassName，则会生成对应的一个服务中间类与服务中间接 ，命名规则为:ClassName+Service、I+ClassName。

### 四、调用服务:
在其他模块中，使用以下方法进行服务的调用:
```
BCDictionary dictionary = LoginService.get().getUserInfo();
```
如上图对Login类的调用，通过其中间服务类LoginService的调用即可，避免了对Login类的强依赖，达到了解耦的目的。


## QQ群
QQ群号：881324846

<a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=cbe96456218bbb48084e92163122e13c3159f3d507c3ebd5e66312a20d475496"><img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="modules技术交流群" title="modules技术交流群"></a>

或者扫描下方二维码加群聊

<img src="image/qq.png" width="30%" height="30%"/>
