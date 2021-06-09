## 使用SVG实现自定义View map
1. 利用Xml解析SVG的代码 封装成javaBean 得到最重要的Path
2. 重写OnDraw方法 利用Path绘制台湾地图
3. 重写OnTouchEvent方法，记录手指触摸位置，判断这个位置是否坐落在某个省份上
4. 支持全国所有省份地图显示,svg资料见svg地图数据文件夹
5. 支持城市显示
6. 支持放大缩小
7. 支持点击选中效果

##地图示例

##### 中国地图
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/china.png)

##### 内蒙古
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/neimeng.png)

##### 台湾
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/taiwan.png)