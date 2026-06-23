package emarket;

import emarket.carrito.ItemCarrito;
import emarket.catalogo.Categoria;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import emarket.facade.LibreriaFacade;
import emarket.notificacion.CanalNotificacion;
import emarket.repositorio.factory.InMemoryRepositorioFactory;
import emarket.repositorio.factory.JdbcRepositorioFactory;
import emarket.repositorio.factory.RepositorioFactory;
import emarket.util.Validaciones;
import emarket.pago.TipoPago;
import emarket.pedido.ItemPedido;
import emarket.pedido.Pedido;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static LibreriaFacade facade;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Lanzar UI Swing si no se pasa el flag --consola
        if (!Arrays.asList(args).contains("--consola")) {
            emarket.ui.LibreriaSwingApp.main(args);
            return;
        }

        // Elegir la factory según el argumento de línea de comandos:
        //   java -jar app.jar --jdbc  →  PostgreSQL (requiere Docker activo)
        //   java -jar app.jar         →  en memoria (sin dependencias externas)
        boolean usarJdbc = Arrays.asList(args).contains("--jdbc");
        RepositorioFactory factory = usarJdbc
                ? new JdbcRepositorioFactory()
                : new InMemoryRepositorioFactory();

        System.out.println();
        System.out.println("  Modo de persistencia: " +
                (usarJdbc ? "PostgreSQL via JDBC" : "En memoria"));
        System.out.println();

        facade  = new LibreriaFacade(factory);
        scanner = new Scanner(System.in);

        facade.precargarDatos();

        boolean corriendo = true;
        while (corriendo) {
            if (!facade.estaAutenticado()) {
                corriendo = menuSinSesion();
            } else if (facade.esCliente()) {
                corriendo = menuCliente();
            } else {
                corriendo = menuAdmin(facade.getUsernameActual());
            }
        }

        System.out.println("\n  Gracias por usar EMarket. ¡Hasta pronto!");
        scanner.close();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MENÚS
    // ══════════════════════════════════════════════════════════════════════════

    private static boolean menuSinSesion() {
        cabecera("EMARKET v1.0");
        System.out.println("  1. Registrarse como cliente");
        System.out.println("  2. Iniciar sesión");
        System.out.println("  3. Registrarse como administrador");
        System.out.println("  0. Salir");
        separador();

        switch (leerOpcion()) {
            case 1 -> accionRegistrarCliente();
            case 2 -> accionIniciarSesion();
            case 3 -> accionRegistrarAdmin();
            case 0 -> { return false; }
            default -> error("Opción inválida.");
        }
        pausar();
        return true;
    }

    private static boolean menuCliente() {
        cabecera("EMARKET — " + facade.getUsernameActual() + " [CLIENTE]");
        System.out.println("  1. Ver catálogo");
        System.out.println("  2. Buscar producto por ID");
        System.out.println("  3. Agregar producto al carrito");
        System.out.println("  4. Ver mi carrito");
        System.out.println("  5. Confirmar compra");
        System.out.println("  6. Mis pedidos");
        System.out.println("  7. Cerrar sesión");
        System.out.println("  0. Salir");
        separador();

        switch (leerOpcion()) {
            case 1 -> accionVerCatalogo();
            case 2 -> accionBuscarProducto();
            case 3 -> accionAgregarAlCarrito();
            case 4 -> accionVerCarrito();
            case 5 -> accionConfirmarCompra();
            case 6 -> accionMisPedidos();
            case 7 -> { facade.cerrarSesion(); ok("Sesión cerrada."); }
            case 0 -> { return false; }
            default -> error("Opción inválida.");
        }
        pausar();
        return true;
    }

    private static boolean menuAdmin(String username) {
        cabecera("EMARKET — " + username + " [ADMINISTRADOR]");
        System.out.println("  1. Ver catálogo");
        System.out.println("  2. Buscar producto por ID");
        System.out.println("  3. Agregar producto");
        System.out.println("  4. Agregar categoría");
        System.out.println("  5. Modificar stock de producto");
        System.out.println("  8. Ver todos los pedidos");
        System.out.println("  9. Avanzar estado de pedido");
        System.out.println("  7. Cerrar sesión");
        System.out.println("  0. Salir");
        separador();

        switch (leerOpcion()) {
            case 1 -> accionVerCatalogo();
            case 2 -> accionBuscarProducto();
            case 3 -> accionAgregarProducto();
            case 4 -> accionAgregarCategoria();
            case 5 -> accionModificarStock();
            case 8 -> accionVerTodosLosPedidos();
            case 9 -> accionActualizarEstadoPedido();
            case 7 -> { facade.cerrarSesion(); ok("Sesión cerrada."); }
            case 0 -> { return false; }
            default -> error("Opción inválida.");
        }
        pausar();
        return true;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — AUTH
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionIniciarSesion() {
        cabecera("INICIAR SESIÓN");
        System.out.print("  Usuario    : ");
        String username = scanner.nextLine().trim();
        String pass = leerPassword("  Contraseña : ");

        if (facade.iniciarSesion(username, pass)) {
            ok("Sesión iniciada. ¡Bienvenido/a, " + username + "!");
            mostrarNotificacionesPendientes();
        } else {
            error("Usuario o contraseña incorrectos.");
        }
    }

    private static void accionRegistrarCliente() {
        cabecera("REGISTRO DE CLIENTE");
        System.out.print("  Usuario            : ");
        String username = scanner.nextLine().trim();
        String pass = pedirPasswordConConfirmacion();
        System.out.print("  Dirección          : ");
        String direccion = scanner.nextLine().trim();
        String email = pedirEmail();
        String telefono = pedirTelefono();
        String token = "TOKEN_" + username.toUpperCase();
        System.out.println("  Canales disponibles: EMAIL, SMS, PUSH");
        List<CanalNotificacion> canales;
        do {
            System.out.print("  Canales preferidos  (separados por coma): ");
            canales = parsearCanales(scanner.nextLine().trim());
            if (canales.isEmpty())
                System.out.println("  ✗ Ningún canal válido. Ingresá al menos uno: EMAIL, SMS o PUSH.");
        } while (canales.isEmpty());

        try {
            facade.registrarClienteCompleto(username, pass, direccion, email, telefono, token, canales);
            ok("Cliente '" + username + "' registrado correctamente.");
        } catch (Exception e) {
            error("Error al registrar: " + e.getMessage());
        }
    }

    private static void mostrarNotificacionesPendientes() {
        List<String> pendientes = facade.tomarNotificaciones();
        if (pendientes.isEmpty()) return;
        System.out.println();
        separador();
        System.out.println("  Notificaciones pendientes:");
        for (String n : pendientes) {
            System.out.println(n);
        }
        separador();
    }

    private static void accionRegistrarAdmin() {
        cabecera("REGISTRO DE ADMINISTRADOR");
        System.out.print("  Usuario          : ");
        String username = scanner.nextLine().trim();
        String pass = pedirPasswordConConfirmacion();
        System.out.print("  Clave de admin   : ");
        String claveAdmin = scanner.nextLine().trim();

        try {
            facade.registrarAdministrador(username, pass, claveAdmin);
            ok("Administrador '" + username + "' registrado correctamente.");
        } catch (Exception e) {
            error("Error al registrar: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — CATÁLOGO
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionVerCatalogo() {
        cabecera("CATÁLOGO DE LIBROS");
        try {
            imprimirCatalogo(facade.listarCatalogo(), "  ");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private static void accionBuscarProducto() {
        System.out.print("  ID del producto: ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); return; }

        try {
            Producto p = facade.buscarProducto(id);
            if (p == null) {
                error("No se encontró ningún producto con ID " + id + ".");
            } else {
                System.out.printf("  id=%-3d %-22s $%-8.2f stock=%d%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — CARRITO
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionAgregarAlCarrito() {
        System.out.print("  ID del producto : ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); return; }

        System.out.print("  Cantidad        : ");
        int cantidad = leerEntero();
        if (cantidad <= 0) { error("La cantidad debe ser mayor a 0."); return; }

        try {
            facade.agregarProductoAlCarrito(id, cantidad);
            ok("Producto agregado al carrito.");
        } catch (IllegalStateException e) {
            error(e.getMessage());
        }
    }

    private static void accionVerCarrito() {
        boolean enCarrito = true;
        while (enCarrito) {
            cabecera("MI CARRITO");

            List<ItemCarrito> items = facade.getItemsCarrito();

            if (items.isEmpty()) {
                System.out.println("  El carrito está vacío.");
                return;
            }

            System.out.printf("  %-4s %-22s %6s   %10s   %10s%n", "ID", "Producto", "Cant.", "Precio unit.", "Subtotal");
            separador();
            for (ItemCarrito item : items) {
                System.out.printf("  %-4d %-22s %6d   $%9.2f   $%9.2f%n",
                        item.getProducto().getId(),
                        item.getProducto().getNombre(),
                        item.getCantidad(),
                        item.getProducto().getPrecio(),
                        item.getSubtotal());
            }
            separador();
            double subtotal = facade.getTotalCarrito();
            System.out.printf("  %-28s %10s   $%9.2f%n", "SUBTOTAL", "", subtotal);
            double total = subtotal * (1 + 0.21);
            System.out.printf("  %-28s %10s   $%9.2f%n", "TOTAL (c/ IVA 21%)", "", total);
            System.out.println();

            System.out.println("  1. Eliminar un producto");
            System.out.println("  2. Modificar cantidad de un producto");
            System.out.println("  3. Vaciar carrito");
            System.out.println("  0. Volver");
            separador();

            switch (leerOpcion()) {
                case 1 -> accionEliminarDelCarrito();
                case 2 -> accionModificarCantidadEnCarrito();
                case 3 -> {
                    try {
                        facade.vaciarCarrito();
                        ok("Carrito vaciado.");
                    } catch (Exception e) {
                        error(e.getMessage());
                    }
                    enCarrito = false;
                }
                case 0 -> enCarrito = false;
                default -> { error("Opción inválida."); pausar(); }
            }
        }
    }

    private static void accionEliminarDelCarrito() {
        System.out.print("  ID del producto a eliminar: ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); pausar(); return; }
        try {
            facade.eliminarProductoDelCarrito(id);
            ok("Producto eliminado del carrito.");
        } catch (Exception e) {
            error(e.getMessage());
        }
        pausar();
    }

    private static void accionModificarCantidadEnCarrito() {
        System.out.print("  ID del producto: ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); pausar(); return; }
        System.out.print("  Nueva cantidad : ");
        int cantidad = leerEntero();
        if (cantidad <= 0) { error("La cantidad debe ser mayor a 0."); pausar(); return; }
        try {
            facade.modificarCantidadEnCarrito(id, cantidad);
            ok("Cantidad actualizada.");
        } catch (Exception e) {
            error(e.getMessage());
        }
        pausar();
    }

    private static void accionConfirmarCompra() {
        cabecera("CONFIRMAR COMPRA");
        System.out.println("  Métodos de pago:");
        System.out.println("    1. Tarjeta de crédito");
        System.out.println("    2. PayPal");
        System.out.println("    3. MercadoPago");
        System.out.println("    4. Transferencia bancaria");
        System.out.print("  Elegí un método: ");
        int metodo = leerEntero();

        TipoPago tipoPago = switch (metodo) {
            case 1 -> TipoPago.TARJETA_CREDITO;
            case 2 -> TipoPago.PAYPAL;
            case 3 -> TipoPago.MERCADO_PAGO;
            case 4 -> TipoPago.TRANSFERENCIA;
            default -> null;
        };

        if (tipoPago == null) {
            error("Método de pago inválido.");
            return;
        }

        try {
            facade.confirmarCompra(tipoPago, facade.pedirDatosPago(tipoPago, scanner));
            ok("¡Compra confirmada con éxito!");
            facade.tomarNotificaciones();
        } catch (IllegalStateException e) {
            error(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — PEDIDOS (CLIENTE)
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionMisPedidos() {
        cabecera("MIS PEDIDOS");

        try {
            List<Pedido> pedidos = facade.listarPedidosCliente();
            if (pedidos.isEmpty()) {
                System.out.println("  No tenés pedidos aún.");
                return;
            }
            for (Pedido p : pedidos) {
                imprimirPedido(p);
            }
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — PEDIDOS (ADMIN)
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionVerTodosLosPedidos() {
        cabecera("TODOS LOS PEDIDOS");

        try {
            List<Pedido> pedidos = facade.listarTodosLosPedidos();
            if (pedidos.isEmpty()) {
                System.out.println("  No hay pedidos registrados en el sistema.");
                return;
            }
            for (Pedido p : pedidos) {
                imprimirPedido(p);
            }
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private static void accionActualizarEstadoPedido() {
        System.out.print("  ID del pedido a avanzar: ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); return; }

        try {
            Pedido pedido = facade.actualizarEstadoPedido(id);
            ok("Pedido #" + id + " avanzó a estado: " + pedido.getEstado().getNombre());
        } catch (IllegalStateException e) {
            error(e.getMessage());
        }
    }

    private static void accionAgregarProducto() {
        System.out.print("  Nombre de la categoría : ");
        String cat = scanner.nextLine().trim();
        System.out.print("  ID del producto        : ");
        int id = leerEntero();
        System.out.print("  Nombre del producto    : ");
        String nombre = scanner.nextLine().trim();
        System.out.print("  Precio                 : ");
        double precio;
        try { precio = Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { error("Precio inválido."); return; }
        System.out.print("  Stock inicial          : ");
        int stock = leerEntero();
        if (stock < 0) { error("Stock inválido."); return; }
        try {
            facade.agregarProducto(cat, id, nombre, precio, stock);
            ok("Producto '" + nombre + "' agregado a '" + cat + "'.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private static void accionAgregarCategoria() {
        System.out.print("  Nombre de la nueva categoría : ");
        String nombre = scanner.nextLine().trim();
        System.out.print("  Categoría padre              : ");
        String padre = scanner.nextLine().trim();
        try {
            facade.agregarCategoria(nombre, padre);
            ok("Categoría '" + nombre + "' agregada bajo '" + padre + "'.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private static void accionModificarStock() {
        System.out.print("  ID del producto : ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); return; }
        System.out.print("  Nuevo stock     : ");
        int stock = leerEntero();
        if (stock < 0) { error("Stock inválido."); return; }
        try {
            facade.modificarStock(id, stock);
            ok("Stock del producto #" + id + " actualizado a " + stock + ".");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DE DISPLAY
    // ══════════════════════════════════════════════════════════════════════════

    private static void imprimirCatalogo(ComponenteCatalogo componente, String prefijo) {
        if (componente instanceof Categoria cat) {
            System.out.printf("%s[%s]  precio total: $%.2f  |  stock total: %d%n",
                    prefijo, cat.getNombre(), cat.getPrecio(), cat.getStock());
            for (ComponenteCatalogo hijo : cat.getHijos()) {
                imprimirCatalogo(hijo, prefijo + "  ");
            }
        } else if (componente instanceof Producto p) {
            System.out.printf("%sid=%-3d  %-22s  $%-8.2f  stock=%d%n",
                    prefijo, p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
        }
    }

    private static void imprimirPedido(Pedido p) {
        System.out.printf("  Pedido #%-3d | Estado: %-10s | Total: $%.2f%n",
                p.getId(), p.getEstado().getNombre(), p.getTotal());
        for (ItemPedido item : p.getItems()) {
            System.out.printf("    - %-22s x%-3d @ $%-8.2f = $%.2f%n",
                    item.getNombreProducto(), item.getCantidad(),
                    item.getPrecioUnitario(), item.getSubtotal());
        }
        System.out.println("  " + "─".repeat(55));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DE INPUT
    // ══════════════════════════════════════════════════════════════════════════

    private static int leerOpcion() {
        System.out.print("  Opción > ");
        return leerEntero();
    }

    private static int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String leerPassword(String prompt) {
        System.out.print(prompt + "\033[8m");
        String pass;
        try {
            pass = scanner.nextLine().trim();
        } catch (java.util.NoSuchElementException e) {
            pass = "";
        } finally {
            System.out.print("\033[0m");
            System.out.flush();
        }
        return pass;
    }

    private static String pedirPasswordConConfirmacion() {
        while (true) {
            String p1 = leerPassword("  Contraseña         : ");
            if (p1.isEmpty()) {
                error("La contraseña no puede estar vacía.");
                continue;
            }
            String p2 = leerPassword("  Repetir contraseña : ");
            if (p1.equals(p2)) return p1;
            error("Las contraseñas no coinciden. Intentá de nuevo.");
        }
    }

    private static String pedirEmail() {
        while (true) {
            System.out.print("  Email              : ");
            String email = scanner.nextLine().trim();
            if (Validaciones.esEmailValido(email)) return email;
            error("El email debe contener '@' con texto antes y después. Ej: nombre@dominio.com");
        }
    }

    private static String pedirTelefono() {
        while (true) {
            System.out.print("  Teléfono (10 dígitos): ");
            String tel = scanner.nextLine().trim();
            if (tel.matches("\\d{10}")) return tel;
            error("El teléfono debe tener exactamente 10 dígitos numéricos. Ej: 1155551234");
        }
    }

    private static List<CanalNotificacion> parsearCanales(String input) {
        List<CanalNotificacion> canales = new ArrayList<>();
        for (String parte : input.toUpperCase().split(",")) {
            String valor = parte.trim();
            try {
                canales.add(CanalNotificacion.valueOf(valor));
            } catch (IllegalArgumentException e) {
                if (!valor.isEmpty())
                    System.out.println("  ✗ Canal desconocido ignorado: '" + valor + "'");
            }
        }
        return canales;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DE UI
    // ══════════════════════════════════════════════════════════════════════════

    private static void cabecera(String titulo) {
        System.out.println("╔══════════════════════════════════╗");
        int padding = Math.max(0, (34 - titulo.length()) / 2);
        System.out.println("║" + " ".repeat(padding) + titulo
                + " ".repeat(Math.max(0, 34 - padding - titulo.length())) + "║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println();
    }

    private static void separador() {
        System.out.println("  " + "─".repeat(40));
    }

    private static void ok(String msg) {
        System.out.println("  ✓ " + msg);
    }

    private static void error(String msg) {
        System.out.println("  ✗ " + msg);
    }

    private static void pausar() {
        System.out.println();
        System.out.print("  [Enter para continuar] ");
        scanner.nextLine();
    }
}
