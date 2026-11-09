
package servicio;

import dao.MovimientoStockDAO;
import java.sql.SQLException;

public abstract class OperacionStock {

    private final int idProducto;
    private final int cantidad;
    private final int idUsuario;

    protected OperacionStock(
            int idProducto, int cantidad, int idUsuario) {

        if (idProducto <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un producto válido.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        if (idUsuario <= 0) {
            throw new IllegalArgumentException(
                    "El usuario no es válido.");
        }

        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.idUsuario = idUsuario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public abstract void registrar(
            MovimientoStockDAO dao) throws SQLException;

    public abstract String getDescripcion();
}

