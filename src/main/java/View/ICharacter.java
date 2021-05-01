package View;

import javafx.scene.canvas.GraphicsContext;

public interface ICharacter {
    /**
     * A function that gets all what is needed to draw the character
     * @param drawer drawer to draw with
     * @param rowIndex
     * @param columnIndex
     * @param cellHeight
     * @param cellWidth
     */
    void drawCharacter(final GraphicsContext drawer,final double rowIndex,final double columnIndex, final double cellHeight,final double cellWidth);
}
