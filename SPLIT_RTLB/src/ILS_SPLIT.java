import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * This class provides static methods to perform the ILS, the local search, 
 * the perturbation, the methods to compute the initial solution and the perturbation
 */
public class ILS_SPLIT {
	/** performs the Iterated local Search (100 iterations of the local search) 
	 * @param instance 
	 * The considered RTLB instance
	 * @param seed
	 * Random seed
	 * @param sol
	 * Initial solution
	 * @param string
	 * Name of the file where to write the solution
	 * @param ils
	 * Number of iterated local searches
	 * @param ls
	 * Number of iterations in each local search
	 * @return
	 * RTLB solution
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static Solution ils(RTLB instance,int seed , Solution sol, String string,int ils, int ls) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(string, "UTF-8");
		Random rnd = new Random(seed);
		//	initial solution
		Solution best = new Solution(sol);
		System.out.println("Initial Solution Cost "+ best.cost());
		int iterILS = 0;
		if(sol.getL().size()>instance.getSmax()) {
			writer.println("Initial solution not feasible");
			writer.close();
			return null;
		}else {
			writer.println("Initial solution feasible");
			writer.println("Cost (Number of machines) of initial solution "+sol.cost());
			writer.println("Number of stations of initial solution "+sol.getL().size());
		}
		long startTime = System.nanoTime();
		
		while (iterILS<ils) {
			System.out.println("iterILS "+ iterILS);
			// local search 
			sol = ls(instance,rnd,sol,ls);
			if (sol.cost()<best.cost()) {
				best = new Solution(sol);
			}
			System.out.println("after local seach, cost:"+ sol.cost()+ " best cost: "+ best.cost());
			// perturbation
			sol = pertub1(instance,rnd,3,best);
			System.out.println("after perturbation, cost "+ sol.cost()+ " best cost "+ best.cost());
			iterILS++;
		}
		writer.println(ils+" iterated local searches of "+ls+" iterations each.");
		writer.println("Cost (Number of machines) "+best.cost());
		writer.println("Number of stations "+best.getL().size());
		writer.println("Time "+(System.nanoTime()-startTime)/1000000000.);
		for (int i = 0 ; i < best.getL().size(); i++) {
			writer.println();
			writer.println("Workstation number "+(i+1)+" contains the following operations "+ best.getL().get(i).getSetOp());
			writer.println("Workstation number "+(i+1)+" uses the following number of machines "+ best.getL().get(i).getNbM());
		}
		writer.close();
		return best;
	}
	/**
	 * Builds an initial solution thanks to method M1
	 * @param instance 
	 * The considered RTLB instance
	 * @param rnd
	 * Random seed
	 * @return
	 * RTLB solution
	 */
	public static Solution initialSolutionM1(RTLB instance, Random rnd) {
		Sequence s = new Sequence(instance,rnd);
		s.mor(instance,rnd);
		Split sp = new Split(instance,s.getSeq());
		Solution cs = sp.solve_unfeas(s.getSeq());
		if (cs.getL().size()>instance.getSmax()) {
			boolean aux = cs.repair(instance);
			while (aux) {
				aux = cs.repair(instance);
			}
		}
		if(cs.getL().size()>instance.getSmax()) {
			System.out.println("Initial solution not feasible");
		}else {
			System.out.println("Initial solution feasible");
		}
		return cs;
	}
	/**
	 * Builds an initial solution thanks to method M2
	 * @param instance 
	 * The considered RTLB instance
	 * @param rnd
	 * Random seed
	 * @param time
	 * Maximum number of seconds allowed for the method
	 * @return
	 * RTLB solution
	 */
	public static Solution initialSolutionM2(RTLB instance, Random rnd,int time) {	
		Sequence s = new Sequence(instance,rnd);
		s.mor(instance,rnd);
		Split sp = new Split(instance,s.getSeq());
		Solution cs = sp.solve_unfeas(s.getSeq());
		int [][] cres = sp.getC();
		s = cs.toSequence();
		long startTime = System.nanoTime();
		double endTime =  ((System.nanoTime() - startTime)/1000000000.);
		while((cs.getL().size()>instance.getSmax())&(endTime<time)) {
			int i = rnd.nextInt(s.getN());
			int ip = Sequence.p(i,instance,s); 
			int is = Sequence.s(i,instance,s); 
			if (is-ip>2) {
				int j = ip + 1 + rnd.nextInt(is-ip-1);
				if(i !=j) {
					Sequence voisin = s.insertion(i, j);
					Solution sVoisin =  sp.solve_unfeas(voisin.getSeq(),i,j,cres);			
					if ((sVoisin != null) && voisin.repects(instance)&& (((cs.getL().size()>=sVoisin.getL().size()))))   {
						cs = sVoisin;
						s = voisin;
						cres = sp.getC();
					}
				}
			}
			endTime =  ((System.nanoTime() - startTime)/1000000000.);
		}
		if(cs.getL().size()>instance.getSmax()) {
			System.out.println("Initial solution not feasible");
		}else {
			System.out.println("Initial solution feasible");
		}
		return cs;
	}
	/** Performs the local Search (1000 iterations) 
	 * @param instance 
	 * The considered RTLB instance
	 * @param rnd
	 * Random seed
	 * @param cs
	 * Initial solution
	 * @return
	 * RTLB solution
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static Solution ls(RTLB instance,Random rnd, Solution cs,int ls) {
		Sequence s = cs.toSequence();
		Split sp = new Split(instance,s.getSeq());
		int [][] cres = sp.getC();
		for(int kkk= 0;kkk<ls;kkk++) {
			int i = rnd.nextInt(s.getN());
			int ip = Sequence.p(i,instance,s); 
			int is = Sequence.s(i,instance,s); 
			if (is-ip>2) {
				int j = ip + 1 + rnd.nextInt(is-ip-1);
				if(i !=j) {
					Sequence voisin = s.insertion(i, j);
					Solution sVoisin =  sp.solve(voisin.getSeq(),i,j,cres);
					if ((sVoisin != null) && ((sVoisin.cost()<=cs.cost())))   {
						cs = sVoisin;
						s = voisin;		
						cres = sp.getC();
					}
				}
			}
		}
		return cs;
	}
	/**
	 * Performs perturbation which consists of a number of moves   
	 * @param instance 
	 * The considered RTLB instance
	 * @param rnd
	 * Random seed
	 * @param nb 
	 * The nuber of moves
	 * @param initSol
	 * Initial solution
	 * @return
	 */
	public static  Solution pertub1(RTLB instance, Random rnd , int nb, Solution initSol) {
		Sequence initSeq = initSol.toSequence();
		Solution res = new Solution(initSol);
		Split sp = new Split(instance,initSeq.getSeq());
		int k = 0;
		while(k<nb) {
			int i = rnd.nextInt(initSeq.getN());
			int ip = Sequence.p(i,instance,initSeq); 
			int is = Sequence.s(i,instance,initSeq); 
			if (is-ip>2) {
				int j = ip + 1 + rnd.nextInt(is-ip-1);
				if(i !=j) {
					Sequence voisin = initSeq.insertion(i, j);
					Solution sVoisin =  sp.solve(voisin.getSeq());
					if ((sVoisin != null)) {			
						k++;
						initSeq.setSeq(voisin.getSeq());
						res = new Solution(sVoisin);
					}			
				}
			}
		}
		return res;
	}
}
