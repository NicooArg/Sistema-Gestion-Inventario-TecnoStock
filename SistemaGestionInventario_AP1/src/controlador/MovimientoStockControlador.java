
package controlador;

import dao.ProductoDAO;
import modelo.Producto;
import servicio.EntradaStock;
import servicio.MovimientoStockServicio;
import servicio.OperacionStock;
import servicio.SalidaStock;
import servicio.ArchivoMovimientosServicio;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador encargado de coordinar las operaciones
 * relacionadas con los movimientos de stock.
 */
public class MovimientoStockControlador {

    private final ProductoDAO productoDAO;
    private final MovimientoStockServicio movimientoServicio;
    private final ArchivoMovimientosServicio archivoServicio;

    public MovimientoStockControlador() {
        this.productoDAO = new ProductoDAO();
        this.movimientoServicio = new MovimientoStockServicio();
        this.archivoServicio = new ArchivoMovimientosServicio();
    }

    /**
     * Obtiene los productos disponibles para la interfaz.
     */
    public List<Producto> listarProductos() throws SQLException {
        return productoDAO.listar();
    }

    /**
     * Obtiene el historial de movimientos de stock.
     */
    public List<String[]> listarMovimientos() throws SQLException {
        return movimientoServicio.listar();
    }
    
    /**
     * Exporta el historial actual de MySQL a un archivo CSV.
     */
    public void exportarHistorial(File archivo)
        throws SQLException, IOException {
        
        List<String[]> movimientos = listarMovimientos();
        archivoServicio.exportar(archivo, movimientos);
    }
    
    /**
     * Recupera movimientos desde un archivo CSV.
     * Los datos se devuelven para consulta y no se insertan en MySQL.
     */
    public ArrayList<String[]> importarHistorial(File archivo)
        throws IOException {
        return archivoServicio.importar(archivo);
    }


    /**
     * Crea y registra una operación según el tipo seleccionado.
     */
    public String registrarMovimiento(
            int idProducto,
            int cantidad,
            int idUsuario,
            String tipo) throws SQLException {

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un tipo de movimiento.");
        }

        OperacionStock operacion;

        if ("ENTRADA".equalsIgnoreCase(tipo.trim())) {

            operacion = new EntradaStock(
                    idProducto,
                    cantidad,
                    idUsuario
            );

        } else if ("SALIDA".equalsIgnoreCase(tipo.trim())) {

            operacion = new SalidaStock(
                    idProducto,
                    cantidad,
                    idUsuario
            );

        } else {

            throw new IllegalArgumentException(
                    "El tipo de movimiento seleccionado no es valido.");
        }

        movimientoServicio.registrar(operacion);

        return operacion.getDescripcion()
                + " se registro correctamente.";
    }
}
