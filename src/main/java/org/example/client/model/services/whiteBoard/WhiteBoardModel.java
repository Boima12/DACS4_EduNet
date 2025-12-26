package org.example.client.model.services.whiteBoard;

import java.io.Serializable;
import java.awt.Color;

public class WhiteBoardModel implements Serializable {
    public int x, y;
    public Color color;

    public WhiteBoardModel(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color != null ? color : Color.BLACK;
    }
}
