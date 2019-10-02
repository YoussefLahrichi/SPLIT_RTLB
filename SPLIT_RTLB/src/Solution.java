import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.Serializable;

/**
 * This class represents a solution of the RTLB problem
 */
public class Solution implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * A solution is represented as a list of workstations
	 */
	List<WSset> l;	
	/**
	 * Default constructor
	 */
	public Solution() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * Constructor from an initial solution
	 * @param s
	 * Initial solution
	 */
	Solution (Solution s) {
		l = new ArrayList<WSset>();
		for (WSset ws : s.getL()) {
			WSset wsc = new WSset (ws); 
			l.add(wsc);
		}
	}
	/**
	 * Constructor from a list of workstations
	 *  @param l
	 *  List of workstations
	 */
	public Solution(List<WSset> l) {
		super();
		this.l = l;
	}
	/**
	 * Computes the number of machines of the solution
	 * @return
	 * Number of machines
	 */
	public int cost() {
		int cost = 0 ;
		for(WSset e : l) {
			cost = cost + e.getNbM();
		}
		return cost; 

	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Solution [l=" + l + "]";
	}

	/**
	 * This method partitions the operations according to inclusion and precedence constraints
	 * @param instance
	 * RTLB instance
	 * @param rnd
	 * Random seed
	 * @return
	 */
	List<List<Integer>> Partition (RTLB instance, Random rnd) {
		List<List<Integer>> res = new ArrayList<List<Integer>>();
		List<List<Integer>> res_aux = new ArrayList<List<Integer>>();
		for (int i = 0 ; i<instance.getN();i++) {
			if (notPart(i,res)) {
				List<Integer> iinc = Sequence.incwithsAll(i,instance);
				res.add(iinc);
			}
		}
		mergePrec(res,instance);
		for (int i =0 ;i<res.size();i++) {
			res_aux.add(rndOrder(res.get(i),rnd,instance));
		}
		return res_aux;
	}

	List <Integer> rndOrder(List<Integer> lll, Random rnd, RTLB instance) {
		List <Integer> seq = new ArrayList<Integer>();
		int n = instance.getN();
		int marked [] = new int [n];
		for(int i=0;i<n;i++) {
			marked[i] = 1;
		}
		for (int i : lll) {
			marked[i] = 0;
		}
		while (seq.size()<lll.size()) {
			int k = Sequence.rndprecLess(instance,marked,rnd);
			seq.add(k);
			marked[k] = 1;
		}
		return seq;
	}

	private void mergePrec(List<List<Integer>> res,RTLB instance) {
		for (int i = 0; i<res.size(); i++) {
			for (int j = 0; j<res.size(); j++) {
				if((i!=j)&&(prec(res.get(i),res.get(j),instance)&&prec(res.get(j),res.get(i),instance))) {
					res.get(i).addAll(res.get(j));
					res.remove(j);
				}
			}}
	}

	private boolean prec(List<Integer> right, List<Integer> list, RTLB instance) {
		for (int i : right) {
			for (int j : list) {
				for (Couple c : instance.getP()) {
					if ((c.getA()==i)&&(c.getB()==j)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean notPart(int i, List<List<Integer>> res) {
		boolean r = true;
		for(List<Integer> list : res) {
			if (list.contains(i)) {
				return false;
			}
		}
		return r;
	}
	
	private int smallWssucc(List<Integer> iinc, RTLB instance, Solution solution) {
		int res = solution.getL().size()-1;
		for (int i = solution.getL().size()-2; i>-1;i--) {
			ll : for (int j : solution.getL().get(i).getSet()) {
				for (int k : iinc) {
					if  (prec(k,j,instance)) {
						res =i;
						break ll;
					}
				}
			}
		}
		return res;
	}

	private int bigWsprec(List<Integer> iinc, RTLB instance, Solution solution) {
		int res = 0;
		for (int i = 1; i<solution.getL().size();i++) {
			ll : for (int j : solution.getL().get(i).getSet()) {
				for (int k : iinc) {
					if  (prec(j,k,instance)) {
						res =i;
						break ll;
					}
				}
			}
		}
		return res;
	}

	private boolean prec(int j, int k, RTLB instance) {
		boolean res = false;
		for (Couple c : instance.getP()) {
			if ((c.getA()==j) &&(c.getB()==k)) {
				res = true;
				break;
			}
		}
		return res;
	}

	private boolean exclusion(List<Integer> list, RTLB instance) {
		boolean res = false;
		for (List<Integer> c : instance.getE()) {
			if (list.containsAll(c)) {
				res = true;
				break;
			}		
		}
		return res;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((l == null) ? 0 : l.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Solution other = (Solution) obj;
		if (!(other.getL().size()==this.getL().size())) {
			return false;
		}
		if (other.getL().containsAll(this.getL())) {
			return true;
		}
		return false;
	}

	public boolean repair(RTLB instance) {
		boolean res = false; 
		for (int i = 0 ; i<this.getL().size();i++) {
			int WSs =i;
			int first = bigWsprec(this.getL().get(i).getSet(),instance, this);
			int last = smallWssucc(this.getL().get(i).getSet(),instance, this);
			if (last > first) {
				for (int WSd = first; WSd <(last+1);WSd++){
					if (WSd != WSs) {
						Solution neighbour = this.compatibleV1( WSs, WSd,  instance);
						if (neighbour != null) {
							this.setL(neighbour.getL());
							return true;
						}
					}
				}
			}
		}
		return res;
	}

	Solution compatibleV1(int WSs, int WSd, RTLB instance){
		List<Integer> iinc = this.getL().get(WSs).getSet();
		Solution res = new Solution (this);
		if (iinc.size()+ l.get(WSd).getSet().size() >instance.getM() ) return null;
		List<Integer> listEntiers = new ArrayList<Integer>();
		listEntiers.addAll(l.get(WSd).getSet());
		listEntiers.addAll(iinc);
		if (exclusion(listEntiers, instance)) return null;  
		if(!instance.getA().isEmpty()){
			List<Integer> inter = new ArrayList<Integer>();
			inter.addAll(instance.getA().get(iinc.get(0)));
			for(int ki = 1; ki<iinc.size();ki++ ) {
				List<Integer> inter2 = instance.getA().get(iinc.get(ki));
				inter.retainAll(inter2);
			}
			for(int ki : l.get(WSd).getSet()) {
				List<Integer> inter2 = instance.getA().get(ki);
				inter.retainAll(inter2);
			}
			if (inter.isEmpty()) 	return null;
		}
		List<Integer> ll = new ArrayList<Integer>();
		ll.addAll(iinc);
		ll.addAll( l.get(WSd).getSet());
		List <Integer> lopt =  TSP_Dynamic_Prec.opt(ll,instance);
		double cost = Scost(lopt,instance.getTt()) + ScostT(lopt,instance.getT());
		int nb = (int) Math.ceil((double)cost/instance.getC());	
		if (nb>instance.getMp()) return null;
		List<Integer> ll2 = new ArrayList<Integer>();
		ll2.addAll( l.get(WSs).getSet());
		ll2.removeAll(iinc);
		List <Integer> lopt2 =  TSP_Dynamic_Prec.opt(ll2,instance);
		cost = Scost(lopt2,instance.getTt()) + ScostT(lopt2,instance.getT());
		int nb2 = (int) Math.ceil((double)cost/instance.getC());	
		if (nb2>instance.getMp()) return null;
		res.getL().get(WSd).setSet(lopt);
		res.getL().get(WSd).setNbM(nb);
		if (!lopt2.isEmpty()) {
			res.getL().get(WSs).setSet(lopt2);
			res.getL().get(WSs).setNbM(nb2);	
		}
		else {
			res.getL().remove(WSs);
		}
		return res;
	}

	public Sequence toSequence() {
		List<Integer> res = new ArrayList<Integer>();
		for(WSset ws : l) {
			res.addAll(ws.getSet());
		}
		return new Sequence(res);
	}

	public List<Integer> toList() {
		List<Integer> res = new ArrayList<Integer>();
		for(WSset ws : l) {
			res.addAll(ws.getSet());
		}
		return res;
	}

	public List<WSset> getL() {
		return l;
	}

	public void setL(List<WSset> l) {
		this.l = l;
	}

	public static double Scost(List<Integer> set,double [][]tt) {
		if (set.isEmpty()) {return 0;}
		double res =  tt[set.get(set.size()-1)][set.get(0)];
		for (int i = 1; i< set.size();i++) {
			res = res + tt[set.get(i-1)][set.get(i)];  		
		}
		if (set.size()==1) {res = 0 ;}
		return res;
	}

	public static double ScostT(List<Integer> set,double []t) {
		double res =  0;
		for (int i : set) {
			res = res + t[i];  		
		}
		return res;
	}

}
