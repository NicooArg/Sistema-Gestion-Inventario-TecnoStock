package vista;

import dao.CategoriaDAO;
import dao.ProductoDAO;
import modelo.Categoria;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductosFrame extends JFrame {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStockMinimo;

    private JComboBox<Categoria> cmbCategoria;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;

    private int productoSeleccionado = -1;

    public ProductosFrame() {

        productoDAO = new ProductoDAO();
        categoriaDAO = new CategoriaDAO();

        setTitle("TecnoStock - Productos");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(
                new GridLayout(5, 2, 10, 10)
        );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        txtStockMinimo = new JTextField();
        cmbCategoria = new JComboBox<>();

        formulario.add(new JLabel("Código:"));
        formulario.add(txtCodigo);

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Precio:"));
        formulario.add(txtPrecio);

        formulario.add(new JLabel("Stock mínimo:"));
        formulario.add(txtStockMinimo);

        formulario.add(new JLabel("Categoría:"));
        formulario.add(cmbCategoria);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnDesactivar = new JButton("Dar de baja");
        JButton btnActualizar = new JButton("Actualizar");

        JPanel botones = new JPanel();

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnDesactivar);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "ID", "Código", "Nombre", "Precio",
                    "Stock", "Mínimo", "Categoría"
                }, 0
        );

        tabla = new JTable(modeloTabla);

        tabla.getSelectionModel().addListSelectionListener(
                e -> cargarSeleccionado()
        );

        setLayout(new BorderLayout());

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> limpiar());

        btnGuardar.addActionListener(e -> guardar());

        btnActualizar.addActionListener(e -> cargarDatos());

        btnDesactivar.addActionListener(e -> desactivar());

        cargarCategorias();
        cargarDatos();
    }

    private void cargarCategorias() {

        try {

            cmbCategoria.removeAllItems();

            for (Categoria c : categoriaDAO.listar()) {
                cmbCategoria.addItem(c);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error cargando categorías: " + e.getMessage()
            );
        }
    }

    private void cargarDatos() {

        try {

            modeloTabla.setRowCount(0);

            for (Producto p : productoDAO.listar()) {

                modeloTabla.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getCodigo(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getStock(),
                    p.getStockMinimo(),
                    p.getNombreCategoria()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error cargando productos: " + e.getMessage()
            );
        }
    }

    private void cargarSeleccionado() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return;
        }

        productoSeleccionado =
                Integer.parseInt(
                        modeloTabla.getValueAt(fila, 0).toString()
                );

        txtCodigo.setText(
                modeloTabla.getValueAt(fila, 1).toString()
        );

        txtNombre.setText(
                modeloTabla.getValueAt(fila, 2).toString()
        );

        txtPrecio.setText(
                modeloTabla.getValueAt(fila, 3).toString()
        );

        txtStockMinimo.setText(
                modeloTabla.getValueAt(fila, 5).toString()
        );

        String categoria =
                modeloTabla.getValueAt(fila, 6).toString();

        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {

            if (cmbCategoria.getItemAt(i)
                    .getNombre()
                    .equals(categoria)) {

                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardar() {

        try {

            if (txtCodigo.getText().trim().isEmpty()
                    || txtNombre.getText().trim().isEmpty()
                    || txtPrecio.getText().trim().isEmpty()
                    || txtStockMinimo.getText().trim().isEmpty()
                    || cmbCategoria.getSelectedItem() == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Complete todos los campos."
                );

                return;
            }

            Producto p = new Producto();

            p.setCodigo(txtCodigo.getText().trim());
            p.setNombre(txtNombre.getText().trim());
            p.setPrecio(
                    Double.parseDouble(txtPrecio.getText().trim())
            );
            p.setStockMinimo(
                    Integer.parseInt(txtStockMinimo.getText().trim())
            );

            Categoria c =
                    (Categoria) cmbCategoria.getSelectedItem();

            p.setIdCategoria(c.getIdCategoria());

            if (productoSeleccionado == -1) {

                productoDAO.insertar(p);

                JOptionPane.showMessageDialog(
                        this,
                        "Producto registrado correctamente."
                );

            } else {

                p.setIdProducto(productoSeleccionado);

                productoDAO.actualizar(p);

                JOptionPane.showMessageDialog(
                        this,
                        "Producto actualizado correctamente."
                );
            }

            limpiar();
            cargarDatos();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Precio y stock mínimo deben ser números."
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void desactivar() {

        if (productoSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un producto."
            );

            return;
        }

        try {

            productoDAO.desactivar(productoSeleccionado);

            JOptionPane.showMessageDialog(
                    this,
                    "Producto dado de baja."
            );

            limpiar();
            cargarDatos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void limpiar() {

        productoSeleccionado = -1;

        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStockMinimo.setText("");

        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }

        tabla.clearSelection();
    }
}