/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author A7-PC00
 */
public class BBDD {

    static boolean logged = false;
    static String loggedID = null;
    static Scanner scan = new Scanner(System.in);

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String cadenaConexion = "jdbc:oracle:thin:@localhost:1521/XE";
            Connection conexion = DriverManager.getConnection(cadenaConexion, "usuario", "usuario");
            Statement stmt = conexion.createStatement();
            while (!logged) {
                System.out.println("Escribe (R) para Registrar a una persona o (I) para Iniciar Sesión");
                switch (scan.nextLine().toUpperCase()) {
                    case "R":
                        stmt.executeQuery(registerPerson(stmt));
                        break;
                    case "I":
                        logged = loginPerson(stmt);
                        break;
                    default:
                        System.err.println("Asegurate de introducir (R) o (I) correctamente");
                        break;
                }
            }
            while (logged) {
                System.out.println("Elige una opción:\n(M) Modificar datos de usuario\n(L) Lista y Vista Detalle\n(B) Búsqueda de productos\n(C) Comprar Producto\n(F) Finalizar Sesión");

                switch (scan.nextLine().toUpperCase()) {
                    case "M":
                        modifyUser(stmt);
                        break;
                    case "L":
                        listInfo(stmt);
                        break;
                    case "B":
                        break;
                    case "C":
                        break;
                    case "F":
                        closeSession();
                        break;
                    default:
                        System.err.println("Asegurate de introducir una opción correctamente");
                        break;
                }
            }

            //6. Cerrar conexion
            stmt.close();
            conexion.close();

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static String registerPerson(Statement stmt) throws SQLException {
        int rndm = (int) (Math.random() * Math.pow(10, 10));
        while (stmt.executeQuery("Select ID_Person from Person where ID_Person = " + rndm).next()) {
            rndm = (int) (Math.random() * Math.pow(10, 10));
        }
        System.out.println("ID: " + rndm);
        System.out.println("Escribe el nombre");
        String name = scan.nextLine();
        System.out.println("Escribe los apellidos");
        String surnames = scan.nextLine();
        System.out.println("Escribe el email");
        String email = scan.nextLine();
        try {
            while (stmt.executeQuery("Select Email from Person where Email = '" + email + "'").next()) {
                System.err.println("Ese correo ya está registrado");
                email = scan.nextLine();
            }
        } catch (SQLException e) {
        }
        System.out.println("Escribe la dirección");
        String adress = scan.nextLine().toLowerCase();
        System.out.println("Escribe el teléfono");
        String phone = scan.nextLine();
        System.out.println("Escribe el usuario (Es sensible a las mayúsculas y minúsculas)");
        String username = scan.nextLine();
        try {
            while (stmt.executeQuery("Select Username from Person where Username = '" + username + "'").next()) {
                System.err.println("Ese Usuario ya está registrado");
                email = scan.nextLine();
            }
        } catch (SQLException e) {
        }
        System.out.println("Escribe la contraseña");
        String password = scan.nextLine();
        System.out.println("Escribe la fecha de nacimiento");
        String birth_date = scan.nextLine();
        System.out.println("Escribe el género");
        String gender = scan.nextLine();
        return "INSERT INTO person (ID_Person, Name, Surnames, Email, Adress, Phone, Username, Password, Birth_Date, Gender)" + "Values( '" + rndm + "', '" + name + "', '" + surnames + "', '" + email + "', '" + adress + "', '" + phone + "', '" + username + "', '" + password + "', DATE '" + birth_date + "', '" + gender + "')";
    }

    private static boolean loginPerson(Statement stmt) throws SQLException {
        System.out.println("Escribe el usuario o email");
        String useremail = scan.nextLine();
        try {
            if (!stmt.executeQuery("Select Email from Person where Email = '" + useremail.toLowerCase() + "'").next() && !stmt.executeQuery("Select Username from Person where Username = '" + useremail + "'").next()) {
                System.err.println("El Usuario o Email introducido no está registrado");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("El Usuario o Email introducido no está registrado\n" + e);
            return false;
        }
        System.out.println("Escribe la contraseña");
        String password = scan.nextLine();
        try {
            for (int i = 0; i < 3; i++) {
                if (stmt.executeQuery("Select Password from Person where Password = '" + password + "' and (Email = '" + useremail.toLowerCase() + "' or Username = '" + useremail + "')").next()) {
                    ResultSet res = stmt.executeQuery("Select ID_Person from Person where Password = '" + password + "' and (Email = '" + useremail.toLowerCase() + "' or Username = '" + useremail + "')");
                    loggedID = res.next() ? res.getString(1) : null;
                    return true;
                }
                System.err.println("La contraseña es incorrecta");
                password = scan.nextLine();
            }
            System.err.println("Has fallado muchas veces la contraseña");
            return false;
        } catch (SQLException e) {
            System.err.println("La contraseña es incorrecta\n" + e);
            return false;
        }
    }

    private static void modifyUser(Statement stmt) throws SQLException {
        System.out.println("Elige qué quieres modificar:\n(N) Nombre\n(A) Apellidos\n(E) Email\n(D) Dirección\n(T) Teléfono\n(U) Usuario\n(C) Contraseña\n(F) Fecha de Nacimiento\n(G) Género");
        switch (scan.nextLine().toUpperCase()) {
            case "N":
                stmt.executeQuery("Update Person Set Name = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "A":
                stmt.executeQuery("Update Person Set Surnames = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "E":
                stmt.executeQuery("Update Person Set Email = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "D":
                stmt.executeQuery("Update Person Set Adress = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "T":
                stmt.executeQuery("Update Person Set Phone = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "U":
                stmt.executeQuery("Update Person Set Username = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "C":
                stmt.executeQuery("Update Person Set Password = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "F":
                stmt.executeQuery("Update Person Set Birth_Date = DATE'" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "G":
                stmt.executeQuery("Update Person Set Gender = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            default:
                System.err.println("Asegurate de introducir una opción correctamente");
                break;
        }
        System.out.println("Modificado correctamente");
    }

    private static void listInfo(Statement stmt) throws SQLException {
        System.out.println("Elige qué quieres visualizar:\n(U) Información de tu Usuario\n(P) ");
        switch (scan.nextLine().toUpperCase()) {
            case "U":
                ResultSet res = stmt.executeQuery("Select Name, Surnames, Email, Adress, Phone, Username, Password, Birth_Date, Gender from Person where ID_Person = '" + loggedID + "'");
                System.out.println("Nombre: " + (res.next() ? res.getString("Name") : null) + "\n"
                        + "Apellidos: " + res.getString("Surname") + "\n"
                        + "Email: " + res.getString("Email") + "\n"
                        + "Dirección: " + res.getString("Adress") + "\n"
                        + "Teléfono: " + res.getString("Phone") + "\n"
                        + "Usuario: " + res.getString("Username") + "\n"
                        + "Contraseña: " + res.getString("Password") + "\n"
                        + "Fecha de Nacimiento: " + res.getString("Birth_Date") + "\n"
                        + "Género: " + res.getString("Gender"));
                break;
            case "P":
                stmt.executeQuery("Update Person Set Surnames = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
        }

    }

    private static void closeSession() {
        logged = false;
        loggedID = null;
        System.out.println("Sesión finalizada correctamente");
    }
}
