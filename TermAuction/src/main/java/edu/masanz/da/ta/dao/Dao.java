package edu.masanz.da.ta.dao;

import edu.masanz.da.ta.dto.*;
import edu.masanz.da.ta.utils.Security;

import java.util.*;

import static edu.masanz.da.ta.conf.Ctes.*;
import static edu.masanz.da.ta.conf.Ini.*;

/**
 * Clase que simula la capa de acceso a datos. Cuando veamos las interfaces crearemos una interfaz para esta clase.
 * También crearemos una clase que implemente esa interfaz y que se conecte a una base de datos relacional.
 * Y una clase servicio que podrá utilizar cualquiera de las dos implementaciones, la simulada, la real u otra.
 * Por ahora, simplemente es una clase con métodos estáticos que simulan la interacción con una base de datos.
 */
public class Dao {


    //region Colecciones que simulan la base de datos
    private static Map<String, Usuario> mapaUsuarios;

    private static Map<Long, Item> mapaItems;



    private static Map<Long, List<Puja>> mapaPujas;

    //endregion

    //region Inicialización de la base de datos simulada
    public static void ini() {
        iniMapaUsuarios();
        iniMapaItems();
        iniMapaPujas();
    }

    private static void iniMapaUsuarios() {
        // TODO 01 iniMapaUsuarios

        for (String usuario : USUARIOS){
            String[]partes = usuario.split(",");
            Usuario usuario1 = new Usuario(partes[0] , partes[1] , partes[2] , partes[3]);
            mapaUsuarios.put(partes[0] , usuario1);



        }
    }

    private static void iniMapaItems() {
        // TODO 02 iniMapaItems

        for (String  item : USUARIOS){
            String[]partes = item.split(",");
            long id = Long.parseLong(partes[0]);
            int precioInicio = Integer.parseInt(partes[3]);
            int estado  = Integer.parseInt(partes[6]);
            boolean historico = Boolean.parseBoolean(partes[7]);
            // En caso de que no haga falta castear , no lo hago
            // y lo hago directamente desde el array resultatnte del split.
            Item item1 = new Item(id , partes[1] , partes [2] , precioInicio , partes[4] , partes[5] , estado , historico);

            mapaItems.put(id , item1);



        }
    }

    private static void iniMapaPujas() {
        // TODO 03 iniMapaPujas
        mapaUsuarios = new Map<Long, List<Puja>>() ;











        long contador = 0 ;

        for (String pujas : PUJAS){
            String[]partes = pujas.split(",");


            long idItem = Long.parseLong(partes[0]);
            int precioPujado = Integer.parseInt(partes[2]);
            Puja puja1 = new Puja(idItem , partes[1] , precioPujado , partes[3]);
            listaPujas.add(puja1);

            mapaPujas.put(contador , listaPujas);
            contador++;

        }

    }
    //endregion

    //region Usuarios
    public static boolean autenticar(String nombreUsuario, String password) {

        boolean usuarioCreado = false;


            if (mapaUsuarios.containsKey(nombreUsuario)){
                usuarioCreado = true;

            }else{ System.out.println("el usuario no esta en la base de datos");
                    return false;}



        if (usuarioCreado){
            Usuario usuario = mapaUsuarios.get(nombreUsuario);
           return Security.validateHashedPasswordSalt(password , usuario.getSal() , usuario.getHashPwSal() );
        }

//        return password.equals("1234");
        // TODO 04 autenticar
        return false;
    }

    public static boolean esAdmin(String nombreUsuario) {
//        return nombreUsuario.equalsIgnoreCase("Admin");
        // TODO 05 esAdmin
        boolean usuarioCreado = false;
        if (mapaUsuarios.containsKey(nombreUsuario)){
            usuarioCreado = true;
        }

        if (usuarioCreado){
            Usuario usuario = mapaUsuarios.get(nombreUsuario);
            if (usuario.getRol().equalsIgnoreCase("Admin")){
                return true;
            }
        }
        return false;
    }

    public static List<Usuario> obtenerUsuarios() {

        ArrayList<Usuario> usuarios = new ArrayList<>();


        for (int i = 0 ; i < mapaUsuarios.size() ; i++){
            Usuario usuario = mapaUsuarios.get(i);
            usuarios.add(usuario);


        }
        System.out.println(usuarios);
        // TODO 06 obtenerUsuarios
        return null;
    }

    public static boolean crearUsuario(String nombre, String password, boolean esAdmin) {

        boolean nombreIgual = false;
        for (Usuario user : mapaUsuarios.values()){

            if (user.getNombre().equalsIgnoreCase(nombre)){
                nombreIgual = true;
                break;
            }

        }

        if (nombreIgual){

        String saltUsuario = Security.generateSalt();
        String hashSaltUsuario = Security.hash(password);
        String rolUsuario = "";

        if (esAdmin){
            rolUsuario = "Admin";
            return true;

        }else  rolUsuario = "user";
        Usuario usuario = new Usuario( nombre , saltUsuario , hashSaltUsuario , rolUsuario );
        mapaUsuarios.put(nombre,usuario);
        return true;
        }


        // TODO 07 crearUsuario
        return false;
    }

    public static boolean modificarPasswordUsuario(String nombre, String password) {
        boolean existeUsuario = false;

        for (Usuario usuario : mapaUsuarios.values()){

            if (usuario.getNombre().equalsIgnoreCase(nombre)){
                existeUsuario = true;
                break;
            }
        }

        if (existeUsuario){
            String contrasenia = (mapaUsuarios.get(nombre).getSal() + password);
            String contraseniaHash = Security.hash(contrasenia);
            mapaUsuarios.get(nombre).setHashPwSal(contraseniaHash);
            return true;



        }


        // TODO 08 modificarPasswordUsuario
        return false;


    }

    public static boolean modificarRolUsuario(String nombre, String rol) {
        boolean existeUsuario = false;

        for (Usuario usuario : mapaUsuarios.values()){

            if (usuario.getNombre().equalsIgnoreCase(nombre)){
                existeUsuario = true;
                break;
            }
        }
        if (existeUsuario){
            mapaUsuarios.get(nombre).setRol("Admin");
            return true;
        }



        // TODO 09 modificarRolUsuario
        return false;
    }

    public static boolean eliminarUsuario(String nombre) {
        // TODO 10 eliminarUsuario

        if (mapaUsuarios.get(nombre)!= null){
            mapaUsuarios.remove(mapaUsuarios.get(nombre));
            return true;
        }
        return false;
    }

    //endregion

    //region Validación de artículos
    public static List<Item> obtenerArticulosPendientes() {
        // TODO 11 obtenerArticulosPendientes
        return null;
    }

    public static boolean validarArticulo(long id, boolean valido) {
        // TODO 12 validarArticulo
        return false;
    }

    public static boolean validarTodos() {
        // TODO 13 validarTodos
        return true;
    }
    //endregion

    //region Gestión de artículos y pujas de administrador
    public static List<ItemPujas> obtenerArticulosConPujas() {
        // TODO 14 obtenerArticulosConPujas
        return null;
    }

    public static boolean resetearSubasta() {
        // TODO 15 resetearSubasta
        return true;
    }

    public static List<PujaItem> obtenerHistoricoGanadores() {
        // TODO 16 obtenerHistoricoGanadores
        return null;
    }
    //endregion

    //region Acciones por parte de usuario normal (no admin)

    public static Item obtenerArticuloPujable(long idArt) {
        // TODO 17 obtenerArticuloPujable
        return null;
    }

    public static List<Item> obtenerArticulosPujables() {
        // TODO 18 obtenerArticulosPujables
        return null;
    }

    public static boolean pujarArticulo(long idArt, String nombre, int precio) {
        // TODO 19 pujarArticulo
        return false;
    }

    public static List<PujaItem> obtenerPujasVigentesUsuario(String nombreUsuario) {
        // TODO 20 obtenerPujasVigentesUsuario
        return null;
    }

    public static boolean ofrecerArticulo(Item item) {
        // TODO 21 ofrecerArticulo
        return true;
    }

    //endregion
}
