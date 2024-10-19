public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        // 1.1.1
        // a
        System.out.println("( 0 + 15 ) / 2 = " + ((0 + 15) / 2)); // 7
        // b 2.0e-6 = 0.000002
        System.out.println("2.0e-6 * 100000000.1 = " + (2.0e-6 * 100000000.1)); // 200.0000002
        // c
        System.out.println("true && false || true && true = " + (true && false || true && true)); // true

        // 1.1.2
        // a
        System.out.println("(1 + 2.236 ) / 2 = " + ((1 + 2.236) / 2)); // 1.618
        // b
        System.out.println("1 + 2 + 3 + 4.0 = " + (1 + 2 + 3 + 4.0)); // 10.0
        // c
        System.out.println("4.1 >= 4 = " + (4.1 >= 4)); // true
        // d
        System.out.println("1 + 2 + \"3\" = " + (1 + 2 + "3")); // 33

        // 1.1.6
        int f = 0;
        int g = 1;
        for (int i = 0; i <= 15; i++) {
            System.out.println("f = " + f);
            f = f + g;
            g = f - g;
        }

        // 1.1.7
        // a
        double t = 9.0;
        while (Math.abs(t - 9.0 / t) > .001)
            t = (9.0 / t + t) / 2.0;
        System.out.println("t = " + t);

        // b
        int sum = 0;
        for (int i = 1; i < 1000; i++)
            for (int j = 0; j < i; j++)
                sum++;
        System.out.println("sum = " + sum);

        // c
        int sum1 = 0;
        for (int i = 1; i < 1000; i *= 2)
            for (int j = 0; j < 1000; j++)
                sum1++;
        System.out.println("sum1 = " + sum1);

        // 1.1.8
        System.out.println("'b' = " + 'b');
        System.out.println("'b' +'c' = " + 'b' + 'c');
        System.out.println("'a' +4 = " + ((char) ('a' + 4)));

        // 1.1.9 write a code fragment that puts the binary representation of a positive integer N into a String s.
        int N = 2;
        String s = "";
        for (int n = N; n > 0; n /= 2) {
            s = (n % 2) + s;
        }
        System.out.println("s = " + s);


        // 1.1.12
        int[] a = new int[10];
        for (int i = 0; i < 10; i++) {
            a[i] = 9 - i;
        }
        for (int i = 0; i < 10; i++) {
            a[i] = a[a[i]];
        }
        for (int i = 0; i < 10; i++) {
            System.out.println("a[i] = " + a[i]);
        }

        // 1.1.16
        System.out.println("exR1 = " + exR1(6));
    }

    public static String exR1(int n) {
        if (n <= 0) return "";
        return exR1(n - 3) + n + exR1(n - 2) + n;
    }
}