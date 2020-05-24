# EasyFragmentation
fragment懒加载可以利用setMaxLifeCycle实现 SupportViewDelegate个人觉得不再需要
关于setMaxLifeCycle请大家查看此库 https://github.com/AndyJennifer/AndroidxLazyLoad
和此文档https://juejin.im/post/5e232d01e51d455801624c06 个人觉得写得很好

Fragmentation(https://github.com/YoKeyword/Fragmentation) 的简化版本， 只管理fragment跳转，代码完全可以看懂，然后可以以此为基础定制使用。

库里面还有AdapterDelegate个人觉得也是完全可以看懂的库(感谢 https://github.com/sockeqwe/AdapterDelegates )，方便ReclerView使用，http://hannesdorfmann.com/android/adapter-delegates

库里面有CustomFragmentPagerAdapter，可以配合Fragmentation使用
https://medium.com/@pjonceski/fragmentpageradapter-with-fragments-that-restore-their-state-properly-a427ecfd792e
https://inthecheesefactory.com/blog/fragment-state-saving-best-practices/en 
原理讲解很清晰

关于windowInset请查看此链接，https://gist.github.com/weikaiyun/c22b92d3159c93667bbceb76c98cafe0

希望以上对大家能有帮助。如果有问题，希望大家可以一起探讨。
