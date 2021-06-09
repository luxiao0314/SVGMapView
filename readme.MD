## 使用svg实现自定义map,结构清晰,逻辑简单
1. 采用android studio将svg转换成xml,如raw/neimeng.xml
2. 利用Xml解析svg的代码 封装成javaBean 得到最重要的Path
3. 重写OnDraw方法 利用Path绘制台湾地图
4. 重写OnTouchEvent方法，记录手指触摸位置，判断这个位置是否坐落在某个省份上
5. 支持全国所有省份地图显示,svg资料见svg地图数据文件夹
6. 支持城市显示
7. 支持放大缩小
8. 支持点击选中效果

##地图示例

##### 中国地图
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/china.png)

##### 内蒙古
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/neimeng.png)

##### 台湾
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/taiwan.png)