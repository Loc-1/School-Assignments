package cpsc331.A1;

//Lachlan Moore - 30030228
//Nathanael Huh - 10128366
//Junehyuk Kim - 30020861
public class SHufflepuff {
    
    public static void main(String args[]) {
        String input = args[0];
        if (input.isEmpty()) {
            System.out.println("Silly muggle! One integer input is required.");
        }
        try {
        int result = Integer.parseInt(input);
        if (result < 0) {
            System.out.println("Silly muggle! The integer input cannot be negative.");
        } else {
            System.out.println(sHuff(result));
        }
        } catch (NumberFormatException e) {
            System.out.println("Silly muggle! One integer input is required.");
        } 
    }
    
    
    private static int sHuff (int n) {
     // Assertion: A non-negative integer n has been given as input.    
        if (n == 0) {
            return 10;
        } else if (n == 1) {
            return 9;
        } else if (n == 2) {
            return 8;
        } else if (n == 3) {
            return 7;
        } else if (n >= 4) {
            return 4 * sHuff(n - 1) - 6 * sHuff(n - 2) + 4 * sHuff(n-3) - sHuff(n-4);
         // Assertion:
         // 1. A non-negative integer n has been given as input.
         // 2. The nth Hufflepuff number Hn has been returned as output.
        } else  {
            throw new IllegalArgumentException();
        }
                
    }
    
    
}
