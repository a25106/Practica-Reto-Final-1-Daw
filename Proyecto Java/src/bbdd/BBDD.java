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

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        int rndm = (int)(Math.random()*Math.pow(10, 10));
        System.out.println(rndm);
        Scanner scan = new Scanner(System.in);
        System.out.println("Escribe el nombre");
        String name = scan.nextLine();
        System.out.println("Escribe los apellidos");
        String surnames = scan.nextLine();
        System.out.println("Escribe el email");
        String email = scan.nextLine();
        System.out.println("Escribe la dirección");
        String adress = scan.nextLine();
        System.out.println("Escribe el teléfono");
        String phone = scan.nextLine();
        System.out.println("Escribe el usuario");
        String username = scan.nextLine();
        System.out.println("Escribe la contraseña");
        String password = scan.nextLine();
        System.out.println("Escribe la fecha de nacimiento");
        String birth_date = scan.nextLine();
        System.out.println("Escribe el género");
        String gender = scan.nextLine();
        try {
            // 1. Cargar el driver
            Class.forName("oracle.jdbc.OracleDriver");
            
            //2. Crear la conexion
            String cadenaConexion = "jdbc:oracle:thin:@localhost:1521/XE";
            Connection conexion= DriverManager.getConnection(cadenaConexion,"user","user");
            
            //3. Crear objeto Statement para lanzar las query
            Statement stmt = conexion.createStatement();
            
            //4. Generar una query            
//            String query = "SELECT * FROM Person";
            String query = "INSERT INTO person (ID_Person, Name, Surnames, Email, Adress, Phone, Username, Password, Birth_Date, Gender)" + "Values( '"+rndm+"', '"+name+"', '"+surnames+"', '"+email+"', '"+adress+"', '"+phone+"', '"+username+"', '"+password+"', DATE '"+birth_date+"', '"+gender+"')";
            //5. Ejecutar la query
            ResultSet resultado = stmt.executeQuery(query);
            System.out.println("Resultado de la query: "+resultado);
                        
            //6. Cerrar conexion
            stmt.close();
            conexion.close();
            
        } catch (ClassNotFoundException | SQLException e){
            System.err.println(e.getMessage());
        }  
    }
}
