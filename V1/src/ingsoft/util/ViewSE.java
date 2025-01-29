package ingsoft.util;

import java.util.Scanner;

public class ViewSE {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void log(Object out){
        System.out.println(out);
    }

    public static String read(){
        return scanner.nextLine();
    }
}