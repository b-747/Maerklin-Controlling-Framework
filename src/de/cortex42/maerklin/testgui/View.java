package de.cortex42.maerklin.testgui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

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
    private JButton buttonToggleDirection;
    private JRadioButton radioButtonEthernet;
    private JRadioButton radioButtonSerialPort;
    private JComboBox<String> comboBoxSerialPort;
    private JButton buttonConfigData;
    private JComboBox<String> comboBoxLoc;
    private JFormattedTextField formattedTextFieldIpAddress;

    public JPanel getPanel() {
        return panel;
    }

    private Presenter presenter;

    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    public View() {
        $$$setupUI$$$();
        //only one radio button can be selected at a time in the group
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonEthernet);
        buttonGroup.add(radioButtonSerialPort);

        radioButtonEthernet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                formattedTextFieldIpAddress.setEnabled(true);
                comboBoxSerialPort.setEnabled(false);
                presenter.useEthernetConnection(true);
            }
        });

        radioButtonSerialPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                comboBoxSerialPort.setEnabled(true);
                formattedTextFieldIpAddress.setEnabled(false);
                presenter.useEthernetConnection(false);
            }
        });

        comboBoxSerialPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                if (actionEvent.getActionCommand().equals("comboBoxChanged")) { //handle selection only
                    final Object selectedItem = comboBoxSerialPort.getSelectedItem();

                    if (selectedItem != null) {
                        presenter.setSerialPort((String) selectedItem);
                    }
                }
            }
        });

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                presenter.sendStart();
            }
        });

        buttonBootloaderGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                presenter.sendBootloaderGo();
            }
        });

        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                presenter.sendStop();
            }
        });

        lightCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                presenter.sendLight(lightCheckBox.isSelected());
            }
        });

        sliderVelocity.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent changeEvent) {
                final Object source = changeEvent.getSource();

                if (source instanceof JSlider) {
                    final int velocity = sliderVelocity.getValue();
                    sliderVelocity.setToolTipText(Integer.toString(velocity));

                    if (!sliderVelocity.getValueIsAdjusting()) {
                        presenter.sendVelocity(velocity);
                    }
                }
            }
        });

        buttonToggleDirection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                presenter.sendToggleDirection();
            }
        });

        buttonConfigData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                presenter.sendGetLocos();
            }
        });

        comboBoxLoc.getEditor().addActionListener(new ActionListener() { //handle enter
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                comboBoxLoc.addItem((String) comboBoxLoc.getEditor().getItem());
            }
        });

        comboBoxLoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                if (actionEvent.getActionCommand().equals("comboBoxChanged")) { //handle selection only
                    presenter.setLoc((String) comboBoxLoc.getSelectedItem());
                }
            }
        });

        formattedTextFieldIpAddress.addActionListener(new ActionListener() { //handle enter
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                presenter.setIpAddress(formattedTextFieldIpAddress.getText());
            }
        });
    }

    public void addSerialPorts(final ArrayList<String> serialPorts) {
        for (int i = 0; i < serialPorts.size(); i++) {
            comboBoxSerialPort.addItem(serialPorts.get(i));
        }

        if (comboBoxSerialPort.getItemCount() == 0) {
            radioButtonSerialPort.setEnabled(false);
        }
    }

    public void setVelocity(final int velocity) {
        sliderVelocity.setValue(velocity);
    }

    public void setDirection(final String direction) {
        buttonToggleDirection.setText("Toggle Direction (Current: " + direction + ")");
    }

    public void setDefaultIpAddress(final String ipAddress) {
        formattedTextFieldIpAddress.setValue(ipAddress);
    }

    public void showConfigData(final String s) {
        JOptionPane.showMessageDialog(null, s, "loks.cs2", JOptionPane.PLAIN_MESSAGE);
    }

    public void showException(final Exception exception) {
        JOptionPane.showMessageDialog(null, Arrays.toString(exception.getStackTrace()), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void createUIComponents() {
        try {
            formattedTextFieldIpAddress = new JFormattedTextField(new MaskFormatter("###.###.###.###"));
        } catch (final ParseException e) {
            //never happens
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        buttonToggleDirection = new JButton();
        buttonToggleDirection.setEnabled(true);
        buttonToggleDirection.setText("Toggle Direction");
        panel.add(buttonToggleDirection, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonConfigData = new JButton();
        buttonConfigData.setText("Show loks.cs2");
        panel.add(buttonConfigData, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        comboBoxLoc.setEditable(true);
        panel1.add(comboBoxLoc, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radioButtonEthernet = new JRadioButton();
        radioButtonEthernet.setEnabled(true);
        radioButtonEthernet.setSelected(true);
        radioButtonEthernet.setText("Ethernet");
        panel1.add(radioButtonEthernet, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxSerialPort = new JComboBox();
        comboBoxSerialPort.setEnabled(false);
        panel1.add(comboBoxSerialPort, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radioButtonSerialPort = new JRadioButton();
        radioButtonSerialPort.setEnabled(true);
        radioButtonSerialPort.setText("SerialPort");
        panel1.add(radioButtonSerialPort, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        formattedTextFieldIpAddress.setEnabled(true);
        formattedTextFieldIpAddress.setText("");
        panel1.add(formattedTextFieldIpAddress, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
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
        buttonBootloaderGo = new JButton();
        buttonBootloaderGo.setEnabled(true);
        buttonBootloaderGo.setText("Bootloader Go");
        panel.add(buttonBootloaderGo, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonStart = new JButton();
        buttonStart.setEnabled(true);
        buttonStart.setText("Start");
        panel.add(buttonStart, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonStop = new JButton();
        buttonStop.setEnabled(true);
        buttonStop.setText("Stop");
        panel.add(buttonStop, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label2.setLabelFor(comboBoxLoc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
