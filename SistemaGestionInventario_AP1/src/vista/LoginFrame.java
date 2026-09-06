package vista;

import dao.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;

    public LoginFrame() {

        setTitle("TecnoStock - Inicio de sesión");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        panel.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panel.add(txtUsuario);

        panel.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField();
        panel.add(txtContrasena);

        panel.add(new JLabel());

        JButton btnIngresar = new JButton("INGRESAR");
        panel.add(btnIngresar);

        panel.add(new JLabel("TecnoStock"));
        panel.add(new JLabel("Gestión de Inventario"));

        add(panel);

        btnIngresar.addActionListener(e -> ingresar());

        getRootPane().setDefaultButton(btnIngresar);
    }

    private void ingresar() {

        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Complete usuario y contraseña."
            );
            return;
        }

        try {

            UsuarioDAO dao = new UsuarioDAO();

            Usuario usuarioLogueado =
                    dao.login(usuario, contrasena);

            if (usuarioLogueado == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario o contraseña incorrectos."
                );

                return;
            }

            new MenuPrincipalFrame(usuarioLogueado).setVisible(true);
            dispose();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage()
            );
        }
    }
}