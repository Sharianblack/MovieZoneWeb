

import dao.ConexionDB;
import dao.PeliculaDAO;
import model.PeliculaGuardada;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConexion {

    public static void main(String[] args) {
        System.out.println("--- Iniciando prueba de conexion ---");

        // 1. Prueba de fuego: ¿Hay conexión?
        try (Connection con = ConexionDB.obtenerConexion()) {
            if (con != null) {
                System.out.println("¡Coronamos! Conexion establecida con PostgreSQL sin problemas.");
            } else {
                System.out.println("Fallo al conectar. Revisa tus credenciales en ConexionDB o revisa si pgAdmin esta abierto.");
                return; // Si no hay conexión, paramos el programa aquí
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar cerrar la conexión.");
            e.printStackTrace();
        }

        System.out.println("\n--- Iniciando prueba del DAO (Insertar) ---");

        // 2. Prueba del DAO: Vamos a intentar guardar una película
        PeliculaDAO dao = new PeliculaDAO();
        PeliculaGuardada peliPrueba = new PeliculaGuardada();

        // Inventamos unos datos temporales
        // OJO: Si corres este código 2 veces, te va a dar error porque pusimos que idExternoApi sea UNIQUE en la base de datos.
        // Si quieres correrlo de nuevo, cámbiale el número.
        peliPrueba.setIdExternoApi(999);
        peliPrueba.setTitulo("Pelicula de Prueba Test");
        peliPrueba.setCategoriaLocal("Test/Accion");

        boolean fueGuardado = dao.guardarPeliculaLocal(peliPrueba);

        if (fueGuardado) {
            System.out.println("¡Belleza! La pelicula se inserto correctamente en tu tabla peliculas_guardadas.");
        } else {
            System.out.println("Ups... Falló el insert. Revisa los errores rojos de arriba.");
        }
    }
}