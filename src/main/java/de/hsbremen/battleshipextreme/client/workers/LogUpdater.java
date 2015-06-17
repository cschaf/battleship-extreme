package de.hsbremen.battleshipextreme.client.workers;

import javax.swing.*;

/**
 * Created by cschaf on 17.06.2015.
 */
public class LogUpdater extends SwingWorker<Integer, Object> {
    JTextArea area;
    String message;

    public LogUpdater(JTextArea area, String message) {
        this.area = area;
        this.message = message;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        area.append(message + "\r\n");
        return 1;
    }
}
