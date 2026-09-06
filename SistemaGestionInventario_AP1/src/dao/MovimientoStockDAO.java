package dao;

import conexion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoStockDAO {

    public void registrarEntrada(int idProducto, int cantidad, int idUsuario)
            throws SQLException {

        Connection cn = null;

        try {

            cn = ConexionBD.conectar();
            cn.setAutoCommit(false);

            String update = """
                    UPDATE producto
                    SET stock = stock + ?
                    WHERE id_producto = ?
                    """;

            try (PreparedStatement ps = cn.prepareStatement(update)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                ps.executeUpdate();
            }

            String insert = """
                    INSERT INTO movimiento_stock
                    (tipo_movimiento, cantidad, id_producto, id_usuario)
                    VALUES ('ENTRADA', ?, ?, ?)
                    """;

            try (PreparedStatement ps = cn.prepareStatement(insert)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                ps.setInt(3, idUsuario);
                ps.executeUpdate();
            }

            cn.commit();

        } catch (SQLException e) {

            if (cn != null) {
                cn.rollback();
            }

            throw e;

        } finally {

            if (cn != null) {
                cn.setAutoCommit(true);
                cn.close();
            }
        }
    }

    public void registrarSalida(int idProducto, int cantidad, int idUsuario)
            throws SQLException {

        Connection cn = null;

        try {

            cn = ConexionBD.conectar();
            cn.setAutoCommit(false);

            String update = """
                    UPDATE producto
                    SET stock = stock - ?
                    WHERE id_producto = ?
                    AND stock >= ?
                    """;

            int filas;

            try (PreparedStatement ps = cn.prepareStatement(update)) {

                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                ps.setInt(3, cantidad);

                filas = ps.executeUpdate();
            }

            if (filas == 0) {
                throw new SQLException("Stock insuficiente.");
            }

            String insert = """
                    INSERT INTO movimiento_stock
                    (tipo_movimiento, cantidad, id_producto, id_usuario)
                    VALUES ('SALIDA', ?, ?, ?)
                    """;

            try (PreparedStatement ps = cn.prepareStatement(insert)) {

                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                ps.setInt(3, idUsuario);

                ps.executeUpdate();
            }

            cn.commit();

        } catch (SQLException e) {

            if (cn != null) {
                cn.rollback();
            }

            throw e;

        } finally {

            if (cn != null) {
                cn.setAutoCommit(true);
                cn.close();
            }
        }
    }

    public List<String[]> listar() throws SQLException {

        List<String[]> lista = new ArrayList<>();

        String sql = """
                SELECT
                    m.id_movimiento,
                    m.tipo_movimiento,
                    m.cantidad,
                    m.fecha,
                    p.nombre,
                    u.nombre_usuario
                FROM movimiento_stock m
                INNER JOIN producto p ON m.id_producto = p.id_producto
                INNER JOIN usuario u ON m.id_usuario = u.id_usuario
                ORDER BY m.fecha DESC
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_movimiento")),
                    rs.getString("tipo_movimiento"),
                    String.valueOf(rs.getInt("cantidad")),
                    rs.getTimestamp("fecha").toString(),
                    rs.getString("nombre"),
                    rs.getString("nombre_usuario")
                });
            }
        }

        return lista;
    }
}