package cpsc331.A1;

//Lachlan Moore - 30030228
//Nathanael Huh - 10128366
//Junehyuk Kim - 30020861
public class Hufflepuff {
       
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
            System.out.println(eval(result));
        }
        } catch (NumberFormatException e) {
            System.out.println("Silly muggle! One integer input is required.");
        } 
    }
    
    
    public static int eval(int n) {
     // Assertion: A non-negative integer n has been given as input.
        if (n == 0) {
            return 10;
        } else if (n == 1) {
            return 9;
        } else if (n == 2) {
            return 8;
        } else if (n == 3) {
            return 7;
        } else {
            int hocus = 10;
            int pocus = 9;
            int abra = 8;
            int kadabra = 7;
            int i = 3;
            while (i < n) {
                int shazam = 4 * kadabra - 6 * abra + 4 * pocus - hocus;
                hocus = pocus;
                pocus = abra;
                abra = kadabra;
                kadabra = shazam;
                i++;
            }
            return kadabra;
        }
     // Assertion:
     // 1. A non-negative integer n has been given as input.
     // 2. The nth Hufflepuff number Hn has been returned as output
    }
}
