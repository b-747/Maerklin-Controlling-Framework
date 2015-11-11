package de.cortex42.maerklin.testgui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by ivo on 12.10.15.
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("View");

        View view = new View();
        Presenter presenter = new Presenter(view);
        view.setPresenter(presenter);

        frame.setContentPane(view.getPanel());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                presenter.cleanUp();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
