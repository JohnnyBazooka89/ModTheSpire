package com.evacipated.cardcrawl.modthespire.ui;

import com.evacipated.cardcrawl.modthespire.ModList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class ModListEditorWindow extends JDialog
{
    private JPanel contentPane;
    private JButton closeButton;
    private JList<String> listList;
    private JButton newButton;
    private JButton deleteButton;
    private JPanel buttonsPanel;
    private JButton renameButton;
    private JButton importButton;
    private JButton exportButton;

    private final DefaultListModel<String> listModel;

    public ModListEditorWindow(ModSelectWindow owner)
    {
        super(owner, "Mod Lists", true);
        setContentPane(contentPane);
        setModal(true);

        closeButton.addActionListener(e -> onClose());

        // call onClose() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onClose();
            }
        });

        // call onClose() on ESCAPE
        contentPane.registerKeyboardAction(
            e -> onClose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        newButton.setMnemonic(KeyEvent.VK_N);
        deleteButton.setMnemonic(KeyEvent.VK_D);
        renameButton.setMnemonic(KeyEvent.VK_R);
        importButton.setMnemonic(KeyEvent.VK_I);
        exportButton.setMnemonic(KeyEvent.VK_E);

        listModel = new DefaultListModel<>();
        for (String listName : ModList.getAllModListNames()) {
            listModel.addElement(listName);
        }
        listList.setModel(listModel);

        listList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean enabled = listList.getSelectedValue() != null;
                if (ModList.DEFAULT_LIST.equals(listList.getSelectedValue())) {
                    deleteButton.setEnabled(false);
                    renameButton.setEnabled(false);
                } else {
                    deleteButton.setEnabled(enabled);
                    renameButton.setEnabled(enabled);
                }
                exportButton.setEnabled(enabled);
            }
        });

        // New
        newButton.addActionListener(e -> {
            String prevName = null;
            do {
                String s = (String) JOptionPane.showInputDialog(
                    this,
                    "Name:",
                    "New Mod List",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    prevName
                );
                if (s != null && !s.isEmpty()) {
                    if (listModel.contains(s)) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Mod List named \"" + s + "\" already exists.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        prevName = s;
                        continue;
                    } else {
                        listModel.addElement(s);
                        listList.setSelectedValue(s, true);
                        ModList.save(s, new File[0]);
                        owner.updateProfilesList();
                    }
                }
                break;
            } while (true);
        });
        // Delete
        deleteButton.addActionListener(e -> {
            String listName = listList.getSelectedValue();

            int n = JOptionPane.showConfirmDialog(
                this,
                "Are you sure?\nThis action cannot be undone.",
                "Delete Mod List \"" + listName + "\"",
                JOptionPane.YES_NO_OPTION
            );
            if (n == 0) {
                listModel.removeElement(listName);
                ModList.delete(listName);
                owner.updateProfilesList();
            }
        });
        // Rename
        renameButton.addActionListener(e -> {
            int index = listList.getSelectedIndex();
            String listName = listList.getSelectedValue();

            String prevName = listName;
            do {
                String s = (String) JOptionPane.showInputDialog(
                    this,
                    "Name:",
                    "Rename Mod List \"" + listName + "\"",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    prevName
                );
                if (s != null && !s.isEmpty() && !s.equals(listName)) {
                    if (listModel.contains(s)) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Mod List named \"" + s + "\" already exists.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        prevName = s;
                        continue;
                    } else {
                        listModel.set(index, s);
                        ModList.rename(listName, s);
                        owner.updateProfilesList(listName, s);
                    }
                }
                break;
            } while (true);
        });
        // Import
        importButton.addActionListener(e -> {
            // TODO
        });
        // Export
        exportButton.addActionListener(e -> {
            // TODO
        });
    }

    private void onClose()
    {
        dispose();
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
    private void $$$setupUI$$$()
    {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setMinimumSize(new Dimension(200, 200));
        contentPane.setPreferredSize(new Dimension(350, 300));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        contentPane.add(panel1, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(scrollPane1, gbc);
        listList = new JList();
        listList.setLayoutOrientation(0);
        listList.setSelectionMode(0);
        scrollPane1.setViewportView(listList);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 0, 5, 5);
        contentPane.add(panel2, gbc);
        closeButton = new JButton();
        closeButton.setText("Close");
        panel2.add(closeButton, BorderLayout.SOUTH);
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        panel2.add(buttonsPanel, BorderLayout.NORTH);
        newButton = new JButton();
        newButton.setText("New");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        buttonsPanel.add(newButton, gbc);
        deleteButton = new JButton();
        deleteButton.setEnabled(false);
        deleteButton.setText("Delete");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        buttonsPanel.add(deleteButton, gbc);
        renameButton = new JButton();
        renameButton.setEnabled(false);
        renameButton.setText("Rename");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        buttonsPanel.add(renameButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 15;
        buttonsPanel.add(spacer1, gbc);
        importButton = new JButton();
        importButton.setText("Import");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        buttonsPanel.add(importButton, gbc);
        exportButton = new JButton();
        exportButton.setEnabled(false);
        exportButton.setText("Export");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        buttonsPanel.add(exportButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return contentPane;
    }

}
