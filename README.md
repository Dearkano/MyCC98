# MyCC98
App在看帖界面使用WebView生成以解析UBB
具体使用操作为：
1. 在UbbContainer.js中找到function generateWebView
2. 目前的接口参数为Postinfo，TopicInfo，BoardInfo，以及react-dom嵌入的dom节点
3. UI界面直接可以在function generatePost中修改

当然最好的建议还是使用TS，JSX编写UI界面然后webpack，所以将所需ts文件一起打包放在项目libs中了~
