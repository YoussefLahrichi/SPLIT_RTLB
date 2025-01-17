import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class provides algorithmic components for sequencing optimally a set of operations in a workstation by means of dynamic programming
 */
public class TSP_Dynamic_Prec {
	public static List<Integer> opt(List<Integer> l, RTLB instance) {
		List<Integer> res = new ArrayList<Integer>();
		if (l.size()<2) {
			res=l; 
		}
		else {
			int pl = precless(l,instance);	
			List<Integer> ll = new ArrayList<Integer>();
			ll.add(pl);
			for (int jj : l) {
				if (jj!=pl) {
					ll.add(jj);
				}
			}
			LinkedHashMap<HashSet<Integer>,HashMap<Integer,Integer> > Bmap = subsets(ll.size());
			double [][] c = new double [ll.size()][ll.size()];
			for (int i = 0; i <ll.size(); i++) {
				for(int j= 0; j <ll.size(); j++) {
					c[i][j] = instance.getTt()[ll.get(i)][ll.get(j)];
				}
			}
			List<Integer> inter = proc(Bmap,c,instance,ll);
			for(int i= 0; i< inter.size()-1 ; i++) {
				res.add(ll.get(inter.get(i)-1));
			}		
		}
		return res;
	}
	private static int precless(List<Integer> l, RTLB instance) {
		int res = -1 ;
		boolean kkk = false;
		for (int i :l) {
			if (kkk) {
				return res;
			}
			kkk=true;
			res=i;
			kk:			for (int j : l) {
				if (i !=j) {
					for (Couple c : instance.getP()) {
						if((c.getA() == j)&& (c.getB() == i)){
							kkk = false;
							break kk;
						}
					}
				}
			}
		}
		return res;
	}
	public static List<Integer> proc (LinkedHashMap<HashSet<Integer>,HashMap<Integer,Integer> >  Bmap, double c[][],RTLB instance, List<Integer> ll) {
		Set<Entry<HashSet<Integer>, HashMap<Integer, Integer>>> setLhm = Bmap.entrySet();
		Iterator<Entry<HashSet<Integer>, HashMap<Integer, Integer>>> it2 = setLhm.iterator();
		it2.next().getValue().put(1, 0);
		Entry<HashSet<Integer>, HashMap<Integer, Integer>> s = null;
		while(it2.hasNext()){
			s = it2.next();
			Iterator<Integer> it =   s.getKey().iterator(); 
			it.next();	  
			while(it.hasNext()) {
				int  j = it.next();
				@SuppressWarnings("unchecked")
				HashSet<Integer> hs3 = (HashSet<Integer>) s.getKey().clone(); 
				if (succless (j,hs3,instance,ll)){
					hs3.remove(j);  
					int min = Integer.MAX_VALUE;
					Set<Entry<Integer, Integer>> setLhm3 = Bmap.get(hs3).entrySet();
					Iterator<Entry<Integer, Integer>> itkk = setLhm3.iterator();
					while(itkk.hasNext()) {
						int k = itkk.next().getKey();
						if((k!=j)){
							if (Bmap.get(hs3).get(k)+c[k-1][j-1]<min) {
								min = (int) (Bmap.get(hs3).get(k)+c[k-1][j-1]);}}}
					s.getValue().put(j,min);}
				else {
					s.getValue().put(j,Integer.MAX_VALUE);}}}
		List<Integer> res = new ArrayList<Integer>();
		Set<Entry<Integer, Integer>> setLhm3 = s.getValue().entrySet();
		Iterator<Entry<Integer, Integer>> itkk = setLhm3.iterator();
		int min = Integer.MAX_VALUE;
		int kmin = -1;
		while(itkk.hasNext()) {
			Entry<Integer, Integer> k = itkk.next();
			if (k.getValue()+c[k.getKey()-1][0]<min) {
				min =(int) (k.getValue()+c[k.getKey()-1][0]);
				kmin = k.getKey();}}
		res.add(1);
		res.add(kmin);
		@SuppressWarnings("unchecked")
		HashSet<Integer> scopy = (HashSet<Integer>) s.getKey().clone(); 
		while(kmin!=1) {
			scopy.remove(kmin);  
			setLhm3 =  Bmap.get(scopy).entrySet();
			itkk = setLhm3.iterator();
			min = Integer.MAX_VALUE;
			int kkmin = kmin;
			kmin = -1;
			while(itkk.hasNext()) {
				Entry<Integer, Integer> k = itkk.next();
				if (k.getValue()+c[k.getKey()-1][kkmin-1]<min) {
					min =(int) (k.getValue()+c[k.getKey()-1][kkmin-1]);
					kmin = k.getKey();
				}
			}
			res.add(kmin);
		}
		Collections.reverse(res);
		return res;
	}
	private static boolean succless(int j, HashSet<Integer> hs3, RTLB instance, List<Integer> ll) {
		// TODO Auto-generated method stub
		boolean res = true;
		ll: for (int i: hs3) {
			for (Couple c : instance.getP()) {
				if ((c.getA() == ll.get(j-1))&& (c.getB() == ll.get(i-1))) {
					res = false;
					break ll;
				}
			}
		}
		return res;
	}
	public static LinkedHashMap<HashSet<Integer>,HashMap<Integer,Integer> >  subsets(int n) {
		LinkedHashMap<HashSet<Integer>,HashMap<Integer,Integer> > Bmap = new LinkedHashMap<HashSet<Integer>,HashMap<Integer,Integer> > () ; 
		HashSet<Integer> a = new HashSet<Integer>();
		a.add(1);
		Bmap.put(a,new  HashMap<Integer,Integer>());
		for (int i = 2; i<=n ; i++) {
			Bmap.putAll(union(Bmap,i));
		}
		return Bmap;
	}
	private static LinkedHashMap< HashSet<Integer>, HashMap<Integer, Integer>> union(
			LinkedHashMap<HashSet<Integer>, HashMap<Integer, Integer>> bmap, int i) {
		LinkedHashMap< HashSet<Integer>, HashMap<Integer, Integer>> res = new LinkedHashMap< HashSet<Integer>, HashMap<Integer, Integer>> ();
		Set<Entry<HashSet<Integer>, HashMap<Integer, Integer>>> setLhm = bmap.entrySet();
		Iterator<Entry<HashSet<Integer>, HashMap<Integer, Integer>>> it2 = setLhm.iterator();
		while(it2.hasNext()){
			Entry<HashSet<Integer>, HashMap<Integer, Integer>> e = it2.next();
			HashSet<Integer> key = new HashSet<Integer>(e.getKey());
			key.add(i);
			res.put(key, new HashMap<Integer, Integer>());
		}
		return res;
	}
}
