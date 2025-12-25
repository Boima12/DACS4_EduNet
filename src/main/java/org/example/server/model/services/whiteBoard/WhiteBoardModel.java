package org.example.server.model.services.whiteBoard;

import java.io.Serializable;
import org.example.common.objects.whiteBoard.WhiteboardCommand;

public class WhiteBoardModel implements Serializable {

    public int x, y;
    public WhiteboardCommand type;

    public WhiteBoardModel(int x, int y) {
        this.x = x;
        this.y = y;
    }
}