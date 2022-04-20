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
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Ángel Gaudes y Samuel Aranda
 */
public class BBDD {

    static boolean logged = false;
    static String loggedID = null;
    static String[] admins = {"Nicolai"};
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
                        System.out.println("\u001B[31m\nAsegurate de introducir (R) o (I) correctamente\u001B[0m");
                        break;
                }
            }
            while (logged) {
                if (verifyAdmin(stmt)) {
                    System.out.println("\nElige una opción:\n(M) Modificar datos de usuario\n(L) Lista y Vista Detalle\n(B) Búsqueda de productos\n(C) Comprar Producto\n(E) Eliminar producto\n(F) Finalizar Sesión");
                } else {
                    System.out.println("\nElige una opción:\n(M) Modificar datos de usuario\n(L) Lista y Vista Detalle\n(B) Búsqueda de productos\n(C) Comprar Producto\n(F) Finalizar Sesión");
                };
                switch (scan.nextLine().toUpperCase()) {
                    case "M":
                        modifyUser(stmt);
                        break;
                    case "L":
                        listInfo(stmt);
                        break;
                    case "B":
                        searchProducts(stmt);
                        break;
                    case "C":
                        buyProduct(stmt);
                        break;
                    case "F":
                        closeSession();
                        break;
                    case "E":
                        deleteProduct(stmt);
                        break;
                    default:
                        System.out.println("\u001B[31m\nAsegurate de introducir una opción correctamente\u001B[0m");
                        break;
                }
            }

            //6. Cerrar conexion
            stmt.close();
            conexion.close();

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("\u001B[31m\n" + e.getMessage() + "\u001B[0m");
        }
    }

    private static String registerPerson(Statement stmt) throws SQLException {
        int rndm = (int) (Math.random() * Math.pow(10, 10));
        while (stmt.executeQuery("Select ID_Person from person where ID_Person = " + rndm).next()) {
            rndm = (int) (Math.random() * Math.pow(10, 10));
        }
        System.out.println("\nID: " + rndm);
        System.out.println("\nEscribe el nombre");
        String name = scan.nextLine();
        System.out.println("\nEscribe los apellidos");
        String surnames = scan.nextLine();
        System.out.println("\nEscribe el email");
        String email = scan.nextLine();
        try {
            while (stmt.executeQuery("Select Email from Person where Email = '" + email + "'").next()) {
                System.out.println("\u001B[31m\nEse correo ya está registrado\u001B[0m");
                email = scan.nextLine();
            }
        } catch (SQLException e) {
        }
        System.out.println("\nEscribe la dirección");
        String adress = scan.nextLine().toLowerCase();
        System.out.println("\nEscribe el teléfono");
        String phone = scan.nextLine();
        System.out.println("\nEscribe el usuario (Es sensible a las mayúsculas y minúsculas)");
        String username = scan.nextLine();
        try {
            while (stmt.executeQuery("Select Username from Person where Username = '" + username + "'").next()) {
                System.out.println("\u001B[31m\nEse Usuario ya está registrado\u001B[0m");
                email = scan.nextLine();
            }
        } catch (SQLException e) {
        }
        System.out.println("\nEscribe la contraseña");
        String password = scan.nextLine();
        System.out.println("\nEscribe la fecha de nacimiento");
        String birth_date = scan.nextLine();
        System.out.println("\nEscribe el género");
        String gender = scan.nextLine();
        return "INSERT INTO person (ID_Person, Name, Surnames, Email, Adress, Phone, Username, Password, Birth_Date, Gender)" + "Values( '" + rndm + "', '" + name + "', '" + surnames + "', '" + email + "', '" + adress + "', '" + phone + "', '" + username + "', '" + password + "', DATE '" + birth_date + "', '" + gender + "')";
    }

    private static boolean loginPerson(Statement stmt) throws SQLException {
        System.out.println("\nEscribe el usuario o email");
        String useremail = scan.nextLine();
        try {
            if (!stmt.executeQuery("Select Email from Person where Email = '" + useremail.toLowerCase() + "'").next() && !stmt.executeQuery("Select Username from Person where Username = '" + useremail + "'").next()) {
                System.out.println("\u001B[31m\nEl Usuario o Email introducido no está registrado\u001B[0m");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31m\nEl Usuario o Email introducido no está registrado\n" + e + "\u001B[0m");
            return false;
        }
        System.out.println("\nEscribe la contraseña");
        String password = scan.nextLine();
        try {
            for (int i = 0; i < 3; i++) {
                if (stmt.executeQuery("Select Password from Person where Password = '" + password + "' and (Email = '" + useremail.toLowerCase() + "' or Username = '" + useremail + "')").next()) {
                    ResultSet res = stmt.executeQuery("Select ID_Person from Person where Password = '" + password + "' and (Email = '" + useremail.toLowerCase() + "' or Username = '" + useremail + "')");
                    loggedID = res.next() ? res.getString(1) : null;
                    return true;
                }
                System.out.println("\u001B[31m\nLa contraseña es incorrecta, introdúcela de nuevo\u001B[0m");
                password = scan.nextLine();
            }
            System.out.println("\u001B[31m\nHas fallado muchas veces la contraseña\u001B[0m");
            return false;
        } catch (SQLException e) {
            System.out.println("\u001B[31m\nLa contraseña es incorrecta\u001B[0m" + e);
            return false;
        }
    }

    private static void modifyUser(Statement stmt) throws SQLException {
        System.out.println("\nElige qué quieres modificar:\n(N) Nombre\n(A) Apellidos\n(E) Email\n(D) Dirección\n(T) Teléfono\n(U) Usuario\n(C) Contraseña\n(F) Fecha de Nacimiento\n(G) Género");
        switch (scan.nextLine().toUpperCase()) {
            case "N":
                System.out.println("\nEscribe un nuevo nombre");
                stmt.executeQuery("Update Person Set Name = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "A":
                System.out.println("\nEscribe unos nuevos apellidos");
                stmt.executeQuery("Update Person Set Surnames = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "E":
                System.out.println("\nEscribe un nuevo email");
                stmt.executeQuery("Update Person Set Email = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "D":
                System.out.println("\nEscribe una nueva dirección");
                stmt.executeQuery("Update Person Set Adress = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "T":
                System.out.println("\nEscribe un nuevo teléfono");
                stmt.executeQuery("Update Person Set Phone = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "U":
                System.out.println("\nEscribe un nuevo usuario");
                stmt.executeQuery("Update Person Set Username = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "C":
                System.out.println("\nEscribe una nueva contraseña");
                stmt.executeQuery("Update Person Set Password = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "F":
                System.out.println("\nEscribe una nueva fecha de nacimiento");
                stmt.executeQuery("Update Person Set Birth_Date = DATE'" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            case "G":
                System.out.println("\nEscribe un nuevo género");
                stmt.executeQuery("Update Person Set Gender = '" + scan.nextLine() + "' Where ID_Person = '" + loggedID + "'");
                break;
            default:
                System.out.println("\u001B[31m\nAsegurate de introducir una opción correctamente\u001B[0m");
                break;
        }
        System.out.println("\nModificado correctamente\n");
    }

    private static void listInfo(Statement stmt) throws SQLException {
        System.out.println("\nElige qué quieres visualizar:\n(U) Información de tu Usuario\n(P) Informacion de los Productos");
        switch (scan.nextLine().toUpperCase()) {
            case "U":
                ResultSet resU = stmt.executeQuery("Select Name, Surnames, Email, Adress, Phone, Username, Password, Birth_Date, Gender from Person where ID_Person = " + loggedID);
                if (resU.next()) {
                    System.out.println("\nNombre: " + resU.getString(1) + "\n"
                            + "Apellidos: " + resU.getString(2) + "\n"
                            + "Email: " + resU.getString(3) + "\n"
                            + "Dirección: " + resU.getString(4) + "\n"
                            + "Teléfono: " + resU.getString(5) + "\n"
                            + "Usuario: " + resU.getString(6) + "\n"
                            + "Contraseña: " + resU.getString(7) + "\n"
                            + "Fecha de Nacimiento: " + resU.getString(8) + "\n"
                            + "Género: " + resU.getString(9) + "\n"
                            + "Administrador: " + (verifyAdmin(stmt) ? "Sí" : "No"));
                }
                break;
            case "P":
                ResultSet resP = stmt.executeQuery("Select ID_App, Name, Volume, Description, Price, Category from App");
                boolean first = resP.next();
                if (!first) {
                    System.out.println("\u001B[31m\nNo hay ninguna aplicación disponible\u001B[0m");
                    return;
                }
                int count = 0;
                while (first || resP.next()) {
                    first = false;
                    System.out.println("\nID: " + resP.getString(1) + "\n"
                            + "Nombre: " + resP.getString(2) + "\n"
                            + "Tamaño: " + resP.getString(3) + "\n"
                            + "Descripción: " + resP.getString(4) + "\n"
                            + "Precio: " + resP.getString(5) + "€\n"
                            + "Categoría: " + resP.getString(6));
                    count++;
                }
                System.out.println("\nNúmero de aplicaciones: " + count);
                break;
        }

    }

    private static void searchProducts(Statement stmt) throws SQLException {
        System.out.println("\nEscribe tu búsqueda: ");
        String search = scan.nextLine().toUpperCase();
        ResultSet res = stmt.executeQuery("Select ID_App, Name, Volume, Description, Price, Category from App where Upper(Name) Like '%" + search + "%' or Upper(Volume) Like '%" + search + "%' or Upper(Description) Like '%" + search + "%' or Upper(Price) Like '%" + search + "%' or Upper(Category) Like '%" + search + "%' or ID_App = '" + search + "'");
        boolean first = res.next();
        if (!first) {
            System.out.println("\u001B[31m\nNo hay ninguna aplicación disponible\u001B[0m");
            return;
        }
        int count = 0;
        while (first || res.next()) {
            first = false;
            System.out.println("\nID: " + res.getString(1) + "\n"
                    + "Nombre: " + res.getString(2) + "\n"
                    + "Tamaño: " + res.getString(3) + "\n"
                    + "Descripción: " + res.getString(4) + "\n"
                    + "Precio: " + res.getString(5) + "€\n"
                    + "Categoría: " + res.getString(6));
            count++;
        }
        System.out.println("\nNúmero de aplicaciones encontradas: " + count);
    }

    private static void buyProduct(Statement stmt) throws SQLException {
        System.out.println("\nEscribe el nombre de un producto a comprar: ");
        String search = scan.nextLine();
        ResultSet res = stmt.executeQuery("Select Name from App where Upper(Name) = '" + search.toUpperCase() + "'");
        boolean first = res.next();
        if (!first) {
            System.out.println("\u001B[31m\nNo hay ninguna aplicación llamada " + search + "\u001B[0m");
        } else {
            System.out.println("\u001B[32m\nHas comprado " + res.getString(1) + "\u001B[0m");
        }
    }

    private static void deleteProduct(Statement stmt) throws SQLException {
        if (verifyAdmin(stmt)) {
            System.out.println("\nEscribe la ID de un producto a eliminar: ");
            String search = scan.nextLine();
            ResultSet res = stmt.executeQuery("Select Name from App where ID_App = '" + search + "'");
            if (res.next()) {
                String nombre = res.getString(1);
                stmt.executeQuery("Delete App Where ID_App = '" + search + "'");
                System.out.println("\u001B[32m\nHas eliminado " + nombre + " correctamente\u001B[0m");

            } else {
                System.out.println("\u001B[31m\nNo hay ninguna aplicación con la ID " + search + "\u001B[0m");
            }
        } else {
            System.out.println("\u001B[31m\nAsegurate de introducir una opción correctamente\u001B[0m");
        }
    }

    private static boolean verifyAdmin(Statement stmt) throws SQLException {
        ResultSet res = stmt.executeQuery("Select Username from Person where ID_Person = " + loggedID);
        return Arrays.asList(admins).contains(res.next() ? res.getString(1) : null);
    }

    private static void closeSession() {
        logged = false;
        System.out.println("\u001B[32m\nSesión finalizada correctamente\u001B[0m");
    }
}
