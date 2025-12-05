import java.util.Scanner;
import java.util.Arrays;


public class matrix{
  public static void OnMult(int m_ar, int m_br) {    
    long Time1, Time2;
    //char[] st = new char[100];
    double temp;
    int i, j, k;

    double[] pha = new double[m_ar*m_ar];
    double[] phb = new double[m_ar*m_ar];
    double[] phc = new double[m_ar*m_ar];
    for(i=0; i<m_ar; i++)
        for(j=0; j<m_ar; j++)
            pha[i*m_ar + j] = (double)1.0;

    for(i=0; i<m_br; i++)
        for(j=0; j<m_br; j++)
            phb[i*m_br + j] = (double)(i+1);

    Time1 = System.currentTimeMillis();

    for(i=0; i<m_ar; i++)
    {    for( j=0; j<m_br; j++)
        {    temp = 0;
            for( k=0; k<m_ar; k++)
            {    
                temp += pha[i*m_ar+k] * phb[k*m_br+j];
            }
            phc[i*m_ar+j]=temp;
        }
    }

    Time2 = System.currentTimeMillis();
   
    //Obtains a clock that returns instants from the specified clock truncated to the nearest occurrence of the specified duration.
    double t = (Time2 - Time1)/1000;

    System.out.printf("Time: %3.3f seconds\n", t);

    // display 10 elements of the result matrix tto verify correctness
    System.out.println("Result matrix: ");
    for(i=0; i<1; i++)
    {    for(j=0; j<Math.min(10,m_br); j++)
            System.out.println(phc[j] + " ");
    }
}

public static void OnMultLine(int m_ar, int m_br) {    
    long Time1, Time2;
    //char[] st = new char[100];
    double temp;
    int i, j, k;

    double[] pha = new double[m_ar*m_ar];
    double[] phb = new double[m_ar*m_ar];
    double[] phc = new double[m_ar*m_ar];
    for(i=0; i<m_ar; i++)
        for(j=0; j<m_ar; j++)
            pha[i*m_ar + j] = (double)1.0;

    for(i=0; i<m_br; i++)
        for(j=0; j<m_br; j++)
            phb[i*m_br + j] = (double)(i+1);

    Time1 = System.currentTimeMillis();

    for(i=0; i<m_ar; i++)
    {    for( j=0; j<m_ar; j++)
        {
            for( k=0; k<m_br; k++)
            {    
                phc[i*m_ar + j] += pha[i*m_ar + j] * phb[k*m_ar + k];
                
            }    
        }
    }
    Time2 = System.currentTimeMillis();
   
    //Obtains a clock that returns instants from the specified clock truncated to the nearest occurrence of the specified duration.
    double t = (double)(Time2 - Time1)/1000;

    System.out.printf("Time: %3.3f seconds\n", t);

    // display 10 elements of the result matrix tto verify correctness
    System.out.println("Result matrix: ");
    for(i=0; i<1; i++)
    {    for(j=0; j<Math.min(10,m_br); j++)
            System.out.println(phc[j] + " ");
    }
}
    public static void main(String args[]) {
        char c;
        int lin, col, blockSize;
        int op;
        Scanner in = new Scanner(System.in);
        op=1;
        do{
        System.out.println("1. Multiplication");
        System.out.println("2. Line Multiplication");
        System.out.println("3. Block Multiplication");
        System.out.println("Selection?: ");
        op = in.nextInt();

        System.out.println("Dimension: lins=cols ? ");
        lin = in.nextInt();
        col = lin;

        switch(op) {
            case 1:
                OnMult(lin, col);
                break;
            case 2:
                OnMultLine(lin, col);  
                break;
            case 3:
                System.out.println("Block Size? ");
                blockSize = in.nextInt();
            //    OnMultBlock(lin, col, blockSize);  
                break;
            default:
                break;
        }
        }while(op!=0);
        in.close();
    }
}
