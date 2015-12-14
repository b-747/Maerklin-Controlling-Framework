package de.cortex42.maerklin.testgui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Created by ivo on 10.10.15.
 */

public class View {
    private JButton buttonStart;
    private JButton buttonStop;
    private JButton buttonBootloaderGo;
    private JCheckBox lightCheckBox;
    private JPanel panel;
    private JSlider sliderVelocity;
    private JTextField velocityTextField;
    private JButton buttonToggleDirection;
    private JTextField directionTextField;
    private JRadioButton radioButtonEthernet;
    private JRadioButton radioButtonSerialPort;
    private JTextField textFieldEthernet;
    private JComboBox<String> comboBoxSerialPort;
    private JButton buttonConfigData;
    private JComboBox comboBoxLoc;
    private JTextField textFieldLoc;

    public JPanel getPanel() {
        return panel;
    }

    private Presenter presenter;

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public View() {
        //only one radio button can be selected at a time in the group
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonEthernet);
        buttonGroup.add(radioButtonSerialPort);

        radioButtonEthernet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                textFieldEthernet.setEnabled(true);
                comboBoxSerialPort.setEnabled(false);
                presenter.useEthernetConnection(true);
            }
        });

        radioButtonSerialPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                comboBoxSerialPort.setEnabled(true);
                textFieldEthernet.setEnabled(false);
                presenter.useEthernetConnection(false);
            }
        });

        comboBoxSerialPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {

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

        sliderVelocity.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Object source = changeEvent.getSource();

                if (source instanceof JSlider) {
                    if (!sliderVelocity.getValueIsAdjusting()) {
                        int velocity = sliderVelocity.getValue();
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

        buttonConfigData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                presenter.sendGetLoks();
            }
        });
    }

    public int getLoc() {
        return Integer.getInteger(textFieldLoc.getText());
    }

    public String getIpAddress() {
        return textFieldEthernet.getText();
    }

    public String getSerialPort() {
        return (String) comboBoxSerialPort.getSelectedItem();
    }

    public void addSerialPorts(ArrayList<String> serialPorts) {
        comboBoxSerialPort.removeAllItems();

        for (int i = 0; i < serialPorts.size(); i++) {
            comboBoxSerialPort.addItem(serialPorts.get(i));
        }
    }

    public void setVelocity(int velocity) {
        velocityTextField.setText(Integer.toString(velocity));
    }

    public void setDirection(String direction) {
        directionTextField.setText(direction);
    }

    public void showConfigData(final String s) {
        JOptionPane.showMessageDialog(null, s, "loks.cs2", JOptionPane.PLAIN_MESSAGE);
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
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        buttonToggleDirection = new JButton();
        buttonToggleDirection.setEnabled(true);
        buttonToggleDirection.setText("Toggle Direction");
        panel.add(buttonToggleDirection, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonConfigData = new JButton();
        buttonConfigData.setText("Show loks.cs2");
        panel.add(buttonConfigData, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonStart = new JButton();
        buttonStart.setEnabled(true);
        buttonStart.setText("Start");
        panel.add(buttonStart, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonStop = new JButton();
        buttonStop.setEnabled(true);
        buttonStop.setText("Stop");
        panel.add(buttonStop, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonBootloaderGo = new JButton();
        buttonBootloaderGo.setEnabled(true);
        buttonBootloaderGo.setText("Bootloader Go");
        panel.add(buttonBootloaderGo, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lightCheckBox = new JCheckBox();
        lightCheckBox.setEnabled(true);
        lightCheckBox.setText("Light");
        panel.add(lightCheckBox, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Velocity:");
        panel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Loc:");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxLoc = new JComboBox();
        panel1.add(comboBoxLoc, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radioButtonEthernet = new JRadioButton();
        radioButtonEthernet.setEnabled(true);
        radioButtonEthernet.setText("Ethernet");
        panel1.add(radioButtonEthernet, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldEthernet = new JTextField();
        textFieldEthernet.setEditable(true);
        textFieldEthernet.setEnabled(true);
        panel1.add(textFieldEthernet, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        comboBoxSerialPort = new JComboBox();
        panel1.add(comboBoxSerialPort, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radioButtonSerialPort = new JRadioButton();
        radioButtonSerialPort.setEnabled(true);
        radioButtonSerialPort.setText("SerialPort");
        panel1.add(radioButtonSerialPort, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sliderVelocity = new JSlider();
        sliderVelocity.setEnabled(true);
        sliderVelocity.setInverted(false);
        sliderVelocity.setMajorTickSpacing(10);
        sliderVelocity.setMaximum(1000);
        sliderVelocity.setMinimum(0);
        sliderVelocity.setMinorTickSpacing(1);
        sliderVelocity.setOrientation(0);
        sliderVelocity.setPaintLabels(false);
        sliderVelocity.setPaintTicks(true);
        sliderVelocity.setPaintTrack(true);
        sliderVelocity.setSnapToTicks(false);
        sliderVelocity.setValue(0);
        panel.add(sliderVelocity, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label2.setLabelFor(comboBoxLoc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
