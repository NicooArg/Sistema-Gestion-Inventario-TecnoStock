package vista;

import dao.CategoriaDAO;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CategoriasFrame extends JFrame {

    private JTextField txtNombre;
    private JTable tabla;
    private DefaultTableModel modelo;
    private CategoriaDAO dao;

    private int categoriaSeleccionada = -1;

    public CategoriasFrame() {

        dao = new CategoriaDAO();

        setTitle("TecnoStock - Categorías");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        txtNombre = new JTextField();

        JPanel superior = new JPanel(new GridLayout(2, 2, 10, 10));

        superior.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        superior.add(new JLabel("Nombre:"));
        superior.add(txtNombre);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnBaja = new JButton("Dar de baja");

        superior.add(btnGuardar);
        superior.add(btnBaja);

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre"}, 0
        );

        tabla = new JTable(modelo);

        tabla.getSelectionModel().addListSelectionListener(
                e -> seleccionar()
        );

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());

        btnBaja.addActionListener(e -> desactivar());

        cargar();
    }

    private void cargar() {

        try {

            modelo.setRowCount(0);

            for (Categoria c : dao.listar()) {

                modelo.addRow(new Object[]{
                    c.getIdCategoria(),
                    c.getNombre()
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

        categoriaSeleccionada =
                Integer.parseInt(
                        modelo.getValueAt(fila, 0).toString()
                );

        txtNombre.setText(
                modelo.getValueAt(fila, 1).toString()
        );
    }

    private void guardar() {

        try {

            String nombre = txtNombre.getText().trim();

            if (nombre.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ingrese un nombre."
                );

                return;
            }

            if (categoriaSeleccionada == -1) {
                dao.insertar(nombre);
            } else {
                dao.actualizar(categoriaSeleccionada, nombre);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Categoría guardada correctamente."
            );

            categoriaSeleccionada = -1;
            txtNombre.setText("");

            cargar();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void desactivar() {

        if (categoriaSeleccionada == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una categoría."
            );

            return;
        }

        try {

            dao.desactivar(categoriaSeleccionada);

            JOptionPane.showMessageDialog(
                    this,
                    "Categoría dada de baja."
            );

            categoriaSeleccionada = -1;
            txtNombre.setText("");

            cargar();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }
}