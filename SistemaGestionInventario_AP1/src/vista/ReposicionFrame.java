package vista;

import dao.ProductoDAO;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReposicionFrame extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;

    public ReposicionFrame() {

        setTitle("TecnoStock - Productos para reposición");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modelo = new DefaultTableModel(
                new Object[]{
                    "Código",
                    "Producto",
                    "Stock",
                    "Stock mínimo",
                    "Categoría"
                }, 0
        );

        tabla = new JTable(modelo);

        JButton btnActualizar =
                new JButton("Actualizar");

        btnActualizar.addActionListener(
                e -> cargar()
        );

        add(btnActualizar, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        cargar();
    }

    private void cargar() {

        try {

            modelo.setRowCount(0);

            ProductoDAO dao = new ProductoDAO();

            for (Producto p : dao.listarStockBajo()) {

                modelo.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getStock(),
                    p.getStockMinimo(),
                    p.getNombreCategoria()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }
}