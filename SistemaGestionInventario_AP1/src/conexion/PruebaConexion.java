package conexion;

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {

        try {
            Connection conexion = ConexionBD.conectar();

            System.out.println("CONEXION EXITOSA CON MYSQL");

            conexion.close();

        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION");
            e.printStackTrace();
        }
    }
}