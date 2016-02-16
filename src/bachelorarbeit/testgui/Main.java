package bachelorarbeit.testgui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by ivo on 12.10.15.
 *
 * -----------PROTOTYPE!-----------
 */
public class Main {
    public static void main(String[] args) {
        final JFrame frame = new JFrame("View");

        final View view = new View();
        final Presenter presenter = new Presenter(view);
        view.setPresenter(presenter);

        frame.setContentPane(view.getPanel());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                presenter.cleanUp();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
