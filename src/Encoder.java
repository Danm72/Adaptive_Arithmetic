import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Encoder {

    static double b = 32; //num of remaining being used 8,16,32
    static double L;
    //Lowest point of current range
    static double R; // magnitude
    static double T;
    static double bitsOutstanding;
    static double _2b1;
    static double _2b2;
    static double l = 0;
    static double h = 0;
    static double t = 257;
    static List<Integer> finalValue = new ArrayList<Integer>();
    static DataOutputStream out = null;
    static int[] frequency = new int[257];
    static boolean DEBUG = true;

    private static void init() {
        _2b1 = Math.pow(2, (b - 1));
        _2b2 = Math.pow(2, (b - 2));
        R = _2b1;
        L = 0;
        bitsOutstanding = 0;

        for (int i = 0; i < frequency.length; i++) {
            frequency[i] = 1;
        }
    }

    private static void input() {
        DataInputStream stream = null;
        try {
            File file = new File("src/lorem.txt"); //replace with arg?
            stream = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            int nRead;

            if (stream != null) {
                while ((nRead = stream.readByte()) >= 0) {
                    encode(nRead);
                }
            }

        } catch (EOFException e) {
            try {
                stream.close();
                if(DEBUG)
                    System.out.println("Closing");

                encode(256); // EOF SYMBOL

                while (finalValue.size() > 0) {
                    output(0);
                }
                //ADD DEAD BITS

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void encode(int b) {

        rangeOf(b);

        T = (R * l) / t;
        L = L + T;
        R = ((R * h) / t) - T;

        if (R <= _2b2) {
            normalise();
        }

        t = t + 1;
    }


    static void normalise() {

        while (R <= _2b2) {

            if (L + R <= _2b1) {
                bitPlusFollows(0);
//                System.out.println("OUTPUT 0");

            } else if (_2b1 <= L) {
                bitPlusFollows(1);
                L = L - _2b1;
//                System.out.println("OUTPUT 1");

            } else {
                bitsOutstanding += 1;
                L = L - _2b2;
            }

            L = L * 2;
            R = R * 2;
        }
    }

    static void bitPlusFollows(int bit) {
        output(bit);
        while (0 < bitsOutstanding) {
            output(1 - bit);
            bitsOutstanding = bitsOutstanding - 1;
        }
    }

    static void rangeOf(int s) {
        String bin = Integer.toBinaryString(s);
        int binInt = Integer.parseInt(bin, 2);

        if(DEBUG) {
            System.out.println("");
            System.out.println("BIN:\t" + bin);
            System.out.println("ASCII:\t" + binInt);
        }

        int weight = 0;

        for (int i = 0; i < binInt; i++) {
            weight = weight + frequency[i];
        }

        l = weight; //set lower
        h = l + frequency[binInt]; //set range

        frequency[binInt] = frequency[binInt] + 1; //increase weight of recurring symbol

    }

    private static void output(double bytes) {
        finalValue.add((int) bytes);
        if (finalValue.size() == 8) {
            try {
                byte c = 0;
//                String s = "";
                for (Integer anOutput : finalValue) {
                    c <<= 1;
                    if (anOutput == 1) {
                        c |= Byte.parseByte("00000001", 2);
                    }
//                    s = s + anOutput;

                }
                int b = ((int) c & 0xFF);

//                out.writeBytes((char) b + "");
                out.write(b);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(finalValue.toString());
            finalValue.clear();
        }
    }

    public static void main(String[] args) {
        init();

        try {
//            System.setOut(new PrintStream(new DataOutputStream(new
//                    FileOutputStream(args [0] + ".txt"))));

            out = new DataOutputStream(new
                    FileOutputStream("src/finalValue" + ".txt"));
            input();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}





