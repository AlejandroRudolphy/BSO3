import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Idea {
	private Problem instance = Problem.getInstance();
	protected final int type = instance.type; 
	private final int id = instance.id;
	protected final int nVariables = instance.dimension[type][id]; 
	
	private int[] x = new int[nVariables];
	
	public Idea() {
	    for (int i = 0; i < nVariables; i++) {
	        x[i] = StdRandom.uniform(2);  
	    }
	}
	
	protected boolean isBetterThan(Idea g){
	  return fitness() > g.fitness();
	}
	
	protected float fitness() { 
	  float beneficio = 0;
	  for(int i = 0; i < nVariables; i++){
	    beneficio += x[i] * instance.prices[type][id][i];
	  }
	  return beneficio;
	}
	
	protected boolean isFeasible() {
	  	return type == 0 ? isFeasibleCapability() : isFeasibleMultidimensionalCapability();
	}
	
	protected boolean isFeasibleCapability() {
	  int suma = 0;
	  for(int i = 0; i < nVariables; i++){
	    suma += x[i] * instance.weights[type][id][0][i];
	  }
	  return suma <= instance.capacity[type][id][0];
	}
	
	private boolean isFeasibleMultidimensionalCapability() {
	  int j = 0;
	  int suma;
	  boolean F = true;
	  while(j < instance.backpacks[type][id] && F){ 
	    suma = 0; 
	    for(int i = 0; i < nVariables; i++){
	      suma += x[i] * instance.weights[type][id][j][i];
	    }
	    F = suma <= instance.capacity[type][id][j];
	    j++;
	 }
	 return F;
	}
	
	protected void repare() {
	  //System.out.println(Arrays.toString(x));
	  List<Integer> values = new ArrayList<Integer>();
	   for(int i = 0; i < nVariables; i++){
	    if (x[i] == 1){
	      values.add(i);
	    }
	  }
	  do{
	    Random generator = new Random();
	    int randomIndex = values.get(generator.nextInt(values.size()));  
	    for(int j = 0; j < instance.backpacks[type][id]; j++){
	        //System.out.printf("Reemplazando el 1 en la posición %s de %s\n",randomIndex,values);
	        x[randomIndex] = 0;
	        for(int i = 0; i < values.size(); i++){
	          if(values.get(i) == randomIndex){
	            values.remove(i);
	          }
	        }
	        break;
	    }
	    //System.out.printf("Nuevo valor: %s\n",Arrays.toString(x));
	  } while(!isFeasible());
	    //System.out.println("Idea factible\n");
	        values = null;
	    }
	
	protected void replaceCenter(){
	  if(fitness() == optimal()) {
		  return;
	  }
      List<Integer> values = new ArrayList<Integer>();
      for(int i = 0; i < nVariables; i++){
        if (x[i] == 0){
          values.add(i);
        }
      }
      do{
        Random generator = new Random();
        int randomIndex = values.get(generator.nextInt(values.size()));
        //System.out.printf("Hola %s\n", Arrays.toString(x));
        for(int j = 0; j < instance.backpacks[type][id]; j++){
          x[randomIndex] = 1;
          for(int k = 0; k < values.size(); k++){
            if(values.get(k) == randomIndex){
              values.remove(k);
            }
          }
          if(!isFeasible()) {
        	  repare();
          }
          //System.out.printf("Chao %s\n", Arrays.toString(x));
          break;
        }
    
      }while(!isFeasible());
   	   //System.out.println("Idea factible\n");
       values = null;
    }
	  /*protected void move(Idea g, float w, float c1, float c2) {
	for (int j = 0; j < nVariables; j++) {
	      /* update velocity */
	      //v[j] = v[j] * w
	              //+ c1 * StdRandom.uniform() * (pBest[j] - x[j])
	      //        + c2 * StdRandom.uniform() * (g.x[j] - x[j]);
	      /* update position */
	    //  x[j] = toBinary(x[j] + v[j]);
	  //}
	//}
	
	protected float optimal() {
		return instance.optimal[type][id];
	}
	
	protected float diff() {
		return optimal() - fitness();
	}
	
	protected float rpd() {
		return diff() / optimal() * 100f;
	}
	
	protected void copy(Object object)  {
	    if (object instanceof Idea) {
	        System.arraycopy(((Idea) object).x, 0, this.x, 0, nVariables);
	    }
	}
	
	private int toBinary(double x) {
	    return StdRandom.uniform() <= (1 / (1 + Math.pow(Math.E, -x))) ? 1 : 0;
	}
	
	@Override
	public String toString() {
		return String.format(
			"optimal value: %.1f, fitness: %.1f, diff: %s, rpd: %.2f%%, x: %s", 
			optimal(), 
			fitness(), 
			diff(),
			rpd(),
			java.util.Arrays.toString(x));
	}
}
