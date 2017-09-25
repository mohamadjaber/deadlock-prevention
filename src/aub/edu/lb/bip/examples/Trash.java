package aub.edu.lb.bip.examples;

public class Trash {
	
	public static void main(String[] args) {
		int N = 5000; 
		int split = 200;
		for(int i = 0 ; i < N; i++) {
			System.out.print(" f" + i  + " p"+ i);
			if((i+1)%split == 0) {
				System.out.print(" f" + (i+1)%N);
				System.out.println(); 
			}
			
		}
	}

}
