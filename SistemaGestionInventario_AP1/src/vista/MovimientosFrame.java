package vista;

import controlador.MovimientoStockControlador;
import modelo.Producto;
import modelo.Usuario;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MovimientosFrame extends JFrame {

    private JComboBox<ProductoCombo> cmbProducto;
    private JComboBox<String> cmbTipo;
    private JSpinner spCantidad;

    private JTable tabla;
    private DefaultTableModel modelo;

    private MovimientoStockControlador controlador;
    private Usuario usuario;

    public MovimientosFrame(Usuario usuario) {

        this.usuario = usuario;
        controlador = new MovimientoStockControlador();

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
        JButton btnExportar = new JButton("Exportar historial");
        JButton btnImportar = new JButton("Importar historial");
        JPanel panelArchivos = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelArchivos.add(btnExportar);
        panelArchivos.add(btnImportar);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelArchivos, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(
                e -> registrar()
        );

        btnExportar.addActionListener(
                e -> exportarHistorial()
        );

        btnImportar.addActionListener(
                e -> importarHistorial()
        );

        cargarProductos();
        cargarMovimientos();
    }

    private void cargarProductos() {
        try {
            cmbProducto.removeAllItems();
            for (Producto p : controlador.listarProductos()) {
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

            ProductoCombo p
                    = (ProductoCombo) cmbProducto.getSelectedItem();

            if (p == null) {
                JOptionPane.showMessageDialog(
                        this, "Debe seleccionar un producto.");
                return;
            }

            int cantidad
                    = Integer.parseInt(
                            spCantidad.getValue().toString()
                    );

            String tipo
                    = cmbTipo.getSelectedItem().toString();

            String mensaje = controlador.registrarMovimiento(
                    p.id,
                    cantidad,
                    usuario.getIdUsuario(),
                    tipo
            );
            JOptionPane.showMessageDialog(
                    this,
                    mensaje
            );

            cargarMovimientos();
            cargarProductos();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Datos no válidos: " + e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            String detalle = e.getMessage() == null
                    ? ""
                    : e.getMessage();

            if (detalle.toLowerCase().contains("stock insuficiente")) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se puede registrar la salida porque "
                        + "no hay stock suficiente.",
                        "Stock insuficiente",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar el movimiento "
                        + "en la base de datos.\nDetalle: "
                        + detalle,
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cargarMovimientos() {

        try {

            modelo.setRowCount(0);

            for (String[] fila : controlador.listarMovimientos()) {
                modelo.addRow(fila);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    /**
     * Permite guardar el historial de movimientos en formato CSV.
     */
    private void exportarHistorial() {
        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle("Guardar historial de movimientos");

        selector.setSelectedFile(
                new File("historial_movimientos.csv")
        );

        int resultado = selector.showSaveDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            archivo = new File(archivo.getAbsolutePath() + ".csv");
        }

        try {

            controlador.exportarHistorial(archivo);

            JOptionPane.showMessageDialog(
                    this,
                    "El historial se exportó correctamente.",
                    "Exportación completada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo consultar el historial en MySQL: "
                    + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar el archivo: " + e.getMessage(),
                    "Error de archivo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Permite cargar un historial previamente guardado en CSV.
     */
    private void importarHistorial() {

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle("Seleccionar historial de movimientos");

        int resultado = selector.showOpenDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        try {

            ArrayList<String[]> movimientos
                    = controlador.importarHistorial(archivo);

            modelo.setRowCount(0);

            for (String[] fila : movimientos) {
                modelo.addRow(fila);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Se recuperaron " + movimientos.size()
                    + " movimientos desde el archivo.\n"
                    + "Los registros se muestran para consulta "
                    + "y no modifican la base de datos.",
                    "Importación completada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo recuperar el archivo: " + e.getMessage(),
                    "Error de archivo",
                    JOptionPane.ERROR_MESSAGE
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
