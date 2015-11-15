package de.cortex42.maerklin.testgui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.Objects;

/**
 * Created by ivo on 10.10.15.
 */

public class View {
    private JButton buttonStart;
    private JButton buttonStop;
    private JButton buttonBootloaderGo;
    private JCheckBox lightCheckBox;
    private JPanel panel;
    private JSlider speedSlider;
    private JTextField velocityTextField;
    private JComboBox<String> comboBoxLoc;
    private JButton buttonToggleDirection;
    private JComboBox<String> comboBoxInterface;
    private JTextField directionTextField;

    private boolean userAction = true;

    public JPanel getPanel(){
        return panel;
    }

    private Presenter presenter;

    public void setPresenter(Presenter presenter){
        this.presenter = presenter;
    }

    public View() {
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.sendStart();
            }
        });

        buttonBootloaderGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.sendBootloaderGo();
            }
        });

        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.sendStop();
            }
        });

        lightCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    presenter.sendLight(true);
                } else {
                    presenter.sendLight(false);
                }
            }
        });

        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Object source = changeEvent.getSource();

                if (source instanceof JSlider) {
                    if (!speedSlider.getValueIsAdjusting()) {
                        int velocity = speedSlider.getValue();
                        presenter.sendVelocity(velocity);
                        velocityTextField.setText(Integer.toString(velocity));
                    }
                }
            }
        });

        buttonToggleDirection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.sendToggleDirection();
            }
        });

        comboBoxInterface.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (userAction
                        && comboBoxLoc.getSelectedItem() != null
                        && (!Objects.equals(comboBoxLoc.getSelectedItem(), ""))
                        && comboBoxInterface.getSelectedItem() != null
                        && (!Objects.equals(comboBoxInterface.getSelectedItem(), ""))) {
                    presenter.initialize();

                    View.this.enableControls();
                }
            }
        });

        comboBoxLoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (userAction
                        && comboBoxLoc.getSelectedItem() != null
                        && (!Objects.equals(comboBoxLoc.getSelectedItem(), ""))
                        && comboBoxInterface.getSelectedItem() != null
                        && (!Objects.equals(comboBoxInterface.getSelectedItem(), ""))) {
                    presenter.initialize();

                    View.this.enableControls();
                }
            }
        });
    }

    public void setVelocity(int velocity){
        velocityTextField.setText(Integer.toString(velocity));
    }

    public void addLoc(String locName){
        userAction = false;
        comboBoxLoc.addItem(locName);
        userAction = true;
    }

    public void setDirection(String direction){
        directionTextField.setText(direction);
    }

    public String getSelectedInterface(){
        return (String)comboBoxInterface.getSelectedItem();
    }

    public String getSelectedLoc(){ return (String)comboBoxLoc.getSelectedItem();}

    public void addInterface(String interfaceString){
        userAction = false;
        comboBoxInterface.addItem(interfaceString);
        userAction = true;
    }

    public void enableControls(){
        if(comboBoxInterface.getSelectedItem() != null
                && (!Objects.equals(comboBoxInterface.getSelectedItem(),""))
                && comboBoxLoc.getSelectedItem() != null
                && (!Objects.equals(comboBoxLoc.getSelectedItem(),""))){

            speedSlider.setEnabled(true);
            buttonToggleDirection.setEnabled(true);
            buttonStart.setEnabled(true);
            buttonStop.setEnabled(true);
            buttonBootloaderGo.setEnabled(true);
            lightCheckBox.setEnabled(true);
        }
    }
}
