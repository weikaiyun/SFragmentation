# EasyFragmentation
改版自Fragmentation(https://github.com/YoKeyword/Fragmentation)， 感谢原作者的贡献。

框架负责管理fragment的各种操作，相比于google新出的navigation框架，更加灵活多变，易于使用。

框架对于fragment可见性判断，懒加载，转场动画有比较好的处理。

框架的源码简单易懂， 不存在复杂的逻辑.

推荐使用NoBackStack分支。

demo有比较详细的使用示例， 欢迎star。

gradle使用:

allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
	implementation 'com.github.weikaiyun:EasyFragmentation:1.3.9'
}
