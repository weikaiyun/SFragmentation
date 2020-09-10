# EasyFragmentation
关于fragment管理 建议大家看下youtube上这个视频课 讲解很好 https://www.youtube.com/playlist?list=PLfuE3hOAeWhZDH-wHD0BJsJl7PpEW-wN8
fragment懒加载可以利用setMaxLifeCycle实现
关于setMaxLifeCycle请大家查看此库 https://github.com/AndyJennifer/AndroidxLazyLoad
和此文档https://juejin.im/post/5e232d01e51d455801624c06 个人觉得写得很好

Fragmentation(https://github.com/YoKeyword/Fragmentation) 的简化版本， 只管理fragment跳转，代码完全可以看懂，然后可以以此为基础定制使用。

推荐使用NoBackStack分支。

现在添加了基本使用demo startWithPop和startWithPopTo可以比较好地进行处理。用法见DemoFragment4和DemoFragment5。

库里面还有AdapterDelegate个人觉得也是完全可以看懂的库(感谢 https://github.com/sockeqwe/AdapterDelegates )，方便ReclerView使用，http://hannesdorfmann.com/android/adapter-delegates

关于windowInset发现一个特别好的库，推荐给大家 https://github.com/chrisbanes/insetter

希望以上对大家能有帮助。如果有问题，希望大家可以一起探讨。

gradle使用:

allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
	implementation 'com.github.weikaiyun:EasyFragmentation:1.0.3'
}
