package de.cortex42.maerklin.testgui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
    private JRadioButton radioButtonEthernet;
    private JRadioButton radioButtonSerialPort;
    private JTextField textFieldEthernet;
    private JTextField textFieldSerialPort;
    private JFormattedTextField formattedTextFieldLoc;

    private boolean userAction = true;

    public JPanel getPanel() {
        return panel;
    }

    private Presenter presenter;

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public View() {

        //formattedTextFieldLoc.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(Number)));

        //only one radio button can be selected at a time in the group
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonEthernet);
        buttonGroup.add(radioButtonSerialPort);

        radioButtonEthernet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                textFieldEthernet.setEnabled(true);
                textFieldSerialPort.setEnabled(false);
            }
        });

        radioButtonSerialPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                textFieldSerialPort.setEnabled(true);
                textFieldEthernet.setEnabled(false);
            }
        });

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

    public void setVelocity(int velocity) {
        velocityTextField.setText(Integer.toString(velocity));
    }

    public void addLoc(String locName) {
        userAction = false;
        comboBoxLoc.addItem(locName);
        userAction = true;
    }

    public void setDirection(String direction) {
        directionTextField.setText(direction);
    }

    public String getSelectedInterface() {
        return (String) comboBoxInterface.getSelectedItem();
    }

    public String getSelectedLoc() {
        return (String) comboBoxLoc.getSelectedItem();
    }

    public void addInterface(String interfaceString) {
        userAction = false;
        comboBoxInterface.addItem(interfaceString);
        userAction = true;
    }

    public void enableControls() {
        if (comboBoxInterface.getSelectedItem() != null
                && (!Objects.equals(comboBoxInterface.getSelectedItem(), ""))
                && comboBoxLoc.getSelectedItem() != null
                && (!Objects.equals(comboBoxLoc.getSelectedItem(), ""))) {

            speedSlider.setEnabled(true);
            buttonToggleDirection.setEnabled(true);
            buttonStart.setEnabled(true);
            buttonStop.setEnabled(true);
            buttonBootloaderGo.setEnabled(true);
            lightCheckBox.setEnabled(true);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 8, new Insets(0, 0, 0, 0), -1, -1));
        buttonStart = new JButton();
        buttonStart.setEnabled(false);
        buttonStart.setText("Start");
        panel.add(buttonStart, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        buttonStop = new JButton();
        buttonStop.setEnabled(false);
        buttonStop.setText("Stop");
        panel.add(buttonStop, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 6, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonBootloaderGo = new JButton();
        buttonBootloaderGo.setEnabled(false);
        buttonBootloaderGo.setText("Bootloader Go");
        panel.add(buttonBootloaderGo, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lightCheckBox = new JCheckBox();
        lightCheckBox.setEnabled(false);
        lightCheckBox.setText("Light");
        panel.add(lightCheckBox, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        speedSlider = new JSlider();
        speedSlider.setEnabled(false);
        speedSlider.setMaximum(1000);
        speedSlider.setOrientation(1);
        speedSlider.setPaintLabels(true);
        speedSlider.setValue(0);
        panel.add(speedSlider, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Velocity:");
        panel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        velocityTextField = new JTextField();
        velocityTextField.setEditable(false);
        panel.add(velocityTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 4, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonToggleDirection = new JButton();
        buttonToggleDirection.setEnabled(false);
        buttonToggleDirection.setText("Toggle Direction");
        panel.add(buttonToggleDirection, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Direction");
        panel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        directionTextField = new JTextField();
        directionTextField.setEditable(false);
        panel.add(directionTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        radioButtonEthernet = new JRadioButton();
        radioButtonEthernet.setEnabled(false);
        radioButtonEthernet.setText("Ethernet");
        panel.add(radioButtonEthernet, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radioButtonSerialPort = new JRadioButton();
        radioButtonSerialPort.setEnabled(false);
        radioButtonSerialPort.setText("SerialPort");
        panel.add(radioButtonSerialPort, new com.intellij.uiDesigner.core.GridConstraints(5, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Loc:");
        panel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(4, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldEthernet = new JTextField();
        textFieldEthernet.setEditable(false);
        textFieldEthernet.setEnabled(false);
        panel.add(textFieldEthernet, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textFieldSerialPort = new JTextField();
        textFieldSerialPort.setEditable(false);
        textFieldSerialPort.setEnabled(false);
        panel.add(textFieldSerialPort, new com.intellij.uiDesigner.core.GridConstraints(5, 7, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        formattedTextFieldLoc = new JFormattedTextField();
        formattedTextFieldLoc.setEnabled(false);
        panel.add(formattedTextFieldLoc, new com.intellij.uiDesigner.core.GridConstraints(4, 7, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        label1.setLabelFor(velocityTextField);
        label2.setLabelFor(directionTextField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
