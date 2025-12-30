//package org.example.common.objects;
//
//import java.awt.Color;
//import java.io.Serializable;
//
//public class WhiteboardPacket implements Serializable {
//	
//    public WhiteboardCommand command;
//    public int x, y;
//    public boolean lock;
//    public int r, g, b;   
//	public Color color;
//	
//    public WhiteboardPacket(WhiteboardCommand command) {
//        this.command = command;
//    }
//
//    public WhiteboardPacket(int x, int y) {
//        this.command = WhiteboardCommand.DRAW;
//        this.x = x;
//    }
//
//    public WhiteboardPacket(WhiteboardCommand command, int x, int y, Color c) {
//        this.command = command;
//        this.x = x;
//        this.y = y;
//        this.r = c.getRed();
//        this.g = c.getGreen();
//        this.b = c.getBlue();
//    }
//    
//    public WhiteboardPacket(int x, int y, Color color) {
//        this.command = WhiteboardCommand.DRAW;
//        this.x = x;
//        this.y = y;
//        this.color = color;
//    }
//}
//

package org.example.common.objects.services.whiteBoard;

import java.awt.Color;
import java.io.Serializable;

/**
 * ??? - Cần Hoàng ghi chú thích cho file
 *
 */
public class WhiteboardPacket implements Serializable {

    private static final long serialVersionUID = 1L;

    public WhiteboardCommand command;
    public int x, y;
    public boolean lock;
    public Color color;

    // Constructor cho các command không cần tọa độ (CLEAR, UNDO, LOCK, SYNC)
    public WhiteboardPacket(WhiteboardCommand command) {
        this.command = command;
        this.color = Color.BLACK; // mặc định
    }

    // Constructor cho DRAW mặc định màu đen
    public WhiteboardPacket(int x, int y) {
        this.command = WhiteboardCommand.DRAW;
        this.x = x;
        this.y = y;
        this.color = Color.BLACK;
    }

    // Constructor DRAW với màu tùy chỉnh
    public WhiteboardPacket(int x, int y, Color color) {
        this.command = WhiteboardCommand.DRAW;
        this.x = x;
        this.y = y;
        this.color = color != null ? color : Color.BLACK;
    }

    // Constructor DRAW hoặc BEGIN/END với màu tùy chọn
    public WhiteboardPacket(WhiteboardCommand command, int x, int y, Color color) {
        this.command = command;
        this.x = x;
        this.y = y;
        this.color = color != null ? color : Color.BLACK;
    }
}
