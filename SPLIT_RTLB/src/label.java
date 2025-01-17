import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the notion of label used for the shortest path algorithm
 */
public class label {
	int a;
	int b;
	List<Integer> c;
	public label(int a, int b, List<Integer> c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public List<Integer> getC() {
		return c;
	}
	public void setC(List<Integer> c) {
		this.c = c;
	}
	/**
	 * Checks if the label is dominated by the label given in argument or equivalent to it
	 * @param l
	 * label
	 * @return
	 * Returns true if the label is dominated by l or equivalent to it
	 */
	boolean isDominatedorequivalent(List<label> l){
		boolean res = false;
		for (label lb : l ) {
			if (((a > lb.getA() )&& (b >= lb.getB() ))  || ((a >= lb.getA() )&& (b > lb.getB() )) ) {
				res = true;
				break;
			}
			if ((a == lb.getA() )&& (b == lb.getB()))  {
				res = true;
				break;
			}
		}
		return res;
	}
	/**
	 * Checks if the label is dominated by the label given in argument
	 * @param lb
	 * label
	 * @return
	 * Returns true if the label is dominated by lb
	 */
	boolean isDominated(label lb){
		boolean res = false;
		if (((a > lb.getA() )&& (b >= lb.getB() ))  || ((a >= lb.getA() )&& (b > lb.getB() )) ) {
			res = true;
		}
		return res;
	}

	/**
	 * propagate a label of node i throw an arc of weight cost
	 * @param cost
	 * weight of the arc 
	 * @param i
	 * node 
	 * @return
	 * resulted label of the propagation
	 */
	label propagate (int cost,int i) {
		List<Integer> nc = new ArrayList<Integer> ();
		//System.out.println(c);
		nc.addAll(c);
		nc.add(i);
		label res = new label (a+cost, b+1, nc );
		return res;	
	}
	@Override
	public String toString() {
		return "label [a=" + a + ", b=" + b + ", c=" + c + "]";
	}
}
