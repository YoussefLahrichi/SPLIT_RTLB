import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides the algorithmic components of the split algorithm
 * Here the operations are numbered from 1 to n (0 fictitious vertex)
 * Notice that we do not delete arcs violating the constraints but we weigh them to 100000 which is equivalent
 */
public class Split {
	/**
	 * Number of operations, the graph contains n vertices + an additional fictitious vertex 
	 */
	int n;
	/**
	 * c[i][j] defines the weight of the arc from i to j
	 */
	int [][] c;
	/**
	 * labels.get(i) defines the list of the labels of vertex i
	 */
	List<List<label>> labels;
	/**
	 * Maximum number of workstations
	 */
	int smax;
	/**
	 * The giant sequence for which the graph is constructed
	 */
	List<Integer> seq;
	/**
	 * The RTLB instance for which the graph is constructed
	 */
	RTLB instance ;
	/**
	 * This constructor initializes the object based on an RTLB instance and a giant sequence
	 * @param instance0
	 * RTLB instance
	 * @param seq
	 * Giant Sequence
	 */

	public Split (RTLB instance0 , List<Integer> seq) {
		instance = instance0;
		raz( seq);
	}
	/**
	 * This constructor initializes the object based an RTLB instance 
	 * @param instance0
	 * RTLB instance
	 */
	public Split (RTLB instance0 ) {
		n = instance0.getN();
		instance = instance0;
	}
	/**
	 * This method reset the class with a different giant sequence
	 * It computes arcs weights and weigh arcs violating the constraints to 100000
	 * @param seq0
	 * Giant sequence
	 */
	public void raz ( List<Integer> seq0) {
		seq = seq0;
		n = instance.getN();
		smax = instance.getSmax();
		labels = new ArrayList<List<label>>() ;
		labels.add(new ArrayList<label>());
		List<Integer> l0 = new ArrayList<Integer>();
		l0.add(0);
		labels.get(0).add(new label (0,0,l0));
		for (int i = 1; i <(n+1); i++) {
			labels.add(new ArrayList<label>());
			labels.get(i).add(new label (100000,100000,null));
		}
		c = new int [n][n+1];
		for (int i = 0; i<n; i++ ) {
			for (int j = i+1; j<(n+1); j++ ) {
				double an = 0;
				for(int k = i +1 ; k < (j+1) ; k++) {
					an = an +instance.getT()[seq.get(k-1)];
				}
				double bn = 0;
				for(int k = i +1 ; k < j ; k++) {
					bn = bn +instance.getTt()[seq.get(k-1)][seq.get(k)];
				}
				double cn = 0;
				if (i<j-1) {
					cn = instance.getTt()[seq.get(j-1)][seq.get(i)];
				}
				c[i][j] = (int) Math.ceil((an+bn+cn)/(instance.getC())) ;
			}
		}
		for (int i = 0; i<n; i++ ) {
			for (int j = i+1; j<(n+1); j++ ) {
				if (c[i][j]>instance.getMp()) {
					c[i][j] = 100000;
				}
				if (j-i>instance.getM()) {
					c[i][j] = 100000;
				}
				for(List<Integer> SE : instance.getE()) {
					int resSE = 0;
					for (int op = i+1; op <(j+1); op++) {
						if (SE.contains(seq.get(op-1))) {resSE = resSE +1;}
					}
					if (resSE == SE.size()) {
						c[i][j] = 100000;
					}
				}
				if(!instance.getA().isEmpty()){
					List<Integer> inter = new ArrayList<Integer>();
					inter.addAll(instance.getA().get(seq.get(i)));
					iii:	for(int ki = i+1; ki<(j+1);ki++ ) {
						List<Integer> inter2 = instance.getA().get(seq.get(ki-1));
						inter.retainAll(inter2);
						if (inter.isEmpty()) {
							c[i][j] = 100000;
							break iii;
						}
					}
				}
				for(Couple I : instance.getI()) {
					boolean inter1 = true;
					boolean inter2 = true;
					for (int op = i+1; op <(j+1); op++) {
						if (seq.get(op-1) == I.min()) 
							inter1 = !inter1;
						if (seq.get(op-1)  == I.max())
							inter2 = !inter2;
					}
					if ((!inter1&&inter2)||(inter1&&!inter2)) {
						c[i][j] = 100000;
					}
				}
			}
		}
	}
	/**
	 * This method reset the class with a different giant sequence, obtained after applying insertion move
	 * It only applies necessary changes
	 * Operation at position kii is to be inserted at position kjj
	 * @param seq0
	 * Giant sequence
	 * @param kii
	 * Source position
	 * @param kjj
	 * Destination position
     * @param cres
	 * Initial weight matrix
	 */
	public void raza ( List<Integer> seq0,int kii, int kjj, int[][] cres) {
		for (int i = 0; i<n; i++ ) {
			for (int j = i+1; j<(n+1); j++ ) {
				c[i][j]=cres[i][j];
			}
		}
		seq = seq0;
		n = instance.getN();
		smax = instance.getSmax();
		labels = new ArrayList<List<label>>() ;
		labels.add(new ArrayList<label>());
		List<Integer> l0 = new ArrayList<Integer>();
		l0.add(0);
		labels.get(0).add(new label (0,0,l0));
		for (int i = 1; i <(n+1); i++) {
			labels.add(new ArrayList<label>());
			labels.get(i).add(new label (100000,100000,null));
		}
		for (int i = kii+1; i<=(kjj+1); i++ ) {
			for (int j = kii+1; j<=(kjj); j++ ) {
				if(i<j) c[i][j] = c[i+1][j+1];	
			}
			for (int j = kjj+1; j<(n+1); j++ ) {
				if(i<j) {
					double an = 0;
					for(int k = i +1 ; k < (j+1) ; k++) {
						an = an +instance.getT()[seq.get(k-1)];
					}
					double bn = 0;
					for(int k = i +1 ; k < j ; k++) {
						bn = bn +instance.getTt()[seq.get(k-1)][seq.get(k)];
					}
					double cn = 0;
					if (i<j-1) cn = instance.getTt()[seq.get(j-1)][seq.get(i)];				
					c[i][j] = (int) Math.ceil((an+bn+cn)/(instance.getC())) ;
					if (c[i][j]>instance.getMp()) c[i][j] = 100000;
					if (j-i>instance.getM()) c[i][j] = 100000;
					if(!instance.getA().isEmpty()){
						List<Integer> inter = new ArrayList<Integer>();
						inter.addAll(instance.getA().get(seq.get(i)));
						iii:	for(int ki = i+1; ki<(j+1);ki++ ) {
							List<Integer> inter2 = instance.getA().get(seq.get(ki-1));
							inter.retainAll(inter2);
							if (inter.isEmpty()) {
								c[i][j] = 100000;
								break iii;
							}}}}}}
		for (int i = 0; i<=kii; i++ ) {
			for (int j = kii+1; j<(n+1); j++ ) {
				if(i<j) {
					double an = 0;
					for(int k = i +1 ; k < (j+1) ; k++) {
						an = an +instance.getT()[seq.get(k-1)];
					}
					double bn = 0;
					for(int k = i +1 ; k < j ; k++) {
						bn = bn +instance.getTt()[seq.get(k-1)][seq.get(k)];
					}
					double cn = 0;
					if (i<j-1) cn = instance.getTt()[seq.get(j-1)][seq.get(i)];
					c[i][j] = (int) Math.ceil((an+bn+cn)/(instance.getC())) ;
					if (c[i][j]>instance.getMp()) c[i][j] = 100000;
					if (j-i>instance.getM()) 	c[i][j] = 100000;					
					if(!instance.getA().isEmpty()){
						List<Integer> inter = new ArrayList<Integer>();
						inter.addAll(instance.getA().get(seq.get(i)));
						iii:	for(int ki = i+1; ki<(j+1);ki++ ) {
							List<Integer> inter2 = instance.getA().get(seq.get(ki-1));
							inter.retainAll(inter2);
							if (inter.isEmpty()) {
								c[i][j] = 100000;
								break iii;
							}}}}}}
		for(Couple I : instance.getI()) {
			int aabis = seq.indexOf(I.min())+1;
			int bbbis = seq.indexOf(I.max())+1;
			int aa =-1;
			int bb =-1;
			if (aabis<bbbis) { aa = aabis; 
			bb = bbbis;
			}
			else{ aa = bbbis; 
			bb = aabis;
			}
			for (int i = 0; i<aa; i++ ) {
				for (int j = aa; j<bb; j++ ) {
					if (i<j) c[i][j] = 100000;
				}}
			for (int i = aa; i<bb; i++ ) {
				for (int j = bb; j<(n+1); j++ ) {
					if (i<j) c[i][j] = 100000;
				}}}
		for(List<Integer> SE : instance.getE()) {
			List<Integer> ops = new ArrayList<Integer>();
			for (int op : SE) {
				ops.add(seq.indexOf(op)+1);
			}
			int aa =Collections.min(ops);
			int bb =Collections.max(ops);
			for (int i = 0; i<aa; i++ ) {
				for (int j = bb; j<(n+1); j++ ) {
					if (i<j) c[i][j] = 100000;

				}}}}
	/**
	 * This method reset the class with a different giant sequence, obtained after applying insertion move
	 * It only applies necessary changes
	 * Operation at position kjj is to be inserted at position kii
	 * @param seq0
	 * Giant sequence
	 * @param kii
	 * Source position
	 * @param kjj
	 * Destination position
	 * @param cres
	 * Initial weight matrix
	 */

	public void razb ( List<Integer> seq0,int kii, int kjj, int[][] cres) {
		for (int i = 0; i<n; i++ ) {
			for (int j = i+1; j<(n+1); j++ ) {
				c[i][j]=cres[i][j];
			}
		}
		seq = new ArrayList<Integer>();
		seq.addAll(seq0);
		n = instance.getN();
		smax = instance.getSmax();
		labels = new ArrayList<List<label>>() ;
		labels.add(new ArrayList<label>());
		List<Integer> l0 = new ArrayList<Integer>();
		l0.add(0);
		labels.get(0).add(new label (0,0,l0));
		for (int i = 1; i <(n+1); i++) {
			labels.add(new ArrayList<label>());
			labels.get(i).add(new label (100000,100000,null));
		}
		for (int i = kjj; i>kii; i-- ) {
			for (int j = (kjj+1); j>kii+1; j-- ) {
				if(i<j) c[i][j] = c[i-1][j-1];	
			}
			for (int j = kjj+2; j<(n+1); j++ ) {
				if(i<j) {
					double an = 0;
					for(int k = i +1 ; k < (j+1) ; k++) {
						an = an +instance.getT()[seq.get(k-1)];
					}
					double bn = 0;
					for(int k = i +1 ; k < j ; k++) {
						bn = bn +instance.getTt()[seq.get(k-1)][seq.get(k)];
					}
					double cn = 0;
					if (i<j-1) {
						cn = instance.getTt()[seq.get(j-1)][seq.get(i)];
					}
					c[i][j] = (int) Math.ceil((an+bn+cn)/(instance.getC())) ;
					if (c[i][j]>instance.getMp()) c[i][j] = 100000;
					if (j-i>instance.getM()) 	c[i][j] = 100000;
					if(!instance.getA().isEmpty()){
						List<Integer> inter = new ArrayList<Integer>();
						inter.addAll(instance.getA().get(seq.get(i)));
						iii:	for(int ki = i+1; ki<(j+1);ki++ ) {
							List<Integer> inter2 = instance.getA().get(seq.get(ki-1));
							inter.retainAll(inter2);
							if (inter.isEmpty()) {
								c[i][j] = 100000;
								break iii;
							}}}}}}
		for (int i = 0; i<=kii; i++ ) {
			for (int j = kii+1; j<(n+1); j++ ) {
				if(i<j) {
					double an = 0;
					for(int k = i +1 ; k < (j+1) ; k++) {
						an = an +instance.getT()[seq.get(k-1)];
					}
					double bn = 0;
					for(int k = i +1 ; k < j ; k++) {
						bn = bn +instance.getTt()[seq.get(k-1)][seq.get(k)];
					}
					double cn = 0;
					if (i<j-1) 	cn = instance.getTt()[seq.get(j-1)][seq.get(i)];
					c[i][j] = (int) Math.ceil((an+bn+cn)/(instance.getC())) ;
					if (c[i][j]>instance.getMp()) c[i][j] = 100000;					
					if (j-i>instance.getM()) c[i][j] = 100000;					
					if(!instance.getA().isEmpty()){
						List<Integer> inter = new ArrayList<Integer>();
						inter.addAll(instance.getA().get(seq.get(i)));
						iii:	for(int ki = i+1; ki<(j+1);ki++ ) {
							List<Integer> inter2 = instance.getA().get(seq.get(ki-1));
							inter.retainAll(inter2);
							if (inter.isEmpty()) {
								c[i][j] = 100000;
								break iii;
							}}}}}}
		for(Couple I : instance.getI()) {
			int aabis = seq.indexOf(I.min())+1;
			int bbbis = seq.indexOf(I.max())+1;
			int aa =-1;
			int bb =-1;
			if (aabis<bbbis) { aa = aabis; 
			bb = bbbis;
			}
			else{ aa = bbbis; 
			bb = aabis;
			}
			for (int i = 0; i<aa; i++ ) {
				for (int j = aa; j<bb; j++ ) {
					if (i<j) c[i][j] = 100000;
				}}
			for (int i = aa; i<bb; i++ ) {
				for (int j = bb; j<(n+1); j++ ) {
					if (i<j) 	c[i][j] = 100000;
				}}}
		for(List<Integer> SE : instance.getE()) {
			List<Integer> ops = new ArrayList<Integer>();
			for (int op : SE) {
				ops.add(seq.indexOf(op)+1);
			}
			int aa =Collections.min(ops);
			int bb =Collections.max(ops);
			for (int i = 0; i<aa; i++ ) {
				for (int j = bb; j<(n+1); j++ ) {
					if (i<j) c[i][j] = 100000;

				}}}}



	/**
	 * This method performs the split by propagating the labels in the appropriate order
	 */
	public void split () {
		for (int t = 0 ; t < n ; t++) {
			if(labels.get(t).get(0).getA()<100000){
				for (int i = t+1;i<(n+1); i++) {
					if(c[t][i]<100000){
						for (label lb : labels.get(t)) {
							if ((lb.getB() < smax-1  )|| (i ==n))    {
								if ( !lb.propagate(c[t][i], i).isDominatedorequivalent(labels.get(i)) )    {
									labels.get(i).add(lb.propagate(c[t][i], i));
									for (Iterator<label> iter = labels.get(i).listIterator(); iter.hasNext();) {
										label lbl = iter.next();
										if (lbl.isDominated(lb.propagate(c[t][i], i))) {
											iter.remove();
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	/**
	 * This method performs the split by propagating the labels in the appropriate order
	 * It does not necessarily respect the smax constraint
	 */
	public void split_unfeas () {
		for (int t = 0 ; t < n ; t++) {
			if(labels.get(t).get(0).getA()<100000){
				for (int i = t+1;i<(n+1); i++) {
					if(c[t][i]<100000){
						for (label lb : labels.get(t)) {
							if ( !lb.propagate(c[t][i], i).isDominatedorequivalent(labels.get(i)) )    {
								labels.get(i).add(lb.propagate(c[t][i], i));
								for (Iterator<label> iter = labels.get(i).listIterator(); iter.hasNext();) {
									label lbl = iter.next();
									if (lbl.isDominated(lb.propagate(c[t][i], i))) {
										iter.remove();
									}
								}
							}
						}
					}
				}
			}else {
			}

		}
	}
	/**
	 * Performs decoding to get a solution
	 * @return A feasible solution
	 */
	public Solution decode() {
		List<Integer> shorthest_path = null;
		int shorthestpathcost = 1000000; 
		for(label lb : labels.get(n)) {
			if (lb.getA()<shorthestpathcost){
				shorthestpathcost = lb.getA();
				shorthest_path = lb.getC();
			}
		}
		List<WSset> res = new ArrayList<WSset>();
		List<Integer> l  = new ArrayList<Integer>();
		if (shorthest_path  == null) {
			return null;
		}
		Collections.reverse(shorthest_path);
		l = shorthest_path;
		for (int i =  l.size()-1 ; i > 0; i--) {
			List<Integer> set = new ArrayList<Integer>() ;
			for (int h=  l.get(i)+1 ;  h <= l.get(i-1);h++) {
				int interme = seq.get(h-1);
				set.add(interme);
			} 
			int nb = c[l.get(i)][l.get(i-1)];
			res.add(new WSset(set,nb));
		}
		return new Solution(res);
	}
	/**
	 * Performs decoding to get a solution
	 * @return A solution not necessarily respect the smax constraint
	 */
	public Solution decode_unfeas() {
		List<Integer> shorthest_path = null;
		int shorthestpathcost = 1000000; 
		int nbWS= 1000000; 
		for(label lb : labels.get(n)) {
			if (lb.getB()<nbWS){
				shorthestpathcost = lb.getA();
				nbWS = lb.getB();
				shorthest_path = lb.getC();
			}
			if ((lb.getB()==nbWS)&&(lb.getA()<shorthestpathcost)){
				shorthestpathcost = lb.getA();
				nbWS = lb.getB();
				shorthest_path = lb.getC();
			}
		}
		if (nbWS<=smax) {
			shorthestpathcost = 1000000; 
			nbWS= 1000000; 
			for(label lb : labels.get(n)) {
				if ((lb.getA()<shorthestpathcost)&&((lb.getB()<=smax))){
					shorthestpathcost = lb.getA();
					nbWS = lb.getB();
					shorthest_path = lb.getC();
				}
			}
		}
		List<WSset> res = new ArrayList<WSset>();
		List<Integer> l  = new ArrayList<Integer>();
		if (shorthest_path  == null) {
			return null;
		}
		Collections.reverse(shorthest_path);
		l = shorthest_path;
		for (int i =  l.size()-1 ; i > 0; i--) {
			List<Integer> set = new ArrayList<Integer>() ;
			for (int h=  l.get(i)+1 ;  h <= l.get(i-1);h++) {
				set.add(seq.get(h-1));
			} 
			int nb = c[l.get(i)][l.get(i-1)];
			res.add(new WSset(set,nb));
		}
		return new Solution(res);
	}
	/**
	 * Performs the split and returns a solution
	 * * Here the operations are numbered from 0 to n-1 in seq2
	 * @param seq2
	 * sequence to split
	 * @return 
	 * Optimal split solution
	 */
	public Solution solve(List<Integer> seq2) {
		raz(seq2);
		split();
		return decode();
	}
	/**
	 * Performs the split and returns a solution
	 * Here the operations are numbered from 1 to n in seq2
	 * @param seq2
	 * sequence to split
	 * @return 
	 * Optimal split solution
	 */
	public Solution solveGS(List<Integer> seq2) {
	  for(int i = 0; i<seq2.size();i++) {
		  seq2.set(i, seq2.get(i)-1);
	  }
	  return solve(seq2);
	}
	/**
	 * Performs the split and returns a solution after applying an insertion move. 
	 * Operation at position i is to be inserted at position j.
	 * @param seq2
	 * sequence to split
	 * @param i
	 * Source position
	 * @param j
	 * Destination position
	 * @param cres
	 * Initial weight matrix 
	 * @return 
	 * Optimal split solution
	 */
	public Solution solve(List<Integer> seq2,int i,int j,int [][] cres) {
		if(i<j) {
			raza(seq2,i,j,cres);
		}else {
			razb(seq2,j,i,cres);
		}
		split();
		return decode();
	}
	/**
	 * Performs the split and returns a solution
	 * @param seq2
	 * sequence to split
	 * @return 
	 * Optimal split solution not necessarily respecting the smax constraint
	 */
	public Solution solve_unfeas(List<Integer> seq2) {
		raz(seq2);
		split_unfeas();
		return decode_unfeas();
	}
	/**
	 * Performs the split and returns a solution after applying an insertion move. 
	 * Operation at position i is to be inserted at position j.
	 * @param seq2
	 * sequence to split
	 * @param i
	 * Source position
	 * @param j
	 * Destination position
	 * @param cres
	 * Initial weight matrix 
	 * @return 
	 * Optimal split solution not necessarily respecting the smax constraint
	 */
	public Solution solve_unfeas(List<Integer> seq2,int i,int j,int [][] cres) {
		if(i<j) {
			raza(seq2,i,j,cres);
			//raz(seq2);
		}else {
			razb(seq2,j,i,cres);
			//raz(seq2);
		}
		split_unfeas();
		return decode();
	}	
	/**
	 * Getter for attribute c
	 * @return
	 * Weight matrix
	 */
	public int[][] getC() {
		int[][] cres = new int [n][n+1];
		for (int i = 0; i<n; i++ ) {
			for (int j = i+1; j<(n+1); j++ ) {
				cres[i][j]=c[i][j];
			}
		}
		return cres;
	}
	/**
	 * Setter for attribute c
	 * @return
	 * Weight matrix
	 */
	public void setC(int[][] c) {
		this.c = c;
	}
}
