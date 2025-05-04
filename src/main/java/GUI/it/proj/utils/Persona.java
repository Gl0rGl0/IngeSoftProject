package GUI.it.proj.utils;

public class Persona {
    public String username;
    public String password;
    public String tipo;

    public Persona(String u, String p, String t) {
        this.username = u;
        this.password = p;
        this.tipo = t;
    }

    @Override
    public String toString() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return tipo;
    }

    public String getPassword() {
        return password;
    }
}
