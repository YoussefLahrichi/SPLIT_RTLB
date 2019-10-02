import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 * This class represents an instance of the RTLB problem
 */
public class RTLB {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		//Read an instance from a text file
		RTLB instance = new RTLB ("example.txt");
		//solve an instance optimally given a giant sequence
		Split sp = new Split(instance);
		List<Integer> GS = Arrays.asList(1,2,3,4,5,6,7); 
		Solution sol = sp.solveGS(GS);
		//Solve an instance thanks to Split-based ILS algorithm
		Random rnd = new Random(0);
		Solution init= ILS_SPLIT.initialSolutionM2(instance,rnd,100);
		Solution res =	ILS_SPLIT.ils(instance,0,init,"result.txt",50,100);
	}
	/**
	 * Number of operations
	 */
	private int n;
	/**
	 * Maximum number of stations
	 */
	private int smax;
	/**
	 * Precedence relatons
	 */
	private List<Couple> P ;
	/**
	 * Maximum number of machines/station
	 */
	private int Mp ;
	/**
	 * Maximum number of operations/station
	 */
	private int M ;
	/**
	 * Number of part-fixing positions
	 */
	private int Na ;
	/**
	 * Processing times
	 */
	private double[] t;
	/**
	 * Setup times
	 */
	private double[][]tt ;
	/**
	 * Cycle time
	 */
	private double C;
	/**
	 * Exclusion constraints
	 */
	private List<List<Integer>> E ;
	/**
	 * Inclusion constraints
	 */
	private List<Couple> I ;
	/**
	 * Accessibility constraints
	 */
	private List<List<Integer>> A ;
	/**
	 * Constructor from an input file
	 * @param fileName
	 * name of the file
	 */
	public RTLB(String fileName){
		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(fileName));
			this.n = Integer.parseInt(lines.get(0).substring(lines.get(0).indexOf("/")+1, lines.get(0).lastIndexOf("/")));
			this.smax = Integer.parseInt(lines.get(2).substring(lines.get(2).indexOf("/")+1, lines.get(2).lastIndexOf("/")));
			this.Mp = Integer.parseInt(lines.get(6).substring(lines.get(6).indexOf("/")+1, lines.get(6).lastIndexOf("/")));
			this.M = Integer.parseInt(lines.get(4).substring(lines.get(4).indexOf("/")+1, lines.get(4).lastIndexOf("/")));
			this.C = Double.parseDouble(lines.get(8).substring(lines.get(8).indexOf("/")+1, lines.get(8).lastIndexOf("/")));
			t = new double [n];
			tt = new double [n][n];
			P = new ArrayList<Couple>();
			for (int k = 11; k<11+n;k++){
				t[k-11] =  Double.parseDouble(lines.get(k).substring(lines.get(k).indexOf("=")+1).trim());
			}
			int ip = n+13;
			this.Na = Integer.parseInt(lines.get(ip).substring(lines.get(ip).indexOf("/")+1,lines.get(ip).lastIndexOf("/")));
			this.A = new ArrayList<List<Integer>>();
			ip = ip+1;
			for (int i = 0 ; i< n ; i++) {
				A.add(new ArrayList<Integer>());
			}
			while(lines.get(ip).indexOf("/")== -1){
				int i = Integer.parseInt(lines.get(ip).substring(0, lines.get(ip).lastIndexOf(".")).trim())-1;
				int pos = Integer.parseInt(lines.get(ip).substring( lines.get(ip).lastIndexOf(".")+1).trim())-1;
				A.get(i).add(pos);
				ip++;
			}
			ip = ip+3;
			while(lines.get(ip).indexOf("/")== -1){
				int i = Integer.parseInt(lines.get(ip).substring(0, lines.get(ip).lastIndexOf(".")).trim())-1;
				int pos = Integer.parseInt(lines.get(ip).substring( lines.get(ip).lastIndexOf(".")+1).trim())-1;
				P.add(new Couple (i, pos ) );
				ip++;
			}
			ip = ip +3;
			E = new ArrayList<List<Integer>>();
			I = new ArrayList<Couple>();
			while(lines.get(ip).indexOf("/")== -1){
				int i = Integer.parseInt(lines.get(ip).substring( 0,lines.get(ip).lastIndexOf(".")).trim()) ;
				List<Integer> L = new ArrayList<Integer>();
				while ((lines.get(ip).indexOf("/")== -1)&&( i == Integer.parseInt(lines.get(ip).substring( 0,lines.get(ip).lastIndexOf(".")).trim()))) {
					int pos = Integer.parseInt(lines.get(ip).substring( lines.get(ip).lastIndexOf(".")+1).trim())-1;
					L.add(pos);
					ip++;
				}
				for(int a = 1; a< L.size();a++) {
					I.add(new Couple (L.get(a-1),L.get(a) ) );
				}
			}
			ip = ip +3;
			while(lines.get(ip).indexOf("/")== -1){
				int i = Integer.parseInt(lines.get(ip).substring( 0,lines.get(ip).lastIndexOf(".")).trim()) ;
				List<Integer> L = new ArrayList<Integer>();
				while ((lines.get(ip).indexOf("/")== -1)&&( i == Integer.parseInt(lines.get(ip).substring( 0,lines.get(ip).lastIndexOf(".")).trim()))) {
					int pos = Integer.parseInt(lines.get(ip).substring( lines.get(ip).lastIndexOf(".")+1).trim())-1;
					L.add(pos);
					ip++;
				}

				E.add(L);

			}
			ip = ip +3;
			while(lines.get(ip).indexOf("/")== -1){
				int i = Integer.parseInt(lines.get(ip).substring(0, lines.get(ip).indexOf(".")).trim())-1;
				int j = Integer.parseInt(lines.get(ip).substring(lines.get(ip).indexOf(".")+1, lines.get(ip).lastIndexOf("=")).trim())-1;
				double pos = Double.parseDouble(lines.get(ip).substring( lines.get(ip).lastIndexOf("=")+1).trim());
				tt[i][j]= pos;
				ip++;
			}
			for (int i = 0; i<n; i++) {
				tt[i][i]=0.0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getN() {
		return n;
	}
	
	public void setN(int n) {
		this.n = n;
	}

	public int getSmax() {
		return smax;
	}

	public void setSmax(int smax) {
		this.smax = smax;
	}

	public List<Couple> getP() {
		return P;
	}

	public void setP(List<Couple> p) {
		P = p;
	}

	public int getMp() {
		return Mp;
	}

	public void setMp(int mp) {
		Mp = mp;
	}

	public int getM() {
		return M;
	}

	public void setM(int m) {
		M = m;
	}

	public double[] getT() {
		return t;
	}

	public void setT(double[] t) {
		this.t = t;
	}

	public double[][] getTt() {
		return tt;
	}

	public void setTt(double[][] tt) {
		this.tt = tt;
	}

	public void setTt(int i, int j, double x) {
		tt[i][j] = x;
	}

	public double getC() {
		return C;
	}


	public void setC(double c) {
		C = c;
	}

	public List<List<Integer>> getE() {
		return E;
	}

	public void setE(List<List<Integer>> e) {
		E = e;
	}

	public List<Couple> getI() {
		return I;
	}

	public void setI(List<Couple> i) {
		I = i;
	}

	public int getNa() {
		return Na;
	}

	public void setNa(int na) {
		Na = na;
	}

	public List<List<Integer>> getA() {
		return A;
	}

	public void setA(List<List<Integer>> a) {
		A = a;
	}
}
