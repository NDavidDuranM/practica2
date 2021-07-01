package encapsulation;

import java.util.ArrayList;

public class Controladora {

    private final ArrayList<Usuario> listaUsarios = new ArrayList<>();
    private final ArrayList<Producto> listaProductos = new ArrayList<>();
    private final ArrayList<VentasProductos> ventasRealizadas = new ArrayList<>();
    private static Controladora instancia;

    private Controladora() {
        listaUsarios.add(new Usuario("admin","admin","admin"));
        listaProductos.add(new Producto(1,"RAM 8GB",(float)1500.00));
        listaProductos.add(new Producto(2,"Computadora",(float)5000.00));
        listaProductos.add(new Producto(3,"Laptop",(float)6000.00));
    }

    public static Controladora getInstance(){
        if(instancia==null){
            instancia = new Controladora();
        }
        return instancia;
    }

    public void agregarUsuario(Usuario usu){
        listaUsarios.add(usu);
    }

    public void agregarProducto(Producto producto){ listaProductos.add(producto); }

    public void agregarVenta(VentasProductos ventaP){ ventasRealizadas.add(ventaP); }

    public ArrayList<VentasProductos> getVentasRealizadas() {
        return ventasRealizadas;
    }

    public Usuario buscarUsuario(String usuario_obtenido, String password){
        Usuario usuario = null;
        for (Usuario x: listaUsarios) {
            if(x.getUsuario().equals(usuario_obtenido) && x.getPassword().equals(password)){
                usuario = x;
                break;
            }
        }
        return usuario;
    }

    public ArrayList<Producto> getListaProductos(){
        return this.listaProductos;
    }

    public Producto buscarProducto(int id){
        Producto productoTemporal = null;
        for (Producto p:listaProductos) {
            if(p.getId() == id){
                productoTemporal = p;
                break;
            }
        }
        return productoTemporal;
    }

    public void eliminarProducto(int id){
        int x;
        for(x=0; x < listaProductos.size(); x++){
            if(listaProductos.get(x).getId() == id){
                listaProductos.remove(x);
                break;
            }
        }
    }

    public void editarProducto(int id, int nuevoID, String nombre, float precio){
        int x;
        for(x=0; x < listaProductos.size(); x++){
            if(listaProductos.get(x).getId() == id){
                listaProductos.get(x).setId(nuevoID);
                listaProductos.get(x).setNombre(nombre);
                listaProductos.get(x).setPrecio(precio);
                break;
            }
        }
    }
}
