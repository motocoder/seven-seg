I hope this can help someone else. I've created and shared a project that renders a 7 segment digital lcd type digit to a canvas using a helper class which should be useable with any other android custom view or layout.

It uses 7 different individual vector drawables plus a background drawable in a different color with all of the segments visible. Then the helper object draws one of the different states of the numbers 0-9. To give a rendering of the number chosen. 

The testing view draws two of them which divide and modulus the system clock to simulate a counting animation. This is just for example though any displayed number use case is possible with the API built.

To use the helper class simply:

```java
this.painter = new SevenSegmentPainter(context);

this.painter.setWidth(channelDigitWidth);
this.painter.setHeight(channelDigitHeight);

painter.drawDigit(canvas, (int) ((System.currentTimeMillis() / 10000) % 10));
```


https://github.com/user-attachments/assets/0e3e0ff0-720d-4d76-98a6-695d9d387485




