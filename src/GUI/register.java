package GUI;

import javax.swing.*;
import java.awt.*;
import BackEnd.RegisterService;

public final class register extends javax.swing.JFrame {

    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtAccountCode;
    private JTextField txtCaptcha;
    private JButton btnReloadCaptcha;
    private JButton btnRegister;
    private JButton btnBack;
    private JCheckBox chkShowPassword;
    private JCheckBox chkShowConfirmPassword;
    private JLabel lblTitle;
    private JLabel lblCaptchaImage; // Label để hiện ảnh captcha

    public register() {
        initComponents();
        RegisterService.loadRandomCaptcha(lblCaptchaImage);
        btnReloadCaptcha.addActionListener(e -> RegisterService.changeCaptcha(lblCaptchaImage));
        btnRegister.addActionListener(e -> RegisterService.registerUser(
            txtFullName, txtUsername, txtPassword, txtConfirmPassword, txtEmail, txtPhone, txtAddress, txtAccountCode, txtCaptcha, this
        ));
        btnRegister.getRootPane().setDefaultButton(btnRegister);
        btnBack.addActionListener(e -> RegisterService.goBack(this));
        chkShowPassword.addActionListener(e -> RegisterService.togglePasswordVisibility(txtPassword, chkShowPassword));
        chkShowConfirmPassword.addActionListener(e -> RegisterService.togglePasswordVisibility(txtConfirmPassword, chkShowConfirmPassword));
    }

    private void initComponents() {
        JPanel panelMain = new JPanel();
        panelMain.setLayout(null);

        lblTitle = new JLabel("Đăng ký");
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblTitle.setBounds(220, 10, 150, 30);
        panelMain.add(lblTitle);

        btnBack = new JButton("Quay lại");
        btnBack.setBounds(10, 10, 90, 30);
        panelMain.add(btnBack);

        JLabel lblFullName = new JLabel("Tên đầy đủ:");
        lblFullName.setBounds(50, 60, 100, 25);
        panelMain.add(lblFullName);

        txtFullName = new JTextField();
        txtFullName.setBounds(180, 60, 250, 25);
        panelMain.add(txtFullName);

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setBounds(50, 100, 100, 25);
        panelMain.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(180, 100, 250, 25);
        panelMain.add(txtUsername);

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setBounds(50, 140, 100, 25);
        panelMain.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180, 140, 250, 25);
        panelMain.add(txtPassword);

        chkShowPassword = new JCheckBox("Hiển thị mật khẩu");
        chkShowPassword.setBounds(440, 140, 150, 25);
        panelMain.add(chkShowPassword);

        JLabel lblConfirmPassword = new JLabel("Nhập lại mật khẩu:");
        lblConfirmPassword.setBounds(50, 180, 130, 25);
        panelMain.add(lblConfirmPassword);

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setBounds(180, 180, 250, 25);
        panelMain.add(txtConfirmPassword);

        chkShowConfirmPassword = new JCheckBox("Hiển thị mật khẩu");
        chkShowConfirmPassword.setBounds(440, 180, 150, 25);
        panelMain.add(chkShowConfirmPassword);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 220, 100, 25);
        panelMain.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(180, 220, 250, 25);
        panelMain.add(txtEmail);

        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setBounds(50, 260, 100, 25);
        panelMain.add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(180, 260, 250, 25);
        panelMain.add(txtPhone);

        JLabel lblAddress = new JLabel("Địa chỉ:");
        lblAddress.setBounds(50, 300, 100, 25);
        panelMain.add(lblAddress);

        txtAddress = new JTextField();
        txtAddress.setBounds(180, 300, 250, 25);
        panelMain.add(txtAddress);

        JLabel lblAccountCode = new JLabel("Tạo mã tài khoản:");
        lblAccountCode.setBounds(50, 340, 100, 25);
        panelMain.add(lblAccountCode);

        txtAccountCode = new JTextField();
        txtAccountCode.setBounds(180, 340, 250, 25);
        panelMain.add(txtAccountCode);

        JLabel lblCaptcha = new JLabel("Captcha:");
        lblCaptcha.setBounds(50, 380, 100, 25);
        panelMain.add(lblCaptcha);

        txtCaptcha = new JTextField();
        txtCaptcha.setBounds(180, 380, 150, 25);
        panelMain.add(txtCaptcha);

        // Ảnh captcha
        lblCaptchaImage = new JLabel();
        lblCaptchaImage.setBounds(340, 370, 120, 50);
        lblCaptchaImage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelMain.add(lblCaptchaImage);

        btnReloadCaptcha = new JButton("Đổi captcha");
        btnReloadCaptcha.setBounds(470, 380, 100, 25);
        panelMain.add(btnReloadCaptcha);

        btnRegister = new JButton("Đăng ký");
        btnRegister.setBounds(200, 440, 120, 40);
        panelMain.add(btnRegister);
        panelMain.setBackground(new Color(200, 173, 127));

        this.add(panelMain);
        this.setSize(600, 550);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Đăng ký tài khoản");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new register().setVisible(true));
    }
}
