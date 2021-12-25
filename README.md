# DoraIconLabelView

描述：顾名思义，一个图标和一行文字的组合，类似于手机桌面应用图标

复杂度：★★☆☆☆

分组：【Dora大控件组】

关系：暂无

技术要点：View的测量、基本绘图、Xfermode

### 照片

![avatar](https://github.com/dora4/dora_icon_label_view/blob/main/art/dora_icon_label_view.jpg)

### 软件包

https://github.com/dora4/dora_x/blob/main/art/dora_icon_label_view.apk

### 用法

| 自定义属性        | 描述                                                         |
| ----------------- | ------------------------------------------------------------ |
| dora_icon         | 设置drawable、mipmap图标                                     |
| dora_iconScaleX   | 设置图标水平方向的缩小比例，取值(0,1]，影响wrap_content的测量 |
| dora_iconScaleY   | 设置图标垂直方向的缩小比例，取值(0,1]，影响wrap_content的测量 |
| dora_text         | 设置文本                                                     |
| dora_textColor    | 设置默认文本颜色，即ratio=0时                                |
| dora_hoverColor   | 设置ratio=1的时候的文本颜色                                  |
| dora_textSize     | 设置文字大小                                                 |
| dora_iconLabelGap | 设置图标和文字的间距                                         |
| dora_ratio        | 用于颜色渐变镀色，取值[0,1]，仿微信底部tab条                 |

另外，可以结合android:padding进行布局。
