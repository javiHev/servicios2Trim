package org.example.correoelectronico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Datos {
    List<Usuario> listaUsuarios = Arrays.asList(
            new Usuario("Juan", "123"),
            new Usuario("Jaime", "321"),
            new Usuario("Javi", "200"),
            new Usuario("Manu", "abc")
    );


    // Obtener la lista de usuarios
    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    // Agregar un nuevo usuario
    public void agregarUsuario(Usuario usuario) {
        listaUsuarios.add(usuario);
    }

    // Buscar un usuario por nombre
    public Usuario buscarUsuarioPorNombre(String nombre) {
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getUserName().equals(nombre)) {
                return usuario;
            }
        }
        return null; // O manejarlo de otra manera, como lanzar una excepción
    }

    // Verificar si un usuario existe
    public boolean usuarioExiste(String nombre) {
        return listaUsuarios.stream().anyMatch(usuario -> usuario.getUserName().equals(nombre));
    }

    // Eliminar un usuario
    public boolean eliminarUsuario(String nombre) {
        return listaUsuarios.removeIf(usuario -> usuario.getUserName().equals(nombre));
    }

    // Actualizar la información de un usuario
    public void actualizarUsuario(String nombre, String nuevaContraseña) {
        Optional<Usuario> usuario = listaUsuarios.stream()
                .filter(u -> u.getUserName().equals(nombre))
                .findFirst();

        usuario.ifPresent(u -> u.setContraseña(nuevaContraseña));
    }
}

