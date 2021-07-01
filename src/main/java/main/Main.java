package main;

import java.util.*;
import java.text.SimpleDateFormat;

import encapsulation.*;

import io.javalin.Javalin;
import io.javalin.plugin.rendering.JavalinRenderer;

import io.javalin.plugin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {
        Javalin aplicacion = Javalin.create(config ->{
            config.addStaticFiles("/publico");
        }).start(7000);

        Controladora control = Controladora.getInstance();

        //**********Register de Thymeleaf dentro de Javalin para su uso**********

        JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");

        //**********Endpoint por defecto**********

        aplicacion.get("/", context -> context.redirect("/login.html"));

        //**********Endpoints y procesos para creacion de usuario y confirmacion del mismo**********

        aplicacion.before("/confirmarLogin", context -> {
            String usuario = context.formParam("Username");
            String password = context.formParam("Password");
            if(control.buscarUsuario(usuario,password) == null){
                context.redirect("/login.html");
            }
        });

        aplicacion.post("/confirmarLogin", context -> {
            String usuario = context.formParam("Username");
            context.req.getSession().invalidate();
            context.sessionAttribute("user", usuario);
            context.redirect("/productos");
        });

        //Los usuarios que se crearan no son administradores

        aplicacion.post("/crearUsuario", context -> {
            String usuario = context.formParam("Username");
            String password = context.formParam("Password");
            String nombre = context.formParam("Name");
            Usuario usuarioTemp = new Usuario(usuario,nombre,password);
            control.agregarUsuario(usuarioTemp);
            context.redirect("/login.html");
        });

        //**********Proceso de lista de productos con uso de la plantilla Thymeleaf**********

        aplicacion.get("/productos", context -> {
            List<Producto> listaProductos = control.getListaProductos();
            Map<String, Object> modeloListaProductos = new HashMap<>();
            modeloListaProductos.put("lista",listaProductos.subList(0,listaProductos.size()));
            if(context.sessionAttribute("user")== null){
                modeloListaProductos.put("size",0);
            }else{
                if(context.sessionAttribute("carritoCompra")==null){
                    CarroCompra carrito = new CarroCompra(context.req.getSession().getId());
                    context.sessionAttribute("carritoCompra",carrito);
                    modeloListaProductos.put("size",0);
                }else{
                    modeloListaProductos.put("size",((CarroCompra)context.sessionAttribute("carritoCompra")).getListaProductos().size());
                }
            }
            context.render("/plantillas/listaProductos.html",modeloListaProductos);
        });

        //Nota de este proceso: se continua solicitando si el usuario es admin.
        aplicacion.before("/gestionProd", context -> {
            if(context.sessionAttribute("user") == null){
                context.redirect("/login.html");
            }
        });

        aplicacion.get("/gestionProd", context -> {
           if(context.sessionAttribute("user").equals("admin")){
               List<Producto> listaProductos = control.getListaProductos();
               Map<String, Object> modeloListaProductos = new HashMap<>();
               modeloListaProductos.put("lista",listaProductos);
               modeloListaProductos.put("size",((CarroCompra)context.sessionAttribute("carritoCompra")).getListaProductos().size());
               context.render("/plantillas/gestionarProductos.html",modeloListaProductos);
           }else{
               context.redirect("/errorPermisos.html");
           }
        });

        //**********Proceso de creacion de productos**********

        aplicacion.post("/nuevoProd", context ->{
            int id = context.formParam("id", Integer.class).get();
            String nombre = context.formParam("nombre");
            float precio = context.formParam("precio", Float.class).get();

            Producto producto = new Producto(id,nombre,precio);
            control.agregarProducto(producto);

            context.redirect("/gestionProd");
        });

        //**********Proceso de eliminacion de productos**********

        aplicacion.get("/eliminarProd/:id", context ->{
            int id = context.pathParam("id",Integer.class).get();
            control.eliminarProducto(id);
            context.redirect("/gestionProd");
        });

        //**********Proceso de actualizacion de productos**********

        aplicacion.get("/editarProd/:id", context ->{
            int id = context.pathParam("id",Integer.class).get();
            Map<String, Object> modeloProductos = new HashMap<>();

            Producto producto = control.buscarProducto(id);
            modeloProductos.put("id",id);
            modeloProductos.put("nombre",producto.getNombre());
            modeloProductos.put("precio",producto.getPrecio());

            context.render("/plantillas/editarProducto.html",modeloProductos);
        });

        aplicacion.post("/editarProd/:id", context -> {
            int id = context.pathParam("id",Integer.class).get();
            int NewId = context.formParam("id", Integer.class).get();
            String nombre = context.formParam("nombre", String.class).get();
            float precio = context.formParam("precio", Float.class).get();

            control.editarProducto(id,NewId,nombre,precio);

            context.redirect("/gestionProd");
        });

        //**********Procesos para añadir al carrito**********

        aplicacion.before("/anadirAlCarrito", context -> {
            if(context.sessionAttribute("carritoCompra") == null){
                context.redirect("login.html");
            }
        });

        aplicacion.post("/anadirAlCarrito", context ->{

            int id = context.formParam("id", Integer.class).get();
            int cantidad = context.formParam("cantidad", Integer.class).get();

            Producto productoTemporal = control.buscarProducto(id);
            CarroCompra carrito = context.sessionAttribute("carritoCompra");
            for(int x=0;x<cantidad;x++){
                carrito.agregarProducto(productoTemporal);
            }
            context.redirect("/productos");
        });

        aplicacion.before("/carritoCompra", context -> {
            if(context.sessionAttribute("carritoCompra") == null){
                context.redirect("login.html");
            }
        });

        aplicacion.get("/carritoCompra", context -> {
            CarroCompra carrito = context.sessionAttribute("carritoCompra");
            Map<String, Object> modeloProductos = new HashMap<>();
            modeloProductos.put("lista",carrito.getListaProductos());
            modeloProductos.put("size",((CarroCompra)context.sessionAttribute("carritoCompra")).getListaProductos().size());
            modeloProductos.put("user",context.sessionAttribute("user"));
            context.render("/plantillas/micarrito.html",modeloProductos);
        });

        aplicacion.get("/eliminarProdCarrito/:id",context ->{
            int id = context.pathParam("id",Integer.class).get();
            CarroCompra carrito = context.sessionAttribute("carritoCompra");
            carrito.eliminarProducto(carrito.buscarProducto(id));

            context.redirect("/carritoCompra");
        });

        //**********Procesos para llevar a cabo el pago de los productos**********

        //Nota de este proceso: se continua con la solicitud mientras no este vacio.
        aplicacion.before("/procesar", context ->{
            CarroCompra carrito = context.sessionAttribute("carritoCompra");
            if(carrito.getListaProductos().size()==0){
                context.redirect("/carritoCompra");
            }
        });

        aplicacion.get("/procesar",context -> {
            CarroCompra carrito = context.sessionAttribute("carritoCompra");
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date fecha = new Date();
            ArrayList<Producto> listaProductos = new ArrayList<>(carrito.getListaProductos());
            VentasProductos venta = new VentasProductos(carrito.getId(),formato.format(fecha),context.sessionAttribute("user"),listaProductos);

            control.agregarVenta(venta);
            carrito.getListaProductos().clear();
            context.redirect("/ventaRealizada.html");

        });

        //**********Procesos para las ventas realizadas**********

        aplicacion.before("/ventasRealizadas",context ->{
           if(context.sessionAttribute("user")==null){
               context.redirect("/login.html");
           }else{
               if(!context.sessionAttribute("user").equals("admin")){
                   context.redirect("errorPermisos.html");
               }
           }
        });

        aplicacion.get("/ventasRealizadas", context ->{
            ArrayList<VentasProductos> listaVentas = control.getVentasRealizadas();
            Map<String, Object> ventas = new HashMap<>();

            ventas.put("lista",listaVentas);
            ventas.put("size",((CarroCompra)context.sessionAttribute("carritoCompra")).getListaProductos().size());

            context.render("/plantillas/visualizarVentas.html",ventas);
        });
    }
}