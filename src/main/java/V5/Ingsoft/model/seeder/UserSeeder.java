package V5.Ingsoft.model.seeder;

import V5.Ingsoft.controller.Controller;

public class UserSeeder implements Seedable{
    public static final int NCON = 3;
    public static final int NVOL = 5;

    @Override
    public void seed(Controller c) {
        for (int i = 1; i <= NCON; i++)
            c.interpreter(String.format("add -c config%d pass%dC", i, i));

        for (int i = 1; i <= NVOL; i++)
            c.interpreter(String.format("add -v volont%d pass%dV", i, i));

        c.interpreter("logout");
        c.interpreter("login fruit1 pass1F pass1F");
        c.interpreter("logout");
        c.interpreter("login fruit2 pass2F pass2F");
        c.interpreter("logout");
        c.interpreter("login fruit3 pass3F pass3F");
        c.interpreter("logout");
        c.interpreter("login ADMIN PASSWORD");
    }
    
}
