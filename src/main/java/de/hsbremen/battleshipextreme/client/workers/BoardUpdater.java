package de.hsbremen.battleshipextreme.client.workers;

import de.hsbremen.battleshipextreme.client.GUI;
import de.hsbremen.battleshipextreme.model.FieldState;

import javax.swing.*;

/**
 * Created on 17.06.2015.
 */
public class BoardUpdater extends SwingWorker<Integer, Object> {
    private final JButton[][] board;
    private final FieldState[][] fieldStates;
    private GUI gui;

    public BoardUpdater(GUI gui, JButton[][] board, FieldState[][] fieldStates) {
        this.gui = gui;
        this.board = board;
        this.fieldStates = fieldStates;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        int boardSize = fieldStates.length;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                FieldState f = fieldStates[i][j];
                if (f != null) {
                    switch (fieldStates[i][j]) {
                        case DESTROYED:
                            board[i][j].setIcon(gui.getDestroyedIcon());
                            break;
                        case HIT:
                            board[i][j].setIcon(gui.getHitIcon());
                            break;
                        case MISSED:
                            board[i][j].setIcon(gui.getMissedIcon());
                            break;
                        case HAS_SHIP:
                            board[i][j].setIcon(gui.getShipIcon());
                            break;
                        case IS_EMPTY:
                            board[i][j].setIcon(null);
                        default:
                            break;
                    }
                } else {
                    // board[i][j].setBackground(GUI.UNKNOWN_COLOR);
                    board[i][j].setIcon(null);
                }
            }
        }
        return 1;
    }
}
