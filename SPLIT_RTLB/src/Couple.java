
public class Couple {
	public int a;
	public int b;
	public Couple() {
		super();
	}
	
	public Couple(int a, int b) {
		super();
		this.a = a;
		this.b = b;
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

	public String toString() {
		return " ("+a+","+b+") ";
	}
	
	public int max() {
		if (a< b)
		 return b;
		else 
			return a;
		
	}
	
	public int min() {
		if (a< b)
		 return a;
		else 
			return b;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + a;
		result = prime * result + b;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Couple other = (Couple) obj;
		if (a != other.a)
			return false;
		if (b != other.b)
			return false;
		
		
		return true;
	}
	
	
}
