import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a workstation
 * The methods in this class are classical JAVA functions such as constructors, getters,setters, hasCode and equals
 */
public class WSset implements Serializable{
	/**
	 * Subsequence of operations in the workstation
	 */
	List<Integer> set;
	/**
	 * Number of machines in the workstation
	 */
	int nbM;
	private static final long serialVersionUID = 1L;
	public WSset(List<Integer> set, int nbM) {
		super();
		this.set = set;
		this.nbM = nbM;
	}
	public WSset() {
		super();
		// TODO Auto-generated constructor stub
	}
	WSset(WSset ws) {
		nbM = ws.getNbM();
		set = new ArrayList<Integer>();
		set.addAll(ws.getSet());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nbM;
		result = prime * result + ((set == null) ? 0 : set.hashCode());
		return result;
	}
	public List<Integer> getSet() {
		return set;
	}
	public void setSet(List<Integer> set) {
		this.set = set;
	}
	public int getNbM() {
		return nbM;
	}
	public void setNbM(int nbM) {
		this.nbM = nbM;
	}
	public  String toString() {
		return set.toString()+" , "+nbM +" ; ";	

	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WSset other = (WSset) obj;
		if (nbM != other.nbM)
			return false;
		if (set.size() != other.getSet().size())
			return false;
		if (set == null) {
			if (other.set != null)
				return false;
		} else if (!set.containsAll(other.set))
			return false;
		return true;
	}
	public List<Integer> getSetOp() {
		List<Integer> setOp = new ArrayList<Integer>();
		for (int i : set) {
			setOp.add(i+1);
		}
		return setOp;
	}
}
