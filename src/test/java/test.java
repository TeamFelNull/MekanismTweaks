import dev.felnull.mekanismtweaks.Utils;

import java.util.Scanner;

public class test {
    public static void main(String[] args){
        var sc = new Scanner(System.in);
        while(true)
            System.out.println(Utils.exponential(sc.nextDouble()));
    }
}
