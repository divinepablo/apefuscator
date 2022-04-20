public class Main {
    public static void main(String[] args) {
        int yes = 0;
        int no = 0;
        while (true) {
            yes++;
            no *= yes >> 1;
            print("Number +%s", yes);
            System.out.println("Number -" + no);
            if (yes == 1000000) {
                break;
            }
        }
    }

    public static void print(String message, Object... args) {
        String output = String.format(message, args);
        System.out.println(message);
    }
}
