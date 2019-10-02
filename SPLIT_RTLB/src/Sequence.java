import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents the notion of a giant sequence 
 */
public class Sequence {

	/**
	 * Sequence of operations of the giant sequence
	 */
	List <Integer> seq;
	/**
	 * Number of operations of the giant sequence
	 */
	int n;
	/**
	 * Default constructor
	 */
	public Sequence() {
		super();
	}
	/**
	 * Constructor from a sequence of operations
	 */
	public Sequence(List<Integer> seq1) {
		super();
		seq = new ArrayList<Integer>();
		for (int i:	seq1) {
			seq.add(i);
		}
		this.n = seq.size();
	}
	/**
	 * Constructor from a solution
	 */
	public Sequence(Solution s) {
		super();
		seq = new ArrayList<Integer>();
		for(WSset ws :s.getL()) {
			seq.addAll(ws.getSet());
		}
		n = seq.size();
	}
	/**
	 * Constructor from a giant sequence
	 */
	public Sequence(Sequence s) {
		super();
		n = s.n;
		seq  = new ArrayList<Integer> ();
		seq.addAll(s.getSeq());
	}
	/**
	 * Constructs a giant sequence respecting precedence constraints
	 * @param instance
	 * RTLB instance
	 * @param rnd
	 * Random seed
	 */
	public Sequence(RTLB instance, Random rnd) {
		super();
		seq = new ArrayList<Integer>();
		n = instance.getN();
		int marked [] = new int [n];
		for(int i=0;i<n;i++) {
			marked[i] = 0;
		}
		while (seq.size()<n) {
			int k = rndprecLess(instance,marked,rnd);
			seq.add(k);
			marked[k] = 1;
		}
	}

	private int rndprecLess(List<List<Integer>> parts, RTLB instance,Random rnd) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i<parts.size();i++) {
			if (!hasPrec(i,parts,instance)) {
				indexes.add(i);
			}
		}
		int index = rnd.nextInt(indexes.size());
		return indexes.get(index);
	}

	private boolean hasPrec(int i, List<List<Integer>> parts, RTLB instance) {
		for (int j = 0 ; j<parts.size();j++) {
			if (i!=j) {
				for (int k : parts.get(j)) {
					for (Couple c : instance.getP() ) {
						for (int ii : parts.get(i)) {
							if ((c.getA() ==k)&&(c.getB()==ii)) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	static int p(int i,RTLB instance,Sequence s) {
		int res = -1;
		if (i==0) return  -1;
		for (Couple c : instance.getP()) {
			if (c.getB() == s.getSeq().get(i)) {
				if (res < s.getIndex(c.getA())) {
					res = s.getIndex(c.getA());
				}
			}
		}
		return res;
	}

	static int s(int i,RTLB instance,Sequence s) {
		int res = instance.getN();
		if (i==instance.getN()-1) return  instance.getN();
		for (Couple c : instance.getP()) {
			if (c.getA() == s.getSeq().get(i)) {
				if (res > s.getIndex(c.getB())) {
					res = s.getIndex(c.getB());
				}
			}
		}
		// TODO Auto-generated method stub
		return res;
	}

	static List<Integer> incwiths(int i, RTLB instance) {
		List<Integer> res = new ArrayList<Integer>();
		for (Couple c : instance.getI()) {
			if (c.getA()==i) res.add(c.getB());
			if (c.getB()==i) res.add(c.getA());
		}
		return res;
	}

	static List<Integer> incwithsAll(int i, RTLB instance) {
		List<Integer> res = incwiths(i,instance);
		res.add(i);
		int ii = 0;
		while ((ii<res.size())) {
			List<Integer> res2 = incwiths(res.get(ii),instance);
			for (int op2 : res2) {
				if (!res.contains(op2)) {
					res.add(op2);
				}
			}
			ii++;
		}
		return res;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Sequence [seq=" + seq + ", n=" + n + ", getN()=" + getN() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	public List<Integer> getSeq() {
		return seq;
	}

	public void setSeq(List<Integer> seq) {
		this.seq = new ArrayList<Integer>();
		this.seq.addAll(seq);
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public void mor (RTLB instance, Random rnd ) { // macro operation randomized 
		seq = new ArrayList<Integer>();
		Solution sol = new Solution ();
		List<List<Integer>> parts = sol.Partition(instance,rnd);
		while(seq.size()<instance.getN()) {
			int index = rndprecLess(parts,instance,rnd);
			seq.addAll(parts.get(index))	;
			parts.remove(index);
		}
	}

	static int rndprecLess(RTLB instance, int[] marked,Random rnd) {
		List<Integer> resList = rndprecLess_aux(instance, marked);
		return 	resList.get(rnd.nextInt(resList.size()));
	}

	private static List<Integer> rndprecLess_aux(RTLB instance, int[] marked) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for(int i =0;i<instance.getN();i++) {
			int tmp = 0 ;
			if(marked[i]==0) {
				for(Couple c :instance.getP()) {
					if((c.getB() == i)&&(marked[c.getA()]==0)) {
						tmp = 1;
						break;
					}
				}
				if(tmp ==0) {
					res.add(i);
				}
			}
		}
		return res;
	}

	public Sequence echange (int i , int j) {
		List<Integer> cpy = new ArrayList<Integer>(this.getSeq());
		int temp = cpy.get(i);
		cpy.set(i, cpy.get(j));
		cpy.set(j, temp);
		return new Sequence(cpy);

	}

	public Sequence insertion (int i , int j) {
		List<Integer> cpy = new ArrayList<Integer>(this.getSeq());
		Sequence res = new Sequence(cpy);
		res.insertion_aux(i, j);
		return res;
	}

	public void insertion_aux(int i , int j) {
		if(i<j)
			insertionilj (i ,  j);
		if(j<i)
			insertionjli (i ,  j);
	}

	public void insertionilj (int i , int j) {
		int temp = seq.get(i);
		for (int k = i; k<j;k++) {
			seq.set(k, seq.get(k+1));
		}
		seq.set(j, temp);
	}

	public void insertionjli (int i , int j) {
		int temp = seq.get(i);
		for (int k = i; k>j;k--) {
			seq.set(k, seq.get(k-1));
		}
		seq.set(j, temp);
	}

	public Sequence inversion (int i , int j) {
		List<Integer> cpy = new ArrayList<Integer>(this.getSeq());
		while (i<j) {
			int temp = cpy.get(i);
			cpy.set(i, cpy.get(j));
			cpy.set(j, temp);
			i++;
			j--;
		}

		return new Sequence(cpy);
	}

	public boolean repects (RTLB instance) {
		boolean res = true;
		for(Couple c : instance.getP() ) {
			int a = getIndex(c.getA());
			int b = getIndex(c.getB());						
			if (((a == -1) &&(b!= -1 )) ){
				return false;
			}			
			if((a>b)&&(b!=-1)) {
				return false;
			}
		}
		return res;
	}

	private int getIndex(int a) {
		int res = -1;
		for(int i = 0; i< seq.size(); i++) {
			if(seq.get(i)==a) {
				res =i;
				break;
			}
		}
		return res;
	}
}
;