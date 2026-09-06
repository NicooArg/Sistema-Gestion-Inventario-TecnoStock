package dao;

import conexion.ConexionBD;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario login(String usuario, String contrasena) throws SQLException {

        String sql = """
                SELECT u.*, r.nombre AS nombre_rol
                FROM usuario u
                INNER JOIN rol r ON u.id_rol = r.id_rol
                WHERE u.nombre_usuario = ?
                AND u.contrasena = ?
                AND u.activo = TRUE
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Usuario u = new Usuario();

                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombreUsuario(rs.getString("nombre_usuario"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    u.setActivo(rs.getBoolean("activo"));
                    u.setIdRol(rs.getInt("id_rol"));
                    u.setNombreRol(rs.getString("nombre_rol"));

                    return u;
                }
            }
        }

        return null;
    }

    public List<Usuario> listar() throws SQLException {

        List<Usuario> lista = new ArrayList<>();

        String sql = """
                SELECT u.*, r.nombre AS nombre_rol
                FROM usuario u
                INNER JOIN rol r ON u.id_rol = r.id_rol
                ORDER BY u.nombre_usuario
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Usuario u = new Usuario();

                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setActivo(rs.getBoolean("activo"));
                u.setIdRol(rs.getInt("id_rol"));
                u.setNombreRol(rs.getString("nombre_rol"));

                lista.add(u);
            }
        }

        return lista;
    }

    public void insertar(Usuario u) throws SQLException {

        String sql = """
                INSERT INTO usuario
                (nombre_usuario, contrasena, nombre_completo, activo, id_rol)
                VALUES (?, ?, ?, TRUE, ?)
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getNombreCompleto());
            ps.setInt(4, u.getIdRol());

            ps.executeUpdate();
        }
    }

    public void actualizar(Usuario u) throws SQLException {

        String sql = """
                UPDATE usuario
                SET nombre_usuario=?, nombre_completo=?, id_rol=?
                WHERE id_usuario=?
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getNombreCompleto());
            ps.setInt(3, u.getIdRol());
            ps.setInt(4, u.getIdUsuario());

            ps.executeUpdate();
        }
    }

    public void desactivar(int id) throws SQLException {

        String sql = "UPDATE usuario SET activo=FALSE WHERE id_usuario=?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}