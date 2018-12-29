package com.zxy.idea.plugin.fvbi.support;

import com.android.utils.Pair;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.CollectionComboBoxModel;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.List;

/**
 * Created by zhengxiaoyong on 2018/09/30.
 */
public class FindViewByIdGenerateDialog extends JDialog {
    private JPanel contentPane;
    private JButton insertButton;
    private JButton cancelButton;
    private JPanel configPane;
    private JPanel configHeaderPane;
    private JTextField rootViewTextField;
    private JPanel configContentPane;
    private JCheckBox memberCheckBox;
    private JCheckBox localCheckBox;
    private JPanel mainPane;
    private JTable viewIdTable;
    private JCheckBox variablePrefixCheckBox;
    private JCheckBox javaCheckBox;
    private JCheckBox kotlinCheckBox;
    private JCheckBox privateCheckBox;
    private JCheckBox defaultCheckBox;
    private JCheckBox publicCheckBox;
    private JCheckBox protectCheckBox;
    private JPanel viewIdPane;
    private JScrollPane viewIdScrollPane;
    private JCheckBox api26CheckBox;
    private JPanel generateCodePane;
    private JScrollPane codeScrollPane;
    private JTextArea codeTextArea;
    private JCheckBox kotlinLazyCheckBox;
    private JButton copyButton;
    private JComboBox classComboBox;
    private JComboBox methodComboBox;
    private JPanel codePositionPane;
    private JCheckBox synchronizedCheckBox;
    private JCheckBox noneCheckBox;
    private JPanel threadModePanel;

    private Project mProject;
    private DefaultTableModel mTableModel;
    private boolean isSelectAllClick = false;
    private Object[][] mTableData;
    private String mClassName;
    private String mMethodName;

    public FindViewByIdGenerateDialog(Project project) {
        mProject = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(insertButton);
        setTitle("FindViewById Generator");

        preInit();

        init();

        handleConfigPanelEvent();

        handleViewIdPanelEvent();

        handleCodePanelEvent();

        updateGenerateCode();

        if (ConfigCenter.getInstance().getFileType() == FileType.JAVA) {
            javaCheckBox.setSelected(true);
        } else if (ConfigCenter.getInstance().getFileType() == FileType.KOTLIN) {
            kotlinCheckBox.setSelected(true);
        }

        updateInsertComboBoxStatus();

    }

    private void preInit() {
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new CodeInjector(mProject, mTableData, mClassName, mMethodName).setInterceptor(new CodeInjector.Interceptor() {
                    @Override
                    public void beforeExecute() {

                    }

                    @Override
                    public void afterExecute() {
                        ConfigCenter.getInstance().reset();
                        ClassInfoManager.getInstance().clear();
                        ViewIdManager.getInstance().clear();
                    }
                }).execute();
            }
        });

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable transferable = new StringSelection(codeTextArea.getText());
                clipboard.setContents(transferable, null);
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void init() {
        rootViewTextField.setText("");
        variablePrefixCheckBox.setSelected(true);
        api26CheckBox.setSelected(true);
        javaCheckBox.setSelected(true);
        kotlinCheckBox.setSelected(false);
        memberCheckBox.setSelected(true);
        localCheckBox.setSelected(false);
        privateCheckBox.setSelected(true);
        defaultCheckBox.setSelected(false);
        publicCheckBox.setSelected(false);
        protectCheckBox.setSelected(false);
        kotlinLazyCheckBox.setSelected(false);
        kotlinLazyCheckBox.setEnabled(false);
        threadModePanel.setEnabled(false);
        noneCheckBox.setSelected(true);
        synchronizedCheckBox.setSelected(false);
        ConfigCenter.getInstance().reset();

        List<ViewInfo> infoList = ViewIdManager.getInstance().getViewIdInfoAsList();
        int count = infoList.size();
        mTableData = new Object[count][4];
        for (int i = 0; i < count; i++) {
            ViewInfo info = infoList.get(i);
            mTableData[i][0] = info.selected;
            mTableData[i][1] = ViewInfo.forShortType(info.type);
            mTableData[i][2] = info.id;
            mTableData[i][3] = NamingRules.forName(info.id);
        }
    }

    private void handleConfigPanelEvent() {
        rootViewTextField.setFocusable(false);
        rootViewTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                rootViewTextField.setFocusable(true);
                rootViewTextField.grabFocus();
            }
        });

        rootViewTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                try {
                    Document document = event.getDocument();
                    String rootViewText = document.getText(0, document.getLength());
                    ConfigCenter.getInstance().setRootView(rootViewText);
                    updateGenerateCode();
                } catch (Exception e) {
                }
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                try {
                    Document document = event.getDocument();
                    String rootViewText = document.getText(0, document.getLength());
                    ConfigCenter.getInstance().setRootView(rootViewText);
                    updateGenerateCode();
                } catch (Exception e) {
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        variablePrefixCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ConfigCenter.getInstance().setAddPrefixM(variablePrefixCheckBox.isSelected());
                updateTableNameColumnData();
                updateGenerateCode();
            }
        });

        api26CheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ConfigCenter.getInstance().setApi26(api26CheckBox.isSelected());
                updateGenerateCode();
            }
        });

        kotlinLazyCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean selected = kotlinLazyCheckBox.isSelected();
                if (selected) {
                    threadModePanel.setEnabled(true);
                } else {
                    threadModePanel.setEnabled(false);
                }
                ConfigCenter.getInstance().setKotlinLazy(selected);
                updateGenerateCode();
            }
        });

        synchronizedCheckBox.addChangeListener(e -> {
            if (!synchronizedCheckBox.isSelected()
                    && !noneCheckBox.isSelected()) {
                synchronizedCheckBox.setSelected(true);
                return;
            }

            if (synchronizedCheckBox.isSelected()) {
                ConfigCenter.getInstance().setSafetyMode(Config.LazyThreadSafetyMode.SYNCHRONIZED);
                updateGenerateCode();
                noneCheckBox.setSelected(false);
            }
        });

        noneCheckBox.addChangeListener(e -> {
            if (!synchronizedCheckBox.isSelected()
                    && !noneCheckBox.isSelected()) {
                noneCheckBox.setSelected(true);
                return;
            }

            if (noneCheckBox.isSelected()) {
                ConfigCenter.getInstance().setSafetyMode(Config.LazyThreadSafetyMode.NONE);
                updateGenerateCode();
                synchronizedCheckBox.setSelected(false);
            }
        });

        javaCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!javaCheckBox.isSelected() && !kotlinCheckBox.isSelected()) {
                    javaCheckBox.setSelected(true);
                    return;
                }

                if (javaCheckBox.isSelected()) {
                    kotlinLazyCheckBox.setEnabled(false);
                    threadModePanel.setEnabled(false);

                    privateCheckBox.setText("private");
                    defaultCheckBox.setText("default");
                    publicCheckBox.setText("public");
                    protectCheckBox.setText("protect");

                    kotlinCheckBox.setSelected(false);
                    ConfigCenter.getInstance().setLanguage(Config.LANGUAGE.JAVA);
                    updateGenerateCode();

                    updateInsertComboBoxStatus();
                }

            }
        });

        kotlinCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!kotlinCheckBox.isSelected() && !javaCheckBox.isSelected()) {
                    kotlinCheckBox.setSelected(true);
                    return;
                }

                if (kotlinCheckBox.isSelected()) {
                    kotlinLazyCheckBox.setEnabled(true);

                    privateCheckBox.setText("private");
                    defaultCheckBox.setText("default");
                    publicCheckBox.setText("internal");
                    protectCheckBox.setText("protect");

                    javaCheckBox.setSelected(false);
                    ConfigCenter.getInstance().setLanguage(Config.LANGUAGE.KOTLIN);
                    updateGenerateCode();

                    updateInsertComboBoxStatus();
                }
            }
        });

        memberCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!memberCheckBox.isSelected() && !localCheckBox.isSelected()) {
                    memberCheckBox.setSelected(true);
                    return;
                }

                if (memberCheckBox.isSelected()) {
                    localCheckBox.setSelected(false);
                    ConfigCenter.getInstance().setVariable(Config.VARIABLE.MEMBER);
                    updateGenerateCode();

                    privateCheckBox.setEnabled(true);
                    defaultCheckBox.setEnabled(true);
                    publicCheckBox.setEnabled(true);
                    protectCheckBox.setEnabled(true);
                }

            }
        });

        localCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!localCheckBox.isSelected() && !memberCheckBox.isSelected()) {
                    localCheckBox.setSelected(true);
                    return;
                }

                if (localCheckBox.isSelected()) {
                    memberCheckBox.setSelected(false);
                    ConfigCenter.getInstance().setVariable(Config.VARIABLE.LOCAL);
                    updateGenerateCode();

                    privateCheckBox.setEnabled(false);
                    defaultCheckBox.setEnabled(false);
                    publicCheckBox.setEnabled(false);
                    protectCheckBox.setEnabled(false);
                }
            }
        });

        privateCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!privateCheckBox.isSelected()
                        && !defaultCheckBox.isSelected()
                        && !publicCheckBox.isSelected()
                        && !protectCheckBox.isSelected()) {
                    privateCheckBox.setSelected(true);
                    return;
                }

                if (privateCheckBox.isSelected()) {
                    ConfigCenter.getInstance().setModifier(Config.MODIFIER.PRIVATE);
                    updateGenerateCode();
                    defaultCheckBox.setSelected(false);
                    publicCheckBox.setSelected(false);
                    protectCheckBox.setSelected(false);
                }

            }
        });

        defaultCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!privateCheckBox.isSelected()
                        && !defaultCheckBox.isSelected()
                        && !publicCheckBox.isSelected()
                        && !protectCheckBox.isSelected()) {
                    defaultCheckBox.setSelected(true);
                    return;
                }

                if (defaultCheckBox.isSelected()) {
                    ConfigCenter.getInstance().setModifier(Config.MODIFIER.DEFAULT);
                    updateGenerateCode();
                    privateCheckBox.setSelected(false);
                    publicCheckBox.setSelected(false);
                    protectCheckBox.setSelected(false);
                }
            }
        });

        publicCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!privateCheckBox.isSelected()
                        && !defaultCheckBox.isSelected()
                        && !publicCheckBox.isSelected()
                        && !protectCheckBox.isSelected()) {
                    publicCheckBox.setSelected(true);
                    return;
                }

                if (publicCheckBox.isSelected()) {
                    ConfigCenter.getInstance().setModifier(Config.MODIFIER.PUBLIC);
                    updateGenerateCode();
                    privateCheckBox.setSelected(false);
                    defaultCheckBox.setSelected(false);
                    protectCheckBox.setSelected(false);
                }
            }
        });

        protectCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!privateCheckBox.isSelected()
                        && !defaultCheckBox.isSelected()
                        && !publicCheckBox.isSelected()
                        && !protectCheckBox.isSelected()) {
                    protectCheckBox.setSelected(true);
                    return;
                }

                if (protectCheckBox.isSelected()) {
                    ConfigCenter.getInstance().setModifier(Config.MODIFIER.PROTECT);
                    updateGenerateCode();
                    privateCheckBox.setSelected(false);
                    defaultCheckBox.setSelected(false);
                    publicCheckBox.setSelected(false);
                }
            }
        });
    }

    private void handleViewIdPanelEvent() {
        viewIdScrollPane.setWheelScrollingEnabled(true);

        Object[] columnNames = new Object[]{"", "type", "id", "name"};
        mTableModel = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return Boolean.class;
                return Object.class;
            }

        };

        mTableModel.setDataVector(mTableData, columnNames);

        viewIdTable.setModel(mTableModel);
        viewIdTable.setDragEnabled(false);
        viewIdTable.setCellSelectionEnabled(false);
        viewIdTable.setRowSelectionAllowed(false);
        viewIdTable.setShowHorizontalLines(true);
        viewIdTable.setShowVerticalLines(true);

        TableColumn tableColumn = viewIdTable.getColumnModel().getColumn(0);
        tableColumn.setCellEditor(viewIdTable.getDefaultEditor(Boolean.class));
        tableColumn.setCellRenderer(viewIdTable.getDefaultRenderer(Boolean.class));
        tableColumn.setPreferredWidth(33);
        tableColumn.setMaxWidth(33);
        tableColumn.setMinWidth(33);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        cellRenderer.setVerticalAlignment(JLabel.CENTER);
        viewIdTable.setDefaultRenderer(Object.class, cellRenderer);

        CheckBoxHeaderRenderer headerRenderer = new CheckBoxHeaderRenderer();
        viewIdTable.getTableHeader().setReorderingAllowed(false);
        viewIdTable.getTableHeader().setDefaultRenderer(headerRenderer);

        mTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (mTableModel == null)
                    return;

                if (e.getColumn() != 0)
                    return;

                if (isSelectAllClick)
                    return;

                Boolean isSelected = (Boolean) mTableModel.getValueAt(e.getFirstRow(), 0);

                updateTableSelectColumnData(e.getFirstRow(), isSelected);

                updateGenerateCode();
            }
        });

    }

    class CheckBoxHeaderRenderer implements TableCellRenderer {
        private JTableHeader mTableHeader;
        private JCheckBox mCheckBox;

        public CheckBoxHeaderRenderer() {
            mTableHeader = viewIdTable.getTableHeader();
            mCheckBox = new JCheckBox();
            mCheckBox.setSelected(true);
            mTableHeader.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectColumn = mTableHeader.columnAtPoint(e.getPoint());
                    if (selectColumn == 0) {
                        isSelectAllClick = true;
                        mCheckBox.setSelected(!mCheckBox.isSelected());
                        mTableHeader.repaint();

                        if (mCheckBox.isSelected()) {
                            selectAll();
                        } else {
                            selectNone();
                        }

                        updateGenerateCode();
                        isSelectAllClick = false;
                    }
                }
            });
        }

        private void selectAll() {
            for (int i = 0; i < mTableModel.getRowCount(); i++) {
                mTableModel.setValueAt(true, i, 0);
                mTableData[i][0] = true;
            }
        }

        private void selectNone() {
            for (int i = 0; i < mTableModel.getRowCount(); i++) {
                mTableModel.setValueAt(false, i, 0);
                mTableData[i][0] = false;
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String v;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = "";
            }

            JLabel label = new JLabel(v);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            mCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            mCheckBox.setVerticalAlignment(SwingConstants.CENTER);
            mCheckBox.setBorderPainted(true);
            JComponent component = column == 0 ? mCheckBox : label;
            component.setForeground(mTableHeader.getForeground());
            component.setBackground(mTableHeader.getBackground());
            component.setFont(mTableHeader.getFont());
            component.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            return component;
        }
    }

    private void handleCodePanelEvent() {
        codeTextArea.setLineWrap(true);
        codeTextArea.setWrapStyleWord(true);
        codeTextArea.setEditable(false);

        classComboBox.setMaximumRowCount(5);
        methodComboBox.setMaximumRowCount(5);
        classComboBox.setEditable(false);
        methodComboBox.setEditable(false);

        CollectionComboBoxModel<String> classComboBoxModel = new CollectionComboBoxModel<>(ClassInfoManager.getInstance().getAllClassNames());
        CollectionComboBoxModel<String> methodComboBoxModel = new CollectionComboBoxModel<>();
        classComboBox.setModel(classComboBoxModel);

        try {
            classComboBox.setSelectedIndex(0);
            methodComboBoxModel.removeAll();
            methodComboBoxModel.add(ClassInfoManager.getInstance().findMethodFromTable(classComboBoxModel.getSelected()));
            methodComboBox.setModel(methodComboBoxModel);
            methodComboBox.setSelectedIndex(0);
            mClassName = classComboBoxModel.getSelected();
            mMethodName = methodComboBoxModel.getSelected();
        } catch (Exception e) {
            //ignore.
        }

        classComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED)
                    return;
                mClassName = classComboBoxModel.getSelected();

                methodComboBoxModel.removeAll();
                methodComboBoxModel.add(ClassInfoManager.getInstance().findMethodFromTable(classComboBoxModel.getSelected()));
                methodComboBox.setSelectedIndex(0);
            }
        });
        methodComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED)
                    return;
                mMethodName = methodComboBoxModel.getSelected();
            }
        });
    }

    private void updateTableNameColumnData() {
        for (int i = 0; i < mTableModel.getRowCount(); i++) {
            String name = NamingRules.forName((String) mTableData[i][2]);
            mTableModel.setValueAt(name, i, 3);
            mTableData[i][3] = name;
        }
    }

    private void updateTableSelectColumnData(int row, boolean selected) {
        if (mTableData == null)
            return;
        try {
            mTableData[row][0] = selected;
        } catch (Exception e) {
        }
    }

    private void updateInsertComboBoxStatus() {
        if (javaCheckBox.isSelected()) {
            if (classComboBox.getItemCount() == 0 || mTableData == null || mTableData.length == 0) {
                insertButton.setEnabled(false);
                classComboBox.setEnabled(false);
                methodComboBox.setEnabled(false);
                getRootPane().setDefaultButton(copyButton);
            } else {
                insertButton.setEnabled(true);
                classComboBox.setEnabled(true);
                methodComboBox.setEnabled(true);
                getRootPane().setDefaultButton(insertButton);
            }
        } else if (kotlinCheckBox.isSelected()) {
            insertButton.setEnabled(false);
            getRootPane().setDefaultButton(copyButton);
            classComboBox.setEnabled(false);
            methodComboBox.setEnabled(false);
        }
    }

    private void updateGenerateCode() {
        Pair<String, String> pair = CodeGenerateFactory.generate(mTableData);
        if (pair == null)
            return;
        codeTextArea.setText("");
        if (!StringUtil.isEmpty(pair.getFirst())) {
            codeTextArea.append(pair.getFirst());
            codeTextArea.append("\n");
        }
        codeTextArea.append(pair.getSecond());
        codeTextArea.setCaretPosition(0);
    }

}
