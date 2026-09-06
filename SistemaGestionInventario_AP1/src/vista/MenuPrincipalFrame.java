package vista;

import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {

    private Usuario usuario;

    public MenuPrincipalFrame(Usuario usuario) {

        this.usuario = usuario;

        setTitle("TecnoStock - Menú principal");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel titulo = new JLabel(
                "SISTEMA DE GESTIÓN DE INVENTARIO",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel usuarioLabel = new JLabel(
                "Usuario: " + usuario.getNombreUsuario()
                + " | Rol: " + usuario.getNombreRol(),
                SwingConstants.CENTER
        );

        JPanel botones = new JPanel(
                new GridLayout(4, 2, 10, 10)
        );

        botones.setBorder(
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        );

        JButton btnProductos = new JButton("Productos");
        JButton btnCategorias = new JButton("Categorías");
        JButton btnMovimientos = new JButton("Movimientos");
        JButton btnInventario = new JButton("Inventario");
        JButton btnReposicion = new JButton("Reposición");
        JButton btnUsuarios = new JButton("Usuarios");
        JButton btnSalir = new JButton("Cerrar sesión");

        botones.add(btnProductos);
        botones.add(btnCategorias);
        botones.add(btnMovimientos);
        botones.add(btnInventario);
        botones.add(btnReposicion);
        botones.add(btnUsuarios);
        botones.add(btnSalir);

        btnProductos.addActionListener(
                e -> new ProductosFrame().setVisible(true)
        );

        btnCategorias.addActionListener(
                e -> new CategoriasFrame().setVisible(true)
        );

        btnMovimientos.addActionListener(
                e -> new MovimientosFrame(usuario).setVisible(true)
        );

        btnInventario.addActionListener(
                e -> new ProductosFrame().setVisible(true)
        );

        btnReposicion.addActionListener(
                e -> new ReposicionFrame().setVisible(true)
        );

        btnUsuarios.addActionListener(
                e -> new UsuariosFrame().setVisible(true)
        );

        btnSalir.addActionListener(e -> {

            new LoginFrame().setVisible(true);
            dispose();

        });

        JPanel superior = new JPanel(
                new GridLayout(2, 1)
        );

        superior.add(titulo);
        superior.add(usuarioLabel);

        setLayout(new BorderLayout());

        add(superior, BorderLayout.NORTH);
        add(botones, BorderLayout.CENTER);

        // Usuarios únicamente para administrador
        btnUsuarios.setEnabled(
                "ADMINISTRADOR".equalsIgnoreCase(usuario.getNombreRol())
        );
    }
}