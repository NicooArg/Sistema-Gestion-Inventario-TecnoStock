package vista;

import dao.MovimientoStockDAO;
import dao.ProductoDAO;
import modelo.Producto;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MovimientosFrame extends JFrame {

    private JComboBox<ProductoCombo> cmbProducto;
    private JComboBox<String> cmbTipo;
    private JSpinner spCantidad;

    private JTable tabla;
    private DefaultTableModel modelo;

    private ProductoDAO productoDAO;
    private MovimientoStockDAO movimientoDAO;
    private Usuario usuario;

    public MovimientosFrame(Usuario usuario) {

        this.usuario = usuario;

        productoDAO = new ProductoDAO();
        movimientoDAO = new MovimientoStockDAO();

        setTitle("TecnoStock - Movimientos de stock");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(
                new GridLayout(4, 2, 10, 10)
        );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        cmbProducto = new JComboBox<>();
        cmbTipo = new JComboBox<>(
                new String[]{"ENTRADA", "SALIDA"}
        );

        spCantidad = new JSpinner(
                new SpinnerNumberModel(1, 1, 100000, 1)
        );

        JButton btnRegistrar = new JButton(
                "Registrar movimiento"
        );

        formulario.add(new JLabel("Producto:"));
        formulario.add(cmbProducto);

        formulario.add(new JLabel("Tipo:"));
        formulario.add(cmbTipo);

        formulario.add(new JLabel("Cantidad:"));
        formulario.add(spCantidad);

        formulario.add(new JLabel());
        formulario.add(btnRegistrar);

        modelo = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Tipo",
                    "Cantidad",
                    "Fecha",
                    "Producto",
                    "Usuario"
                }, 0
        );

        tabla = new JTable(modelo);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnRegistrar.addActionListener(
                e -> registrar()
        );

        cargarProductos();
        cargarMovimientos();
    }
    
    private void cargarProductos() {
        try {
            cmbProducto.removeAllItems();
            for (Producto p : productoDAO.listar()) {
                cmbProducto.addItem(
                        new ProductoCombo(
                                p.getIdProducto(),
                                p.getNombre()
                        )
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void registrar() {

        try {

            ProductoCombo p =
                    (ProductoCombo) cmbProducto.getSelectedItem();

            if (p == null) {
                return;
            }

            int cantidad =
                    Integer.parseInt(
                            spCantidad.getValue().toString()
                    );

            String tipo =
                    cmbTipo.getSelectedItem().toString();

            if (tipo.equals("ENTRADA")) {

                movimientoDAO.registrarEntrada(
                        p.id,
                        cantidad,
                        usuario.getIdUsuario()
                );

            } else {

                movimientoDAO.registrarSalida(
                        p.id,
                        cantidad,
                        usuario.getIdUsuario()
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Movimiento registrado correctamente."
            );

            cargarMovimientos();
            cargarProductos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void cargarMovimientos() {

        try {

            modelo.setRowCount(0);

            for (String[] fila : movimientoDAO.listar()) {
                modelo.addRow(fila);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private static class ProductoCombo {
        private int id;
        private String nombre;
        
        public ProductoCombo(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        
        @Override
        public String toString() {
            return nombre;
        }
    }
}