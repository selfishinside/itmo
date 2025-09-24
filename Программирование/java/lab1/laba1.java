// math
// jdb :39 ^2
import static java.lang.Math.*;
public class laba1 {

	public static void main (String[] args) {
		
		short[] array_c = new short[12];  
		int hod_c = 0;    
		for (short l = 24; l >= 2; l -= 2) {                             
			array_c[hod_c] = l;
			hod_c++;                                                  
		}


		double[] array_x = new double[19];
		for (int o = 0; o < array_x.length; o++) {
    		array_x[o] = ((double)(random() * 16) - 10);
		}
		

		double[][] array_y = new double[12][19];

		for (int i = 0; i <= 11; i++) {
			for (int j = 0; j <= 18; j++){
				if (array_c[i] == 8) {
					array_y[i][j] = pow(((sin(log(abs(array_x[j])))-1)/1/2), atan(pow(E, (-1*abs(array_x[j])))));
				}
				else if (array_c[i] == 4 || array_c[i] == 12 || array_c[i] == 16 || array_c[i] == 18 || array_c[i] == 20 || array_c[i] == 22) {
					array_y[i][j] = asin((2*array_x[j]-5)/45);
				}
				else {
					array_y[i][j] = pow(PI,2) / pow (PI+tan(pow(array_x[j],cos(array_x[j]))),2);
				}
			}
		}
		for(double[] array : array_y){
			for(double element : array){
				System.out.printf(" " + "%6.2f", element);
            }
            System.out.println();
        }

        int[][] arr = new int[2][];
		arr[0] = new int[5];
		arr[1] = new int[4];
		System.out.println("arr[0]=" + arr[0].length);
		System.out.print("arr[1]=" + arr[1].length);
//1) echo -e "Main-class: laba1\n" > manifest.mf
//-cfe
	
	}

}