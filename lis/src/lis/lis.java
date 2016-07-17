package lis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Random;
import java.util.Scanner;


public class lis {
	void n() throws FileNotFoundException {
        File Fileright = new File("a.txt");

        PrintWriter pw = new PrintWriter(Fileright);

        Random random = new Random();
	   	
    	for (int i = 0; i < 20; i++) {
            int generatedNumber = random.nextInt(20);
    	
            pw.println(generatedNumber);
    	}
    	
        pw.close();
 }

		
    public static void main(String[] args) throws IOException {
    	
    	for(int i=0; i<2; i++ ){
    	
    	new lis().n();
    	         
    	    

        System.out.println("Longest Almost-Increasing Subsequenc.");

        if (args.length < 1 && args[0] != null)
            throw new RuntimeException("Unknown data inputFile name");

        File inputFile = new File(args[0]);


        System.out.println("Source data inputFile: " + inputFile);

        LinkedList<Long> sourceData = new LinkedList<>();

        try {
            Scanner scanner = new Scanner(inputFile);
            scanner.useDelimiter("[\\s,]+");
            while (scanner.hasNextLong()) {
                sourceData.add(scanner.nextLong());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        sourceData.forEach(System.out::println);

        if (sourceData.isEmpty())
            throw new RuntimeException("SourceData is empty.");

        long c;
        if (args.length > 1 && args[1] != null)
            c = Long.parseLong(args[1]);
        else
            c = 2;

        
        
        long[] laIS = new DataProcessor(sourceData, c).getLaIS();
        
        
        
        PrintWriter writer;
        
        try {
            if (args.length > 2 && args[2] != null) {
                File outputFile = new File(args[2]);
                writer = new PrintWriter(outputFile, "UTF-8");
        		
            } else {
                writer = new PrintWriter("output.txt", "UTF-8");
        		
            }
  
	        StringBuilder builder = new StringBuilder();
            for (long element : laIS) {
                builder.append(element).append(" ");
            }

            writer.println(builder.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        BufferedReader br = new BufferedReader(new FileReader("output.txt"));
        String line = null;
        while ((line = br.readLine()) != null) {
        	System.out.println("Longest almost increasing subsequence is: " + line );
        	System.out.println("******************************************************************");
        }
    }
   }
  public static class DataProcessor {

        /**
         * Resulted Longest Almost-Increasing Subsequence.
         */
        long[] lais;

        // We have to store multiple indexes for one 'x' value.
        private TreeMap<Long, Stack<Integer>> z;

        /**
         * When we add a new element 'x' in 'z' tree we store (in 'p' list)
         * index (the index in the original data) of an element that precedes
         * the element (that we add).
         */
        private List<Integer> p;

        private List<Long> data;
        private long c;

        public DataProcessor(List<Long> data, long c) {
            this.data = data;
            this.c = c;

            z = new TreeMap<>();
            p = new ArrayList<>(data.size());
        }

        public long[] getLaIS() {
            int n = data.size();

            for (int i = 0; i < n; ++i) {
                long xi = data.get(i);

                Map.Entry<Long, Stack<Integer>> pred = z.lowerEntry(xi);
                if (pred != null)
                    p.add(i, pred.getValue().get(0));// store index of predecessor of xi
                else
                    p.add(i, i);
                showMeZ(z);
                showMeP(p);
                
                // insert()
                // key: xi; value: i - index of xi;
                Stack<Integer> stack = z.get(xi);
                if (stack == null) {
                    stack = new Stack<>();
                    z.put(xi, stack);
                }
                stack.push(i);

                // delete()
                Map.Entry<Long, Stack<Integer>> s = z.ceilingEntry(xi + c);
                if (s != null) {
                    Stack<Integer> stack1 = s.getValue();
                    if (stack1.size() < 2)
                        z.remove(s.getKey());
                    else
                        stack1.pop();
                    
                }

            }
            

            // count length(LaIS)
            int l = countLaisLength(z);

            // m <- tail_node().index
            int m = z.lastEntry().getValue().get(0);
            long xm = data.get(m);

            lais = new long[l];
            // A position in LaIS to put element in it.
            int positionInLais = l;

            for (int i = n - 1; i > m; --i) {
                long xi = data.get(i);
                if (xm - c < xi && xi <= xm) {
                    lais[--positionInLais] = xi;
                }
            }

            lais[--positionInLais] = xm;
            
            int t = m;
            int pt = p.get(t);
            while (t != pt) {
                long xpt = data.get(pt);
                for (int i = t - 1; i > pt; --i) {
                    long xi = data.get(i);
                    if (xpt - c < xi && xi <= xpt) {
                        lais[--positionInLais] = xi;
                    }
                }
                lais[--positionInLais] = xpt;
                t = pt;
                pt = p.get(t);
            }
            
            showMeZ(z);
            showMeP(p);
            System.out.println("Lenght of LAIS is :" + l);
            return lais;
            
            
        }

        private int countLaisLength(TreeMap<Long, Stack<Integer>> z) {
            int l = 0;
            for (Map.Entry<Long, Stack<Integer>> zi : z.entrySet()) {
                l += zi.getValue().size();
            }
            
            return l;
        }

        private void showMeZ(TreeMap<Long, Stack<Integer>> z) {
            System.out.println("____ z tree: ____");
            for (Map.Entry<Long, Stack<Integer>> zi : z.entrySet()) {
                System.out.print(zi.getKey() + "  " + stackToString(zi.getValue())+ "   ");
            }
            
            System.out.println();
         }

        private String stackToString(Stack<Integer> stack) {
            int size = stack.size();
            StringBuilder builder = new StringBuilder("[");
            for (int j = 0; j < size; ++j) {
                if (j > 0)
                    builder.append(", ");
                builder.append(stack.get(j));
            }
            builder.append("]");
            return builder.toString();
        }

        private void showMeP(List<Integer> p) {
            System.out.println("____ p list: ____");
            for (int pi : p) {
                System.out.print(pi + " ");
            }
            System.out.println();
        }
    }
}