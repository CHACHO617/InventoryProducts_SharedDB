package com.example.inventory;

import java.sql.*;
import java.util.Scanner;

public class InventoryService {

    // Mostrar todos los productos con su inventario
    public void viewAllProducts() {
        String query = """
            SELECT p.id_producto, p.nombre, p.descripcion, p.precio,
                   i.cantidad_disponible, i.ubicacion_tienda, i.estado_stock
            FROM Productos p
            JOIN Inventario i ON p.id_producto = i.id_producto_inv
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("=== Lista de Productos con Inventario ===");
            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Descripción: %s | Precio: %.2f | Cantidad: %d | Tienda: %s | Estado Stock: %s\n",
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad_disponible"),
                        rs.getString("ubicacion_tienda"),
                        rs.getString("estado_stock"));
                      
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al mostrar productos:");
            e.printStackTrace();
        }
    }

    // Agregar un nuevo producto con inventario
    public void addProduct(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Nombre del producto: ");
            String nombre = scanner.nextLine();

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine();

            System.out.print("Precio (> 0): ");
            double precio = scanner.nextDouble();
            if (precio <= 0) {
                System.out.println("❌ El precio debe ser mayor que 0.");
                return;
            }

            System.out.print("Cantidad disponible (>= 0): ");
            int cantidad = scanner.nextInt();
            if (cantidad < 0) {
                System.out.println("❌ La cantidad debe ser 0 o más.");
                return;
            }
            scanner.nextLine(); // limpiar salto de línea

            System.out.print("Ubicación tienda (ej. Quito, Guayaquil, Cuenca): ");
            String tienda = scanner.nextLine();

            conn.setAutoCommit(false);

            String insertProducto = "INSERT INTO Productos (nombre, descripcion, precio) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertProducto, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.setString(2, descripcion);
                ps.setDouble(3, precio);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int productoId = keys.getInt(1);

                    String insertInventario = "INSERT INTO Inventario (id_producto_inv, cantidad_disponible, ubicacion_tienda) VALUES (?, ?, ?)";
                    try (PreparedStatement ps2 = conn.prepareStatement(insertInventario)) {
                        ps2.setInt(1, productoId);
                        ps2.setInt(2, cantidad);
                        ps2.setString(3, tienda);
                        ps2.executeUpdate();
                    }
                }

                conn.commit();
                System.out.println("✅ Producto agregado exitosamente con inventario.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar producto:");
            e.printStackTrace();
        }
    }

    // Actualizar un producto existente e inventario
    public void updateProduct(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("ID del producto a actualizar: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Nuevo nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Nueva descripción: ");
            String descripcion = scanner.nextLine();

            System.out.print("Nuevo precio (> 0): ");
            double precio = scanner.nextDouble();
            if (precio <= 0) {
                System.out.println("❌ El precio debe ser mayor que 0.");
                return;
            }

            System.out.print("Nueva cantidad disponible (>= 0): ");
            int cantidad = scanner.nextInt();
            if (cantidad < 0) {
                System.out.println("❌ La cantidad debe ser 0 o más.");
                return;
            }
            scanner.nextLine();

            System.out.print("Nueva ubicación tienda: ");
            String tienda = scanner.nextLine();

            conn.setAutoCommit(false);

            String updateProducto = "UPDATE Productos SET nombre = ?, descripcion = ?, precio = ? WHERE id_producto = ?";
            String updateInventario = "UPDATE Inventario SET cantidad_disponible = ?, ubicacion_tienda = ? WHERE id_producto_inv = ?";

            try (PreparedStatement ps1 = conn.prepareStatement(updateProducto);
                 PreparedStatement ps2 = conn.prepareStatement(updateInventario)) {

                ps1.setString(1, nombre);
                ps1.setString(2, descripcion);
                ps1.setDouble(3, precio);
                ps1.setInt(4, id);
                ps1.executeUpdate();

                ps2.setInt(1, cantidad);
                ps2.setString(2, tienda);
                ps2.setInt(3, id);
                ps2.executeUpdate();

                conn.commit();
                System.out.println("✅ Producto actualizado exitosamente.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar producto:");
            e.printStackTrace();
        }
    }

    // Eliminar un producto
    public void deleteProduct(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("ID del producto a eliminar: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            conn.setAutoCommit(false);

            String deleteInventario = "DELETE FROM Inventario WHERE id_producto_inv = ?";
            String deleteProducto = "DELETE FROM Productos WHERE id_producto = ?";

            try (PreparedStatement ps1 = conn.prepareStatement(deleteInventario);
                 PreparedStatement ps2 = conn.prepareStatement(deleteProducto)) {

                ps1.setInt(1, id);
                ps1.executeUpdate();

                ps2.setInt(1, id);
                ps2.executeUpdate();

                conn.commit();
                System.out.println("✅ Producto eliminado.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar producto:");
            e.printStackTrace();
        }
    }

    // Limpiar consola
    public void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
