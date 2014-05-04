import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Decoder {

    static int b = 32; //num of remaining being used 8,16,32
    static int L;
    //Lowest point of current range
    static int R; // magnitude
    static int V;
    static double bitsOutstanding;
    static int _2b1;
    static int _2b2;
    static int l = 0;
    static int h = 0;
    static int t = 256;
    static List<Integer> finalValue = new ArrayList<Integer>();
    static DataOutputStream out = null;
    static DataInputStream stream = null;
    static int[] frequency = new int[256];
    static int[] range = new int[256];
    static int[] weight = new int[258];

    static int remaining = 0;
    static int byte1 = 0;
    static boolean DEBUG = true;


    private static void init() {
        _2b1 = (int) Math.pow(2, (b - 1));
        _2b2 = (int) Math.pow(2, (b - 2));
        R = _2b1;
        L = 0;
        V = 0;
        bitsOutstanding = 0;

        int index = 0;
        for (int i = 0; i < frequency.length; i++) {
            frequency[i] = 1;
            weight[i] = index;
            index += 1;
        }
    }

    private static void input() {
        try {
            File file = new File("src/finalValue.txt"); //replace with arg?
            stream = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        try {
        int nRead;


        if (stream != null) {
//                while ((nRead = stream.readByte()) >= 0) {
//                    encode(nRead);

            for (int i = 0; i < b; i++) {
                int bit = getBit();
                V = (V * 2) + bit;
            }

            decode(V);
//                }
        }

        //}
//        catch (EOFException e) {
//            try {
//                stream.close();
//                System.out.println("Closing");
//
//
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    static int getBit() {
        if (remaining == 0) {
            try {
                byte1 = stream.read();
                remaining = 8;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        remaining -= 1;

        return (byte1 >> remaining) & 1;
    }

    static void decode(int b) {

        while (true) {
            for (int i = 0; i < range.length - 1; i++) {
                int T = (R * range[i]) / t;
                range[i] = L + T;

            }

/*
            int character =

                    rangeOf(b);
*/


//            if(frequency[b]){
//
//            }


            if (b == 256) {
                break;
            }
            try {
                out.write((int) b);
            } catch (IOException e) {
                e.printStackTrace();
            }

            V = (R * l) / t;
            L = L + V;
            R = ((R * h) / t) - V;

            if (R <= _2b2) {
//                normalise();
            }

            t = t + 1;
        }
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

        if (DEBUG) {
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

                out.writeBytes((char) b + "");
//                out.write(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finalValue.clear();
        }
    }


    public static void main(String[] args) {
        init();

        try {
//            System.setOut(new PrintStream(new DataOutputStream(new
//                    FileOutputStream(args [0] + ".txt"))));

            out = new DataOutputStream(new
                    FileOutputStream("src/finalValue_decode" + ".txt"));

            input();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}





