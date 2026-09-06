package vista;

import conexion.ConexionBD;
import dao.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuariosFrame extends JFrame {

    private JTextField txtUsuario;
    private JTextField txtNombre;
    private JPasswordField txtContrasena;

    private JComboBox<RolCombo> cmbRol;

    private JTable tabla;
    private DefaultTableModel modelo;

    private UsuarioDAO dao;
    private int seleccionado = -1;

    public UsuariosFrame() {

        dao = new UsuarioDAO();

        setTitle("TecnoStock - Usuarios");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario =
                new JPanel(new GridLayout(5, 2, 10, 10));

        formulario.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        txtUsuario = new JTextField();
        txtNombre = new JTextField();
        txtContrasena = new JPasswordField();
        cmbRol = new JComboBox<>();

        formulario.add(new JLabel("Usuario:"));
        formulario.add(txtUsuario);

        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtContrasena);

        formulario.add(new JLabel("Nombre completo:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Rol:"));
        formulario.add(cmbRol);

        JButton btnGuardar =
                new JButton("Guardar");

        JButton btnBaja =
                new JButton("Dar de baja");

        formulario.add(btnGuardar);
        formulario.add(btnBaja);

        modelo = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Usuario",
                    "Nombre",
                    "Rol",
                    "Activo"
                }, 0
        );

        tabla = new JTable(modelo);

        tabla.getSelectionModel().addListSelectionListener(
                e -> seleccionar()
        );

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnGuardar.addActionListener(
                e -> guardar()
        );

        btnBaja.addActionListener(
                e -> desactivar()
        );

        cargarRoles();
        cargar();
    }

    private void cargarRoles() {

        try {

            cmbRol.removeAllItems();

            String sql =
                    "SELECT id_rol, nombre FROM rol ORDER BY nombre";

            try (Connection cn = ConexionBD.conectar();
                 PreparedStatement ps =
                         cn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    cmbRol.addItem(
                            new RolCombo(
                                    rs.getInt("id_rol"),
                                    rs.getString("nombre")
                            )
                    );
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error cargando roles: " + e.getMessage()
            );
        }
    }

    private void cargar() {

        try {

            modelo.setRowCount(0);

            for (Usuario u : dao.listar()) {

                modelo.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getNombreUsuario(),
                    u.getNombreCompleto(),
                    u.getNombreRol(),
                    u.isActivo()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void seleccionar() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return;
        }

        seleccionado =
                Integer.parseInt(
                        modelo.getValueAt(fila, 0).toString()
                );

        txtUsuario.setText(
                modelo.getValueAt(fila, 1).toString()
        );

        txtNombre.setText(
                modelo.getValueAt(fila, 2).toString()
        );
    }

    private void guardar() {

        try {

            String usuario =
                    txtUsuario.getText().trim();

            String nombre =
                    txtNombre.getText().trim();

            String contrasena =
                    new String(txtContrasena.getPassword());

            RolCombo rol =
                    (RolCombo) cmbRol.getSelectedItem();

            if (usuario.isEmpty()
                    || nombre.isEmpty()
                    || rol == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Complete los campos."
                );

                return;
            }

            Usuario u = new Usuario();

            u.setNombreUsuario(usuario);
            u.setNombreCompleto(nombre);
            u.setIdRol(rol.id);

            if (seleccionado == -1) {

                if (contrasena.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Ingrese una contraseña."
                    );

                    return;
                }

                u.setContrasena(contrasena);

                dao.insertar(u);

            } else {

                u.setIdUsuario(seleccionado);

                dao.actualizar(u);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Usuario guardado correctamente."
            );

            limpiar();
            cargar();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void desactivar() {

        if (seleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un usuario."
            );

            return;
        }

        try {

            dao.desactivar(seleccionado);

            JOptionPane.showMessageDialog(
                    this,
                    "Usuario dado de baja."
            );

            limpiar();
            cargar();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void limpiar() {

        seleccionado = -1;

        txtUsuario.setText("");
        txtNombre.setText("");
        txtContrasena.setText("");

        tabla.clearSelection();
    }

    private static class RolCombo {

        private int id;
        private String nombre;

        public RolCombo(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}