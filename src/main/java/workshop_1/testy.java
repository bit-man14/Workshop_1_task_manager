package workshop_1;

import java.util.Arrays;
import java.util.Random;

public class testy {
    
    public static void main(String[] args) {
       Random rnd=new Random();
       String str="To jest test";
       int min=1;
       int max=9;
       int n=10;//no of elements
       int[] tab=new int[n];
        for (int i = 0; i < tab.length; i++) {
            tab[i]=rnd.nextInt(max-min+1)+min;
            //System.out.println(rnd.nextDouble());
        }
        Arrays.sort(tab,0,tab.length/2);//sortuj pierwszą połowę
        Arrays.sort(tab,tab.length/2,tab.length);//sortuj drugą połowę
        System.out.println(Arrays.toString(tab));
        System.out.println((Arrays.toString(str.toCharArray())));
        
        
    }
}
