![](https://img.shields.io/github/v/release/weikaiyun/EasyFragmentation.svg)
[![](https://jitpack.io/v/weikaiyun/EasyFragmentation.svg)](https://jitpack.io/#weikaiyun/EasyFragmentation)
# EasyFragmentation

***
#### 鸣谢
#### 原项目非常方便使用，但由于原作者已不再维护此项目，特基于原项目[Fragmentation](https://github.com/YoKeyword/Fragmentation)， 进行升级改造。
#### 感谢原作者右右的开源贡献。 如有不妥请及时联系。
***
#### 框架负责管理fragment的各种操作，相比于google新出的navigation框架，更加灵活多变，易于使用。
#### 框架对于fragment可见性判断，懒加载，转场动画有比较好的处理。
#### 框架的源码简单易懂， 不存在复杂的逻辑.

### 推荐使用NewNoBackStack分支。

#### gradle使用:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
```
dependencies {
	//请使用最新版本
	implementation 'com.github.weikaiyun:EasyFragmentation:latest'
}
```
##### demo有比较详细的使用示例， 欢迎star。

#####  如需详细API 可暂时到原作者项目查看[API](https://github.com/YoKeyword/Fragmentation/wiki/2.-API)
#####  后期会根据修改后整理一篇新的使用文档，如有使用问题欢迎提交issues
