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
        mapaUsuarios = new HashMap<>();

        for (String usuario : USUARIOS){
            String[]partes = usuario.split(",");
            Usuario usuario1 = new Usuario(partes[0] , partes[1] , partes[2] , partes[3]);
            mapaUsuarios.put(partes[0] , usuario1);



        }
    }

    private static void iniMapaItems() {
        // TODO 02 iniMapaItems

        mapaItems = new HashMap<>();

        for (String  item : ITEMS){
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
        // Initialize the map
        mapaPujas = new HashMap<>();

        for (String puja : PUJAS) {
            String[] partes = puja.split(",");
            long idItem = Long.parseLong(partes[0]);
            int precioPujado = Integer.parseInt(partes[2]);

            Puja puja1 = new Puja(idItem, partes[1], precioPujado, partes[3]);

            List<Puja> listaDePujas = mapaPujas.get(idItem);
            if (listaDePujas == null) {
                listaDePujas = new ArrayList<>();
                mapaPujas.put(idItem, listaDePujas);
            }
            listaDePujas.add(puja1);
        }
    }
    //endregion

    //region Usuarios

    public static boolean autenticar(String nombreUsuario, String password) {
        //        return password.equals("1234");
        // TODO 04 autenticar


        if (!mapaUsuarios.containsKey(nombreUsuario)) {
          //  System.out.println("El usuario no está en la base de datos"); Puesto para veer que esta mal
            return false;
        }

        Usuario usuario = mapaUsuarios.get(nombreUsuario);
        //System.out.println("el usuario si que esta en la base de datos"); (Puesto para debuggear)

       // System.out.println(usuario.toString());


        //System.out.println(Security.validateHashedPasswordSalt(password, usuario.getSal(), usuario.getHashPwSal())) ;lo mismo, es porque me da error

        return Security.validateHashedPasswordSalt(password, usuario.getSal(), usuario.getHashPwSal());
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

        // TODO 07 crearUsuario

        ArrayList<Usuario> usuarios = new ArrayList<>();

        for (Usuario usuario : mapaUsuarios.values()){
            usuarios.add(usuario);
        }
        return usuarios;

    }

    public static boolean crearUsuario(String nombre, String password, boolean esAdmin) {

        if (mapaUsuarios.containsKey(nombre)){
            System.out.println("el usuario ya esta en la base de datos");
            return false;
        }

        String saltUsuario = Security.generateSalt();
        String hashedUsuario = Security.hash(  password + saltUsuario);

        String rol = "USER";

        if (esAdmin){
            rol = "ADMIN";
        }

        Usuario nuevoUsuario = new Usuario(nombre , saltUsuario , hashedUsuario , rol );

        mapaUsuarios.put(nombre , nuevoUsuario);

        return true;
    }

    public static boolean modificarPasswordUsuario(String nombre, String password) {


        if (!mapaUsuarios.containsKey(nombre)){

            System.out.println("el usuario no esta en la base de datos");
            return false;

        }
        String nuevaHashed = Security.hash(password+ mapaUsuarios.get(nombre).getSal());

        mapaUsuarios.get(nombre).setHashPwSal(nuevaHashed);

        // TODO 08 modificarPasswordUsuario
        return true;


    }

    public static boolean modificarRolUsuario(String nombre, String rol) {

        if (!mapaUsuarios.containsKey(nombre)){

            System.out.println("no existe tal usuario");
            return false;

        }

        if (rol.equalsIgnoreCase("ADMIN")  || rol.equalsIgnoreCase("USER")){
            mapaUsuarios.get(nombre).setRol(rol);
            return true;

        }
        System.out.println("El rol que has escrito esta mal, es o admin o user");
        return false;


        // TODO 09 modificarRolUsuario
    }

    public static boolean eliminarUsuario(String nombre) {
        // TODO 10 eliminarUsuario

        if (mapaUsuarios.containsKey(nombre)){
            mapaUsuarios.remove(nombre);
            return true;
        }
        return false;


    }

    //endregion

    //region Validación de artículos
    public static List<Item> obtenerArticulosPendientes() {

        ArrayList<Item> listaPendientes = new ArrayList<>();

        for (Item items : mapaItems.values()){
            if (items.getEstado() == EST_PENDIENTE){
                listaPendientes.add(items);
            }
        }

        // TODO 11 obtenerArticulosPendientes
        return listaPendientes;
    }

    public static boolean validarArticulo(long id, boolean valido) {
        // TODO 12 validarArticulo
        if (!mapaItems.containsKey(id)){
            System.out.println("el objeto no existe");
            return false;

        }

        if (valido){
            mapaItems.get(id).setEstado(EST_ACEPTADO);
        }else mapaItems.get(id).setEstado(EST_RECHAZADO);

        return true;

    }

    public static boolean validarTodos() {
        // TODO 13 validarTodos

        for (Item items : mapaItems.values()){
            items.setEstado(EST_ACEPTADO);
        }
        return true;
    }
    //endregion

    //region Gestión de artículos y pujas de administrador
    public static List<ItemPujas> obtenerArticulosConPujas() {
        // TODO 14 obtenerArticulosConPujas
        ArrayList<ItemPujas>  objetosPujados = new ArrayList<>();
        for (Map.Entry<Long, List<Puja>> puja  : mapaPujas.entrySet()){
            long itemID = puja.getKey();
            List<Puja> pujilla = puja.getValue();

            Item itemPujado = mapaItems.get(itemID);

            ItemPujas instancia = new ItemPujas(itemPujado , pujilla);

            objetosPujados.add(instancia);

        }
        return objetosPujados;
    }

    public static boolean resetearSubasta() {
        // TODO 15 resetearSubasta

        for (Long key : mapaPujas.keySet()){
            if (mapaItems.containsKey(key)){
                mapaItems.remove(key);
            }
        }

        mapaPujas.clear();

        return true;
    }

    public static List<PujaItem> obtenerHistoricoGanadores() {
        // TODO 16 obtenerHistoricoGanadores

        List<PujaItem> historial = new ArrayList<>();

        for (Map.Entry<Long, List<Puja>> entry : mapaPujas.entrySet()) {
            Long itemId = entry.getKey();
            List<Puja> pujas = entry.getValue();


            Item item = mapaItems.get(itemId);
            Puja pujaGanadora = new Puja();

            for (Puja puja : pujas){
                if (puja.getPrecioPujado() > pujaGanadora.getPrecioPujado()){
                    pujaGanadora.setPrecioPujado(puja.getPrecioPujado());
                    pujaGanadora.setNombreUsuario(puja.getNombreUsuario());
                    pujaGanadora.setIdItem(puja.getIdItem());
                }
            }


            if (pujaGanadora != null) {

                PujaItem pujaItem = new PujaItem(item.getId(), item.getNombre(), item.getPrecioInicio(), item.getUrlImagen(), item.getNombreUsuario(),pujaGanadora.getNombreUsuario(), pujaGanadora.getPrecioPujado(), pujaGanadora.getInstanteTiempo());
                historial.add(pujaItem);
            }
        }
        return historial;

    }
    //endregion

    //region Acciones por parte de usuario normal (no admin)

    public static Item obtenerArticuloPujable(long idArt) {
        // TODO 17 obtenerArticuloPujable

        if (mapaItems.containsKey(idArt)){

            if (mapaItems.get(idArt).getEstado() == EST_ACEPTADO && mapaItems.get(idArt).isHistorico() == false){
                return mapaItems.get(idArt);
            }
        }


        return null;
    }

    public static List<Item> obtenerArticulosPujables() {
        // TODO 18 obtenerArticulosPujables
        ArrayList<Item> listaPujables = new ArrayList<>();


        for (Item objeto : mapaItems.values()){
            if (obtenerArticuloPujable(objeto.getId()) != null){
                listaPujables.add(objeto);
            }

        }

        return listaPujables;
    }

    public static boolean pujarArticulo(long idArt, String nombre, int precio) {
        // TODO 19 pujarArticulo

        if (!mapaItems.containsKey(idArt)) {
            return false;
        }

        Item itemAPujar = mapaItems.get(idArt);


        if (precio < itemAPujar.getPrecioInicio()) {
            System.out.println("has pujado menos que el precio inicial");
            return false;
        }


        List<Puja> listaPujas = mapaPujas.get(idArt);


        if (listaPujas == null) {
            listaPujas = new ArrayList<>();
            mapaPujas.put(idArt, listaPujas);
        } else {

            int pujaMasAlta = 0;
            for (Puja puja : listaPujas) {
                if (puja.getPrecioPujado() > pujaMasAlta) {
                    pujaMasAlta = puja.getPrecioPujado();
                }
            }

            if (precio <= pujaMasAlta) {
                System.out.println("tu puja no es suficiente, ,");
                return false;
            }
        }


        System.out.println("registrando la puja...");
        Puja pujaUsuario = new Puja(idArt, nombre, precio, new Date().toString());
        listaPujas.add(pujaUsuario);
        return true;
    }

    public static List<PujaItem> obtenerPujasVigentesUsuario(String nombreUsuario) {
        // TODO 20 obtenerPujasVigentesUsuario

        if (!mapaUsuarios.containsKey(nombreUsuario)){
            return null;
        }
        List<PujaItem> pujasUsuario  = new ArrayList<>();

        for (List<Puja> pujillas : mapaPujas.values()){
            for (Puja puja : pujillas){
                if (puja.getNombreUsuario().equalsIgnoreCase(nombreUsuario)){
                    PujaItem puja1 = new PujaItem(puja.getIdItem() , mapaItems.get(puja.getIdItem()).getNombre() , mapaItems.get(puja.getIdItem()).getPrecioInicio() ,
                            mapaItems.get(puja.getIdItem()).getUrlImagen() , mapaItems.get(puja.getIdItem()).getNombreUsuario() , puja.getNombreUsuario() , puja.getPrecioPujado(), puja.getInstanteTiempo());
                    pujasUsuario.add(puja1);
                }
            }


        }
        return pujasUsuario;


    }

    public static boolean ofrecerArticulo(Item item) {
        // TODO 21 ofrecerArticulo
        long posicionNueva = 0 ;

        Set<Long> claves = mapaItems.keySet();
        for (Long clave : claves){
            if (clave > posicionNueva){
                posicionNueva = clave;
            }
        }

        mapaItems.put(posicionNueva, item);

        return true;
    }

    //endregion
}
