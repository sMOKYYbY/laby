package g62221.dev.ascii;

import g62221.dev.ascii.controller.AsciiController;
import g62221.dev.ascii.model.AsciiPaint;
import g62221.dev.ascii.view.AsciiView;

public class App {
    public static void main(String[] args) {
        AsciiPaint paint = new AsciiPaint(40, 20);
        AsciiView view = new AsciiView(paint);
        AsciiController controller = new AsciiController(paint, view);
        controller.start();
    }
}
