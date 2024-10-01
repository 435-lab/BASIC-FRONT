package org.example.register;

import org.example.login.LoginUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class RegisterUI extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JPasswordField passwordConfirmField;
    private JTextField nameField;
    private JComboBox<Integer> yearCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> dayCombo;
    private JRadioButton maleButton;
    private JRadioButton femaleButton;
    private JButton registerButton;
    private JButton backButton;

    public RegisterUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("회원가입");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(173, 216, 230));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(173, 216, 230));
        add(mainPanel, BorderLayout.CENTER);

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, new GridBagConstraints());

        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("맑은 고딕", Font.BOLD, 15);

        addTitleToForm(formPanel, gbc);
        addFormFields(formPanel, gbc, labelFont);
        addDateFields(formPanel, gbc, labelFont);
        addGenderFields(formPanel, gbc, labelFont);
        addButtons(formPanel, gbc);

        return formPanel;
    }

    private void addTitleToForm(JPanel panel, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);
    }

    private void addFormFields(JPanel panel, GridBagConstraints gbc, Font labelFont) {
        gbc.gridwidth = 1;
        gbc.gridy++;
        addFormField(panel, gbc, "아이디:", idField = new JTextField(30), labelFont);
        gbc.gridy++;
        addFormField(panel, gbc, "비밀번호:", passwordField = new JPasswordField(30), labelFont);
        gbc.gridy++;
        addFormField(panel, gbc, "비밀번호 확인:", passwordConfirmField = new JPasswordField(30), labelFont);
        gbc.gridy++;
        addFormField(panel, gbc, "이름:", nameField = new JTextField(30), labelFont);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, Font labelFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(field, gbc);
    }

    private void addDateFields(JPanel panel, GridBagConstraints gbc, Font labelFont) {
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel birthLabel = new JLabel("생년월일:");
        birthLabel.setFont(labelFont);
        panel.add(birthLabel, gbc);

        yearCombo = new JComboBox<>();
        monthCombo = new JComboBox<>();
        dayCombo = new JComboBox<>();

        initDateComboBoxes();

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBackground(Color.WHITE);
        datePanel.add(yearCombo);
        datePanel.add(new JLabel("년"));
        datePanel.add(monthCombo);
        datePanel.add(new JLabel("월"));
        datePanel.add(dayCombo);
        datePanel.add(new JLabel("일"));

        gbc.gridx = 1;
        panel.add(datePanel, gbc);
    }

    private void initDateComboBoxes() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= 1900; year--) {
            yearCombo.addItem(year);
        }

        String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        for (String month : months) {
            monthCombo.addItem(month);
        }

        for (int day = 1; day <= 31; day++) {
            dayCombo.addItem(day);
        }
    }

    private void addGenderFields(JPanel panel, GridBagConstraints gbc, Font labelFont) {
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel genderLabel = new JLabel("성별:");
        genderLabel.setFont(labelFont);
        panel.add(genderLabel, gbc);

        maleButton = new JRadioButton("남성");
        femaleButton = new JRadioButton("여성");

        Font radioButtonFont = new Font("맑은 고딕", Font.PLAIN, 14);
        maleButton.setFont(radioButtonFont);
        femaleButton.setFont(radioButtonFont);

        maleButton.setBackground(Color.WHITE);
        femaleButton.setBackground(Color.WHITE);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(Color.WHITE);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);

        gbc.gridx = 1;
        panel.add(genderPanel, gbc);
    }

    private void addButtons(JPanel panel, GridBagConstraints gbc) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        registerButton = new JButton("가입하기");
        registerButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        registerButton.setBackground(new Color(173, 216, 230));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(registerButton);


        backButton = new JButton("뒤로가기");
        backButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        backButton.setBackground(new Color(181, 181, 181));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> {
            dispose();  // 현재 창을 닫고
            new LoginUI();  // 로그인 창을 열기
        });
        buttonPanel.add(backButton);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
    }

    public void setRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    // Getter methods
    public String getId() { return idField.getText(); }
    public char[] getPassword() { return passwordField.getPassword(); }
    public char[] getPasswordConfirm() { return passwordConfirmField.getPassword(); }
    public String getName() { return nameField.getText(); }
    public String getBirthDate() {
        int year = (int) yearCombo.getSelectedItem();
        int month = monthCombo.getSelectedIndex() + 1;
        int day = (int) dayCombo.getSelectedItem();
        return String.format("%04d-%02d-%02d", year, month, day);
    }
    public String getGender() {
        return maleButton.isSelected() ? "M" : (femaleButton.isSelected() ? "F" : "");
    }
}